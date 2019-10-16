package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.PmsBaseAttrValue;

import java.util.List;

public interface PmsBaseAttrValueService {

    List<PmsBaseAttrValue> getAttrValuesByAttrInfoId(Integer attrInfoId);

    int deleteAttrValueByAttrInfoId(Integer attrInfoId);

    PmsBaseAttrValue getAttrValuesById(Integer id);
}
