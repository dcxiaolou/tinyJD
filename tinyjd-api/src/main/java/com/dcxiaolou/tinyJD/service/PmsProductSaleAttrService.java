package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.PmsProductSaleAttr;

import java.util.List;

public interface PmsProductSaleAttrService {

    List<PmsProductSaleAttr> getProductSaleAttrBySPUId(Integer SPUId);

    List<PmsProductSaleAttr> selectSpuSaleAttrListCheckBySku(Integer productId, Integer skuId);

}
