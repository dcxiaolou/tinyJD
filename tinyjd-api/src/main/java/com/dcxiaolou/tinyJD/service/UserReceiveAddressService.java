package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserReceiveAddressService {
    List<UmsMemberReceiveAddress> selectAllUserAddress(Integer id);
}
