package com.dcxiaolou.tinyJD.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dcxiaolou.tinyJD.bean.PmsProductSaleAttr;
import com.dcxiaolou.tinyJD.bean.PmsSkuInfo;
import com.dcxiaolou.tinyJD.bean.PmsSkuSaleAttrValue;
import com.dcxiaolou.tinyJD.service.PmsProductSaleAttrService;
import com.dcxiaolou.tinyJD.service.PmsSkuInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
public class ItemController {

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @Reference
    private PmsProductSaleAttrService pmsProductSaleAttrService;

    @RequestMapping("/show")
    public String showPage() {
        return "itemDetail";
    }

    @RequestMapping("/{skuId}.html")
    public String itemPage(@PathVariable("skuId") Integer skuId, Model model) {
        PmsSkuInfo PmsSkuInfo = pmsSkuInfoService.getBySkuId(skuId);

        Map<String, String> map = new HashMap<>();
        List<PmsSkuInfo> skuSaleAttrValueListBySpu = pmsSkuInfoService.getSkuSaleAttrValueListBySpu(Integer.parseInt(PmsSkuInfo.getProductId()));
        for (PmsSkuInfo pmsSkuInfo : skuSaleAttrValueListBySpu) {
            String v = pmsSkuInfo.getId();
            List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValue = pmsSkuInfo.getPmsSkuSaleAttrValue();
            String k = "";
            for (PmsSkuSaleAttrValue skuSaleAttrValue : pmsSkuSaleAttrValue) {
                k += skuSaleAttrValue.getSaleAttrValueId() + "|";
            }
            map.put(k, v);
        }
        model.addAttribute("valuesSkuJson", JSON.toJSONString(map));
        System.out.println(JSON.toJSONString(map));
        model.addAttribute("skuInfo", PmsSkuInfo);
        return "itemDetail";
    }

    @ResponseBody
    @RequestMapping("/spuSaleAttrListCheckBySku/{productId}/{skuId}")
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(@PathVariable("productId") Integer productId, @PathVariable("skuId") Integer skuId) {
        return pmsProductSaleAttrService.selectSpuSaleAttrListCheckBySku(productId, skuId);
    }

}
