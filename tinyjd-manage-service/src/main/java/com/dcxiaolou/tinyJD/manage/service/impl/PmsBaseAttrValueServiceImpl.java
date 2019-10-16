package com.dcxiaolou.tinyJD.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.PmsBaseAttrValue;
import com.dcxiaolou.tinyJD.manage.mapper.PmsBaseAttrValueMapper;
import com.dcxiaolou.tinyJD.service.PmsBaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class PmsBaseAttrValueServiceImpl implements PmsBaseAttrValueService {

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public List<PmsBaseAttrValue> getAttrValuesByAttrInfoId(Integer attrInfoId) {
        Example e = new Example(PmsBaseAttrValue.class);
        e.createCriteria().andEqualTo("attr_id", attrInfoId);
        return pmsBaseAttrValueMapper.selectByExample(e);
    }

    public int deleteAttrValueByAttrInfoId(Integer attrInfoId) {
        Example e = new Example(PmsBaseAttrValue.class);
        e.createCriteria().andEqualTo("attr_id", attrInfoId);
        return pmsBaseAttrValueMapper.deleteByExample(e);
    }

    @Override
    public PmsBaseAttrValue getAttrValuesById(Integer id) {
        PmsBaseAttrValue attrValue = new PmsBaseAttrValue();
        attrValue.setId(id);
        return pmsBaseAttrValueMapper.select(attrValue).get(0);
    }
}
