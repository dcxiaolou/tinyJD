package com.dcxiaolou.tinyJD.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.dcxiaolou.tinyJD.bean.OmsOrder;
import com.dcxiaolou.tinyJD.bean.PaymentInfo;
import com.dcxiaolou.tinyJD.payment.config.AlipayConfig;
import com.dcxiaolou.tinyJD.service.OrderService;
import com.dcxiaolou.tinyJD.service.PaymentService;
import com.dcxiaolou.tinyJD.annotations.LoginRequired;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {

    @Autowired
    private AlipayClient alipayClient;

    @Reference
    private PaymentService paymentService;

    @Reference
    private OrderService orderService;

    @RequestMapping("/payment")
    public String payment(String outTradeNo, String totalPrice, String itemNames, HttpServletRequest request, Model model) {
        String memberId = (String) request.getAttribute("memberId");
        String username = (String) request.getAttribute("username");

        model.addAttribute("outTradeNo", outTradeNo);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("itemNames", itemNames);

        return "payment";
    }

    @RequestMapping("/submit")
    @LoginRequired(loginSuccess = true)
    @ResponseBody
    public String submit(String outTradeNo, String totalPrice, String itemNames, HttpServletRequest request, Model model) {

        // 获取一个支付宝请求的客户端（并不是一个连接，而是一个封装好的http表单请求）
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

        // 回调函数
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);

        Map<String, String> map = new HashMap<>();
        map.put("out_trade_no", outTradeNo);
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        map.put("total_amount", /*totalPrice*/"0.01");
        map.put("subject", itemNames);

        String param = JSON.toJSONString(map);

        alipayRequest.setBizContent(param);

        String form = null;
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }

        OmsOrder omsOrder = orderService.getByOutTradeNo(outTradeNo);

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(omsOrder.getId());
        paymentInfo.setOrderSn(outTradeNo);
        paymentInfo.setPaymentStatus("1");
        paymentInfo.setSubject(itemNames);
        paymentInfo.setTotalAmount(totalPrice);

        // 生成并保存用户的支付信息
        paymentService.savePaymentInfo(paymentInfo);

        //向消息中间件发送一个检查支付状态（支付服务消费）的延迟消息队列，并控制检查次数
        paymentService.sendDelayPaymentResultCheckQueue(outTradeNo, 5);

        // 提交请求到支付宝
        return form;
    }

    @RequestMapping("/alipay/callback/return")
    @LoginRequired(loginSuccess = true)
    public String alipayCallbackReturn(HttpServletRequest request, Model model) {

        // 回调请求中获取支付宝参数
        String sign = request.getParameter("sign");
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_status = request.getParameter("trade_status");
        String total_amount = request.getParameter("total_amount");
        String subject = request.getParameter("subject");

        // 获取支付宝回调内容
        String call_back_content = request.getQueryString();

        // 通过支付宝的paramsMap进行签名验证，2.0版本的接口将paramsMap参数去掉，导致同步请求无法验证
        if (StringUtils.isNotBlank(sign)) {
            // 此处因为没有外部服务器，只能通过同步的sign验证
            //只要sign不为空，就认为验签成功
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOrderSn(trade_no);
            paymentInfo.setPaymentStatus("2");
            paymentInfo.setAlipayTradeNo(trade_no); // 支付宝的交易凭证号
            paymentInfo.setCallbackContent(call_back_content); // 回调请求字符串
            paymentInfo.setCallbackTime(new Date());

            // 更新用户的支付状态
            paymentService.updatePayment(paymentInfo);
        }

        // 支付成功，引起的系统服务-》订单服务的更新-》库存服务的更新-》物流服务的更新

        return "success";

    }

}
