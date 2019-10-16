package com.dcxiaolou.tinyJD.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog2;
import com.dcxiaolou.tinyJD.manage.mapper.Catalog2Mapper;
import com.dcxiaolou.tinyJD.service.Catalog2Service;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class Catalog2ServiceImpl implements Catalog2Service {

    @Autowired
    private Catalog2Mapper catalog2Mapper;

    @Override
    public List<PmsBaseCatalog2> selectCatalog2sByCatalog1Id(Integer catalog1Id) {
        Example e = new Example(PmsBaseCatalog2.class);
        e.createCriteria().andEqualTo("catalog1_id", catalog1Id);
        return catalog2Mapper.selectByExample(e);
    }
}
