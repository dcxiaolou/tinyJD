package com.dcxiaolou.tinyJD.user.mapper;

import com.dcxiaolou.tinyJD.bean.UmsMemberReceiveAddress;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserReceiveAddressMapper extends Mapper<UmsMemberReceiveAddress> {
    List<UmsMemberReceiveAddress> selectAllUserReceiveAddress();
}
