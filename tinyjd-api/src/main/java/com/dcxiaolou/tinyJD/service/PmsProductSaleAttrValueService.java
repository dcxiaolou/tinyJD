package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.PmsProductSaleAttrValue;

import java.util.List;

public interface PmsProductSaleAttrValueService {

    List<PmsProductSaleAttrValue> getBySPUId(Integer SPUId);

}
