package com.dcxiaolou.tinyJD.service;


import com.dcxiaolou.tinyJD.bean.OmsCartItem;

import java.util.List;

public interface CartService {

    OmsCartItem ifCartExistByUser(String memberId, String skuId);

    void save(OmsCartItem omsCartItem);

    void update(OmsCartItem omsCartItemFromBd);

    void flushCartCache(String memberId);

    List<OmsCartItem> cartList(String memberId);

    void checkCart(OmsCartItem omsCartItem);

    void delCartItemBySkuId(String productSkuId);
}
