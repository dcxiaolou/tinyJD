package com.dcxiaolou.tinyJD.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.PmsProductImage;
import com.dcxiaolou.tinyJD.bean.PmsProductInfo;
import com.dcxiaolou.tinyJD.bean.PmsProductSaleAttr;
import com.dcxiaolou.tinyJD.bean.PmsProductSaleAttrValue;
import com.dcxiaolou.tinyJD.manage.mapper.PmsProductImageMapper;
import com.dcxiaolou.tinyJD.manage.mapper.PmsProductInfoMapper;
import com.dcxiaolou.tinyJD.manage.mapper.PmsProductSaleAttrMapper;
import com.dcxiaolou.tinyJD.manage.mapper.PmsProductSaleAttrValueMapper;
import com.dcxiaolou.tinyJD.service.PmsProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class PmsProductInfoServiceImpl implements PmsProductInfoService {

    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;

    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsProductInfo> selectByCatalog3Id(Integer catalog3Id) {
        Example e = new Example(PmsProductInfo.class);
        e.createCriteria().andEqualTo("catalog3Id", catalog3Id);
        return pmsProductInfoMapper.selectByExample(e);
    }

    @Override
    public String save(PmsProductInfo pmsProductInfo) {
        int result = 0;
        result += pmsProductInfoMapper.insertSelective(pmsProductInfo);

        List<PmsProductImage> pmsProductImageList = pmsProductInfo.getPmsProductImageList();
        for (PmsProductImage pmsProductImage : pmsProductImageList) {
            pmsProductImage.setProductId(pmsProductInfo.getId());
            pmsProductImageMapper.insertSelective(pmsProductImage);
        }
        result += 1;
        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductInfo.getPmsProductSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrList) {
            pmsProductSaleAttr.setProductId(pmsProductInfo.getId());
            pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList = pmsProductSaleAttr.getPmsProductSaleAttrValueList();
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : pmsProductSaleAttrValueList) {
                pmsProductSaleAttrValue.setProductId(pmsProductInfo.getId());
                pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
            }
        }
        result += 1;
        if (result == 3)
            return "success";
        else
            return "fail";
    }
}
