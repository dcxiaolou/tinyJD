package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.PmsSkuInfo;

import java.util.List;

public interface PmsSkuInfoService {

    String save(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getBySkuId(Integer skuId);

    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(Integer skuId);

    List<PmsSkuInfo> getAll();

    boolean checkPrice(String productSkuId, String price);
}
