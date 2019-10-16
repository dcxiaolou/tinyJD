package com.dcxiaolou.tinyJD.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog1;
import com.dcxiaolou.tinyJD.search.mapper.PmsBaseCatalog1Mapper;
import com.dcxiaolou.tinyJD.service.PmsBaseCatalog1Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class PmsBaseCatalog1ServiceImpl implements PmsBaseCatalog1Service {

    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Override
    public List<PmsBaseCatalog1> getPmsBaseCatalog1() {
        return pmsBaseCatalog1Mapper.selectAll();
    }
}
