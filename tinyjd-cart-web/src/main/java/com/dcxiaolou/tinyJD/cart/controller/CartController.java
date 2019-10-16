package com.dcxiaolou.tinyJD.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dcxiaolou.tinyJD.bean.OmsCartItem;
import com.dcxiaolou.tinyJD.bean.PmsSkuInfo;
import com.dcxiaolou.tinyJD.service.CartService;
import com.dcxiaolou.tinyJD.service.PmsSkuInfoService;
import com.dcxiaolou.tinyJD.annotations.LoginRequired;
import com.dcxiaolou.tinyJD.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @Reference
    private CartService cartService;

    @RequestMapping("/addToCart")
    @LoginRequired(loginSuccess = false)
    public String addToCart(String skuId, int quantity, HttpServletRequest request, HttpServletResponse response, Model model) {
        //根据商品服务查询商品信息
        PmsSkuInfo skuInfo = pmsSkuInfoService.getBySkuId(Integer.parseInt(skuId));

        //将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        Date date = new Date();
        omsCartItem.setCreateDate(date);
        omsCartItem.setModifyDate(date);
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(quantity);
        omsCartItem.setIsChecked("1");
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());

        List<OmsCartItem> omsCartItems;
        //判断用户是否登录
        String memberId = (String) request.getAttribute("memberId");
        String username = (String) request.getAttribute("username");
        System.out.println("addToCart = " + memberId + " " + username);
        if (StringUtils.isBlank(memberId)) {
            //用户没有登录
            //获取cookie中原有的购物车数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            //判断添加的购物车数据在cookie中是否已经存在
            if (omsCartItems != null && omsCartItems.size() > 0) {
                boolean updateCartItem = false;
                for (OmsCartItem cartItem : omsCartItems) {
                    if (cartItem.getProductId().equals(omsCartItem.getProductId())) {
                        //cookie中已经存在该购物车数据，更新购物中商品数量
                        cartItem.setQuantity(cartItem.getQuantity() + omsCartItem.getQuantity());
                        updateCartItem = true;
                        break;
                    }
                }
                if (!updateCartItem) {
                    //cookie中不存在该商品，新增该商品
                    omsCartItems.add(omsCartItem);
                }
            } else {
                //cookie中不存在任何商品，新增该商品
                omsCartItems = new ArrayList<>();
                omsCartItems.add(omsCartItem);
            }
            //更新cookie
            CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60 * 60 * 72, true);
        } else {
            //用户已登录
            //从db中查询出该商品数据，判断该商品是否存在
            OmsCartItem omsCartItemFromBd = cartService.ifCartExistByUser(memberId, skuId);

            if (omsCartItemFromBd == null) {
                //用户没有添加该商品
                omsCartItem.setMemberId(memberId);
                cartService.save(omsCartItem);
            } else {
                //用户添加过该商品
                omsCartItemFromBd.setQuantity(omsCartItemFromBd.getQuantity() + quantity);
                cartService.update(omsCartItemFromBd);
            }

            //同步缓存
            cartService.flushCartCache(memberId);

        }

        model.addAttribute("skuInfo", skuInfo);
        model.addAttribute("num", quantity);

        return "cartSuccess";
    }

    @RequestMapping("/cartList")
    @LoginRequired(loginSuccess = false)
    public String cartList(HttpServletRequest request, HttpServletResponse response, Model model) {
        String memberId = (String) request.getAttribute("memberId");
        String username = (String) request.getAttribute("username");
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        System.out.println("cartController " + memberId + " " + username);
        if (StringUtils.isNotBlank(memberId)) {
            //用户已登录，从缓存中获取
            omsCartItems = cartService.cartList(memberId);
        }
        //用户未登录，从cookie中获取
        String cookieValue = CookieUtil.getCookieValue(request, "cartListCookie", true);
        if (StringUtils.isNotBlank(cookieValue)) {
            List<OmsCartItem> omsCartItemsFromCookie = JSON.parseArray(cookieValue, OmsCartItem.class);
            omsCartItems.addAll(omsCartItemsFromCookie);
        }
        double totalPrice = 0.0;
        for (OmsCartItem omsCartItem : omsCartItems) {
            if (omsCartItem.getIsChecked().equals("1"))
                totalPrice += omsCartItem.getQuantity() * Double.parseDouble(omsCartItem.getPrice());
        }
        model.addAttribute("cartList", omsCartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cart";
    }

    @RequestMapping("/checkCart")
    @LoginRequired(loginSuccess = false)
    public String checkCart(String isCheck, String skuId, HttpServletRequest request, HttpServletResponse response, Model model) {
        String memberId = (String) request.getAttribute("memberId");
        String username = (String) request.getAttribute("username");
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        double totalPrice = 0.0;
        if (StringUtils.isNotBlank(memberId)) {
            //调用服务，修改状态
            OmsCartItem omsCartItem = new OmsCartItem();
            omsCartItem.setMemberId(memberId);
            omsCartItem.setProductSkuId(skuId);
            omsCartItem.setIsChecked(isCheck);
            cartService.checkCart(omsCartItem);
            //将最新的数据从缓存中查出，渲染给内嵌页
            omsCartItems = cartService.cartList(memberId);
            for (OmsCartItem cartItem : omsCartItems) {
                if (cartItem.getIsChecked().equals("1"))
                    totalPrice += cartItem.getQuantity() * Double.parseDouble(cartItem.getPrice());
            }
        } else {
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                for (OmsCartItem omsCartItem : omsCartItems) {
                    if (omsCartItem.getProductSkuId().equals(skuId))
                        omsCartItem.setIsChecked(isCheck);
                    if (omsCartItem.getIsChecked().equals("1"))
                        totalPrice += omsCartItem.getQuantity() * Double.parseDouble(omsCartItem.getPrice());
                }
            }
        }
        model.addAttribute("cartList", omsCartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cartInner";
    }

}
