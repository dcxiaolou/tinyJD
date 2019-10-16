package com.dcxiaolou.tinyJD.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog2;
import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog3;
import com.dcxiaolou.tinyJD.search.mapper.PmsBaseCatalog2Mapper;
import com.dcxiaolou.tinyJD.search.mapper.PmsBaseCatalog3Mapper;
import com.dcxiaolou.tinyJD.service.PmsBaseCatalog2Service;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class PmsBaseCatalog2ServiceImpl implements PmsBaseCatalog2Service {

    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    @Override
    public List<PmsBaseCatalog2> getPmsBaseCatalog2(Integer catalog1Id) {
        Example e = new Example(PmsBaseCatalog2.class);
        e.createCriteria().andEqualTo("catalog1_id", catalog1Id);
        List<PmsBaseCatalog2> pmsBaseCatalog2s = pmsBaseCatalog2Mapper.selectByExample(e);
        for (PmsBaseCatalog2 pmsBaseCatalog2 : pmsBaseCatalog2s) {
            e = new Example(PmsBaseCatalog3.class);
            e.createCriteria().andEqualTo("catalog2_id", pmsBaseCatalog2.getId());
            pmsBaseCatalog2.setCatalog3s(pmsBaseCatalog3Mapper.selectByExample(e));
        }
        return pmsBaseCatalog2s;
    }
}
