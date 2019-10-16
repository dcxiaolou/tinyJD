package com.dcxiaolou.tinyJD.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog3;
import com.dcxiaolou.tinyJD.manage.mapper.Catalog3Mapper;
import com.dcxiaolou.tinyJD.service.Catalog3Service;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class Catalog3ServiceImpl implements Catalog3Service {

    @Autowired
    private Catalog3Mapper catalog3Mapper;

    @Override
    public List<PmsBaseCatalog3> selectCatalog3sByCatalog2Id(Integer catalog2Id) {
        Example e = new Example(PmsBaseCatalog3.class);
        e.createCriteria().andEqualTo("catalog2_id", catalog2Id);
        return catalog3Mapper.selectByExample(e);
    }
}
