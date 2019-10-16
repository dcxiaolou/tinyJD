package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.PmsProductInfo;

import java.util.List;

public interface PmsProductInfoService {

    List<PmsProductInfo> selectByCatalog3Id(Integer catalog3Id);

    String save(PmsProductInfo pmsProductInfo);
}
