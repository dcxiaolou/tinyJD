package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.PmsBaseAttrInfo;

import java.util.List;
import java.util.Set;

public interface PmsBaseAttrInfoService {

    List<PmsBaseAttrInfo> getAttrInfoByCatalog3Id(Integer catalog3Id);

    void insertAttrInfo(PmsBaseAttrInfo attrInfo);

    List<PmsBaseAttrInfo> getAttrInfoById(Integer id);

    void updateAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueSet);
}
