package com.dcxiaolou.tinyJD.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.PmsProductSaleAttr;
import com.dcxiaolou.tinyJD.bean.PmsProductSaleAttrValue;
import com.dcxiaolou.tinyJD.manage.mapper.PmsProductSaleAttrMapper;
import com.dcxiaolou.tinyJD.manage.mapper.PmsProductSaleAttrValueMapper;
import com.dcxiaolou.tinyJD.service.PmsProductSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class PmsProductSaleAttrServiceImpl implements PmsProductSaleAttrService {

    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsProductSaleAttr> getProductSaleAttrBySPUId(Integer SPUId) {
        Example e = new Example(PmsProductSaleAttr.class);
        e.createCriteria().andEqualTo("productId", SPUId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectByExample(e);
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrs) {
            e = new Example(PmsProductSaleAttrValue.class);
            e.createCriteria().andEqualTo("saleAttrId", pmsProductSaleAttr.getSaleAttrId())
                    .andEqualTo("productId", pmsProductSaleAttr.getProductId());
            pmsProductSaleAttr.setPmsProductSaleAttrValueList(pmsProductSaleAttrValueMapper.selectByExample(e));
        }
        return pmsProductSaleAttrs;
    }

    @Override
    public List<PmsProductSaleAttr> selectSpuSaleAttrListCheckBySku(Integer productId, Integer skuId) {
        return pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(productId, skuId);
    }
}
