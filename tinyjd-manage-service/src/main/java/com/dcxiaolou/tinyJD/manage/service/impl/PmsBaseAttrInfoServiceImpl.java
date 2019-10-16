package com.dcxiaolou.tinyJD.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.PmsBaseAttrInfo;
import com.dcxiaolou.tinyJD.bean.PmsBaseAttrValue;
import com.dcxiaolou.tinyJD.manage.mapper.PmsBaseAttrInfoMapper;
import com.dcxiaolou.tinyJD.manage.mapper.PmsBaseAttrValueMapper;
import com.dcxiaolou.tinyJD.service.PmsBaseAttrInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Set;

@Service
public class PmsBaseAttrInfoServiceImpl implements PmsBaseAttrInfoService {

    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public List<PmsBaseAttrInfo> getAttrInfoByCatalog3Id(Integer catalog3Id) {
        Example e = new Example(PmsBaseAttrInfo.class);
        e.createCriteria().andEqualTo("catalog3_id", catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectByExample(e);
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfos) {
            e = new Example(PmsBaseAttrValue.class);
            e.createCriteria().andEqualTo("attr_id", pmsBaseAttrInfo.getId());
            pmsBaseAttrInfo.setAttrValues(pmsBaseAttrValueMapper.selectByExample(e));
        }
        return pmsBaseAttrInfos;
    }

    public void insertAttrInfo(PmsBaseAttrInfo attrInfo) {
        pmsBaseAttrInfoMapper.insertSelective(attrInfo);
        for (PmsBaseAttrValue attrValue : attrInfo.getAttrValues()) {
            attrValue.setAttr_id(attrInfo.getId());
            pmsBaseAttrValueMapper.insertSelective(attrValue);
        }
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrInfoById(Integer id) {
        Example e = new Example(PmsBaseAttrInfo.class);
        e.createCriteria().andEqualTo("id", id);
        return pmsBaseAttrInfoMapper.selectByExample(e);
    }

    public void updateAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        Example e = new Example(PmsBaseAttrInfo.class);
        e.createCriteria().andEqualTo("id", pmsBaseAttrInfo.getId());
        pmsBaseAttrInfoMapper.updateByExample(pmsBaseAttrInfo, e);

        e = new Example(PmsBaseAttrValue.class);
        e.createCriteria().andEqualTo("attr_id", pmsBaseAttrInfo.getId());
        pmsBaseAttrValueMapper.deleteByExample(e);

        for (PmsBaseAttrValue attrValue : pmsBaseAttrInfo.getAttrValues()) {
            pmsBaseAttrValueMapper.insert(attrValue);
        }
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueSet) {

        String valueIdStr = StringUtils.join(valueSet, ',');
        System.out.println("valueIdStr = " + valueIdStr);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectAttrValueByValueId(valueIdStr);
        return pmsBaseAttrInfos;
    }

}
