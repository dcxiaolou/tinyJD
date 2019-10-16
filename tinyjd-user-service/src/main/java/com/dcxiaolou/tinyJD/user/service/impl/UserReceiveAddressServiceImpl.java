package com.dcxiaolou.tinyJD.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.UmsMemberReceiveAddress;
import com.dcxiaolou.tinyJD.service.UserReceiveAddressService;
import com.dcxiaolou.tinyJD.user.mapper.UserReceiveAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserReceiveAddressServiceImpl implements UserReceiveAddressService {

    @Autowired
    private UserReceiveAddressMapper userReceiveAddressMapper;

    public List<UmsMemberReceiveAddress> selectAllUserAddress(Integer id) {
        //需要使用tk.mybatis.mapper下的方发
        Example e = new Example(UmsMemberReceiveAddress.class);
        Example.Criteria criteria = e.createCriteria();
        criteria.andEqualTo("member_id", id);
        return userReceiveAddressMapper.selectByExample(e);
    }
}
