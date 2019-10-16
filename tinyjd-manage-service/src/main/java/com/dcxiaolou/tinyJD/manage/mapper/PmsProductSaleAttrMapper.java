package com.dcxiaolou.tinyJD.manage.mapper;

import com.dcxiaolou.tinyJD.bean.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {
    List<PmsProductSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("productId") Integer productId, @Param("skuId") Integer skuId);
}
