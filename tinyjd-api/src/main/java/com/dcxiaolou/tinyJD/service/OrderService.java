package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.OmsOrder;

public interface OrderService {
    String createTradeCode(String memberId);

    String checkTradeCode(String memberId, String tradeCode);

    void saveOrder(OmsOrder omsOrder);

    OmsOrder getByOutTradeNo(String outTradeNo);

    void updateOrder(OmsOrder omsOrder);
}
