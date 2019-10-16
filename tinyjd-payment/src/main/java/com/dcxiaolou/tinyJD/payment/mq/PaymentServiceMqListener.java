package com.dcxiaolou.tinyJD.payment.mq;

import com.dcxiaolou.tinyJD.bean.PaymentInfo;
import com.dcxiaolou.tinyJD.service.PaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Date;
import java.util.Map;

@Component
public class PaymentServiceMqListener {

    @Autowired
    private PaymentService paymentService;

    @JmsListener(destination = "PAYMENT_CHECK_QUEUE", containerFactory = "jmsQueueListener")
    public void consumerPaymentCheckResult(MapMessage mapMessage) throws JMSException {
        String out_trade_no = mapMessage.getString("out_trade_no");
        int count = Integer.parseInt(mapMessage.getString("count"));
        // 调用paymentService的支付宝检查接口
        Map<String, Object> resultMap = paymentService.checkAlipayPayment(out_trade_no);

        if (resultMap != null && !resultMap.isEmpty()) {
            Object trade_status = resultMap.get("trade_status");

            //根据查询的支付状态结果，判断是否进行下一次的延迟任务还是支付成功更新数据
            if (StringUtils.isNotBlank(out_trade_no) && trade_status.equals("TRADE_SUCCESS")) {
                //支付成功，更新支付发送支付队列

                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setOrderSn(out_trade_no);
                paymentInfo.setPaymentStatus("2");
                paymentInfo.setAlipayTradeNo(resultMap.get("trade_no").toString()); // 支付宝的交易凭证号
                paymentInfo.setCallbackContent(resultMap.get("call_back_content").toString()); // 回调请求字符串
                paymentInfo.setCallbackTime(new Date());

                return ;
            }
        }
        //继续发送延迟检查任务，计算延迟时间等
        if (count > 0) {
            count--; //控制检查次数
            paymentService.sendDelayPaymentResultCheckQueue(out_trade_no, count);
        } else {
            System.out.println("检查次数用尽，停止检查，支付失败");
        }
    }

}
