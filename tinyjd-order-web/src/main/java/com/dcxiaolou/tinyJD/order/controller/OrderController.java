package com.dcxiaolou.tinyJD.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dcxiaolou.tinyJD.bean.OmsCartItem;
import com.dcxiaolou.tinyJD.bean.OmsOrder;
import com.dcxiaolou.tinyJD.bean.OmsOrderItem;
import com.dcxiaolou.tinyJD.bean.UmsMemberReceiveAddress;
import com.dcxiaolou.tinyJD.service.CartService;
import com.dcxiaolou.tinyJD.service.OrderService;
import com.dcxiaolou.tinyJD.service.PmsSkuInfoService;
import com.dcxiaolou.tinyJD.service.UserService;
import com.dcxiaolou.tinyJD.annotations.LoginRequired;
import com.dcxiaolou.tinyJD.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    private UserService userService;

    @Reference
    private CartService cartService;

    @Reference
    private OrderService orderService;

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @RequestMapping("/toTrade")
    @LoginRequired(loginSuccess = true) //必须是登录成功后
    public String trade(HttpServletRequest request, Model model) {
        String memberId = request.getAttribute("memberId").toString();
        String username = request.getAttribute("username").toString();
        System.out.println("trade " + memberId + " " + username);
        //获取收件人地址
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = userService.getReceiverAddressByMemberId(memberId);

        //将购物车集合转换为页面计算清单集合
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);

        // 判断cookie中是否包含商品
        String cookieValue = CookieUtil.getCookieValue(request, "cartListCookie", true);
        if (StringUtils.isNotBlank(cookieValue)) {
            List<OmsCartItem> omsCartItemsFromCookie = JSON.parseArray(cookieValue, OmsCartItem.class);
            if (omsCartItems != null) {
                omsCartItems.addAll(omsCartItemsFromCookie);
            } else {
                omsCartItems = omsCartItemsFromCookie;
            }
        }

        System.out.println("omsCartItems.size() = " + omsCartItems.size());
        List<OmsOrderItem> omsOrderItems = new ArrayList<>();
        double totalPrice = 0.0;
        for (OmsCartItem omsCartItem : omsCartItems) {
            if (omsCartItem.getIsChecked().equals("1")) {
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductId(omsCartItem.getProductId());
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductQuantity("" + omsCartItem.getQuantity());
                omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                omsOrderItem.setProductPrice(omsCartItem.getPrice());
                omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                omsOrderItems.add(omsOrderItem);
                totalPrice += Double.parseDouble(omsCartItem.getPrice());
            }
        }

        System.out.println("omsOrderItems.size() = " + omsOrderItems.size());

        model.addAttribute("umsMemberReceiveAddressList", umsMemberReceiveAddressList);
        model.addAttribute("username", username);
        model.addAttribute("omsOrderItems", omsOrderItems);
        model.addAttribute("totalPrice", totalPrice);

        //生成交易码，为了在提交订单时做交易码的验证，防止重复提交订单
        String tradeCode = orderService.createTradeCode(memberId);

        model.addAttribute("tradeCode", tradeCode);

        return "order-cart";
    }

    @RequestMapping("/submitOrder")
    @LoginRequired(loginSuccess = true)
    public ModelAndView submitOrder(String receiveAddressId, String tradeCode, HttpServletRequest request, HttpServletResponse response, Model model) {
        String memberId = (String) request.getAttribute("memberId");
        String username = (String) request.getAttribute("username");

        //检查交易码
        String success = orderService.checkTradeCode(memberId, tradeCode);
        if ("success".equals(success)) {
            //根据用户id获取要购买的商品列表（购物车）和总价格
            double totalPrice = 0.0;
            String itemNames = "";
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            //订单对象
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setAutoConfigDay("3"); //收货后过几天自动确认收货
            omsOrder.setCreateTime(new Date());
            omsOrder.setMemberId(memberId);
            omsOrder.setMemberUsername(username);
            omsOrder.setNote("快点发货");

            //生成外部订单号
            String outTradeNo = "gmall" + System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
            outTradeNo += sdf.format(new Date());

            omsOrder.setOrderSn(outTradeNo); //外部订单号


            List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    //获取订单详情
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    totalPrice += Double.parseDouble(omsCartItem.getPrice());
                    itemNames += omsCartItem.getProductName() + "\t" + omsCartItem.getQuantity() + " × " + omsCartItem.getPrice() + "\n";
                    //检查价格，数据库中的价格可能与页面显示的价格不同
                    boolean b = pmsSkuInfoService.checkPrice(omsCartItem.getProductSkuId(), omsCartItem.getPrice());
                    if (b == false) {
                        ModelAndView modelAndView = new ModelAndView("/error/exception");
                        modelAndView.addObject("message", "订单提交失败，请不要重复提交订单！");
                        return modelAndView;
                    }
                    //检验库存，远程调用库存系统
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductName(omsCartItem.getProductName());

                    omsOrderItem.setOrderSn(outTradeNo); //外部订单号，用来与外部系统进行交互，防止重复
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity().toString());
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductSkuCode("111111");
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSn("仓库商品");


                    omsOrderItems.add(omsOrderItem);
                }
            }

            omsOrder.setOmsOrderItems(omsOrderItems);
            omsOrder.setPayAmount("" + totalPrice);
            omsOrder.setOrderType("1");
            UmsMemberReceiveAddress umsMemberReceiveAddress = userService.getReceiverAddressById(receiveAddressId);
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetail_address());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhone_number());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPost_code());
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());
            //当前日期加一天
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            omsOrder.setReceiverTime(calendar.getTime());
            omsOrder.setSourceType("0");
            omsOrder.setStatus("0");
            omsOrder.setTotalAmount("" + totalPrice);

            //将订单和订单详情写入数据库
            //删除购物车中对应的商品
            orderService.saveOrder(omsOrder);

            //清空cookie
            CookieUtil.deleteCookie(request, response, "cartListCookie");

            //重定向到支付系统
            ModelAndView modelAndView = new ModelAndView("redirect:http://payment.gmall.com:8087/payment");
            modelAndView.addObject("outTradeNo", outTradeNo);
            modelAndView.addObject("totalPrice", totalPrice);
            modelAndView.addObject("itemNames", itemNames);
            return modelAndView;

        } else {
            ModelAndView modelAndView = new ModelAndView("/error/exception");
            modelAndView.addObject("message", "订单提交失败，请不要重复提交订单！");
            return modelAndView;
        }

    }

}
