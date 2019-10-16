package com.dcxiaolou.tinyJD.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dcxiaolou.tinyJD.bean.*;
import com.dcxiaolou.tinyJD.service.PmsBaseAttrInfoService;
import com.dcxiaolou.tinyJD.service.PmsProductImageService;
import com.dcxiaolou.tinyJD.service.PmsProductSaleAttrService;
import com.dcxiaolou.tinyJD.service.PmsSkuInfoService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SKUController {
    @Reference
    private PmsBaseAttrInfoService pmsBaseAttrInfoService;

    @Reference
    private PmsProductSaleAttrService pmsProductSaleAttrService;

    @Reference
    private PmsProductImageService pmsProductImageService;

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @Autowired
    private JestClient jestClient;

    @RequestMapping("/toAddSKUPage")
    public String toSKUPage(Integer catalog3Id,Integer spuId,String productName, Model model) {
        List<PmsBaseAttrInfo> attrInfoByCatalog3Id = pmsBaseAttrInfoService.getAttrInfoByCatalog3Id(catalog3Id);
        List<PmsProductSaleAttr> productSaleAttrBySPUId = pmsProductSaleAttrService.getProductSaleAttrBySPUId(spuId);
        List<PmsProductImage> bySPUId = pmsProductImageService.getBySPUId(spuId);
        model.addAttribute("catalog3Id", catalog3Id);
        model.addAttribute("productId", spuId);
        model.addAttribute("product_name", productName);
        model.addAttribute("attrInfos", attrInfoByCatalog3Id);
        model.addAttribute("saleAttrValue", productSaleAttrBySPUId);
        model.addAttribute("productImgs", bySPUId);
        for (PmsBaseAttrInfo attrInfo : attrInfoByCatalog3Id) {
            System.out.println(attrInfo);
        }
        System.out.println("-----------------------------------");
        for (PmsProductSaleAttr pmsProductSaleAttr : productSaleAttrBySPUId) {
            System.out.println(pmsProductSaleAttr);
        }
        System.out.println("-----------------------------------");
        for (PmsProductImage pmsProductImage : bySPUId) {
            System.out.println(pmsProductImage);
        }
        return "sku-add";
    }

    @ResponseBody
    @RequestMapping("/saveSkuInfo")
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo) {
        //后台接收json数据需要在接收对象上添加@RequestBody注解
        return pmsSkuInfoService.save(pmsSkuInfo);
    }

    @RequestMapping("/flushES")
    public void flushES() throws Exception {
        //查询mysql
        List<PmsSkuInfo> skuInfos = pmsSkuInfoService.getAll();
        //转换为es的数据结构
        // 使用commons.bean工具类
        List<PmsSearchSkuInfo> searchSkuInfos = new ArrayList<>();
        for (PmsSkuInfo skuInfo : skuInfos) {
            PmsSearchSkuInfo searchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(searchSkuInfo, skuInfo);
            searchSkuInfos.add(searchSkuInfo);
        }
        // 导入es

        for (PmsSearchSkuInfo skuInfo : searchSkuInfos) {
            Index index = new Index.Builder(skuInfo).index("gmall_pms").type("PmsSkuInfo").id(skuInfo.getId()).build();
            jestClient.execute(index);
        }
    }
}
