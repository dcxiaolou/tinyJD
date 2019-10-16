package com.dcxiaolou.tinyJD.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog1;
import com.dcxiaolou.tinyJD.manage.mapper.Catalog1Mapper;
import com.dcxiaolou.tinyJD.service.Catalog1Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class Catalog1ServiceImpl implements Catalog1Service {

    @Autowired
    private Catalog1Mapper catalog1Mapper;

    @Override
    public List<PmsBaseCatalog1> selectAllCatalog1s() {
        return catalog1Mapper.selectAll();
    }
}
