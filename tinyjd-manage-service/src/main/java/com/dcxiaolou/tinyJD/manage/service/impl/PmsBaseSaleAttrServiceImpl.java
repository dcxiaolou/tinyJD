package com.dcxiaolou.tinyJD.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.PmsBaseSaleAttr;
import com.dcxiaolou.tinyJD.manage.mapper.PmsBaseSaleAttrMapper;
import com.dcxiaolou.tinyJD.service.PmsBaseSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class PmsBaseSaleAttrServiceImpl implements PmsBaseSaleAttrService {

    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Override
    public List<PmsBaseSaleAttr> getAll() {
        return pmsBaseSaleAttrMapper.selectAll();
    }
}
