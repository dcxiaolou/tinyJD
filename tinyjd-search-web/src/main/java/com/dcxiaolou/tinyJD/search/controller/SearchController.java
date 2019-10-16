package com.dcxiaolou.tinyJD.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dcxiaolou.tinyJD.bean.*;
import com.dcxiaolou.tinyJD.service.PmsBaseAttrInfoService;
import com.dcxiaolou.tinyJD.service.PmsBaseAttrValueService;
import com.dcxiaolou.tinyJD.service.PmsSearchParamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class SearchController {

    @Reference
    private PmsSearchParamService pmsSearchParamService;

    @Reference
    private PmsBaseAttrInfoService pmsBaseAttrInfoService;

    @Reference
    private PmsBaseAttrValueService pmsBaseAttrValueService;

    @RequestMapping("/search")
    public String search(@RequestParam("catalog3Id") String catalog3Id, @RequestParam("keyword") String keyword, @RequestParam("valueId") String[] valueIds, Model model) {
        PmsSearchParam searchParam = new PmsSearchParam();
        searchParam.setCatalog3Id(catalog3Id);
        searchParam.setKeyword(keyword);
        List<PmsSkuAttrValue> pmsSkuAttrValueList = new ArrayList<>();
        for (String valueId : valueIds) {
            System.out.println(valueId);
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setValueId(valueId);
            pmsSkuAttrValueList.add(pmsSkuAttrValue);
        }
        searchParam.setPmsSkuAttrValue(pmsSkuAttrValueList);
        System.out.println(searchParam);
        List<PmsSearchSkuInfo> searchSkuInfos = pmsSearchParamService.list(searchParam);
        if (searchSkuInfos != null && searchSkuInfos.size() > 0) {
            model.addAttribute("skuInfos", searchSkuInfos);
            model.addAttribute("keyword", keyword);
            for (PmsSearchSkuInfo searchSkuInfo : searchSkuInfos) {
                System.out.println(searchSkuInfo);
            }

            //抽取检索结果所包含的平台属性集合
            Set<String> valueSet = new HashSet<>();
            for (PmsSearchSkuInfo searchSkuInfo : searchSkuInfos) {
                List<PmsSkuAttrValue> pmsSkuAttrValue = searchSkuInfo.getPmsSkuAttrValue();
                for (PmsSkuAttrValue skuAttrValue : pmsSkuAttrValue) {
                    valueSet.add(skuAttrValue.getValueId());
                }
            }

            //根据valueId将属性列表查询出来
            List<PmsBaseAttrInfo> attrValueList = pmsBaseAttrInfoService.getAttrValueListByValueId(valueSet);
            List<PmsBaseAttrInfo> attrValues = new ArrayList<>();
            List<PmsBaseAttrValue> values;
            for (PmsBaseAttrInfo attrInfo : attrValueList) {
                PmsBaseAttrInfo attrInfoById = pmsBaseAttrInfoService.getAttrInfoById(attrInfo.getId()).get(0);
                List<PmsBaseAttrValue> attrValues1 = attrInfo.getAttrValues();
                values = new ArrayList<>();
                for (PmsBaseAttrValue attrValue : attrValues1) {
                    values.add(pmsBaseAttrValueService.getAttrValuesById(attrValue.getId()));
                }
                attrInfoById.setAttrValues(values);
                attrValues.add(attrInfoById);
            }
            for (PmsBaseAttrInfo attrValue : attrValues) {
                System.out.println(attrValue);
            }

            //对平台属性集合进一步处理，移除当前条件valueId所在的属性组
            /*
             * 使用迭代器可以很方便的处理
             * 不要使用ArrayList，容易出现下标越界
             * */
            List<PmsSkuAttrValue> pmsSkuAttrValue = searchParam.getPmsSkuAttrValue();
            if (pmsSkuAttrValue != null) {
                //面包屑
                List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
                //如果pmsSkuAttrValue不为空，说明包含平台属性，每一个平台属性就是一个面包屑
                for (PmsSkuAttrValue skuAttrValue : pmsSkuAttrValue) {
                    PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                    pmsSearchCrumb.setValueId(skuAttrValue.getValueId());
                    pmsSearchCrumb.setUrlParam(getUrlParam(searchParam, skuAttrValue.getValueId()));
                    Iterator<PmsBaseAttrInfo> iterator = attrValues.iterator();
                    while (iterator.hasNext()) {
                        PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                        List<PmsBaseAttrValue> attrInfoAttrValues = pmsBaseAttrInfo.getAttrValues();
                        for (PmsBaseAttrValue attrInfoAttrValue : attrInfoAttrValues) {
                            if (Integer.parseInt(skuAttrValue.getValueId()) == attrInfoAttrValue.getId()) {
                                pmsSearchCrumb.setValueName(attrInfoAttrValue.getValue_name());
                                iterator.remove();
                            }
                        }
                    }
                    pmsSearchCrumbs.add(pmsSearchCrumb);
                }
                model.addAttribute("attrValueSelectList", pmsSearchCrumbs);
            }

            model.addAttribute("attrList", attrValues);

            String urlParam = getUrlParam(searchParam, null);

            model.addAttribute("urlParam", urlParam);
        } else {
            model.addAttribute("skuIsNull", "skuIsNull");
        }

        return "search";
    }

    private String getUrlParam(PmsSearchParam searchParam, String valueId) {
        String urlParam = "";
        String keyword = searchParam.getKeyword();
        String catalog3Id = searchParam.getCatalog3Id();
        List<PmsSkuAttrValue> pmsSkuAttrValue = searchParam.getPmsSkuAttrValue();
        if (catalog3Id != null && !"null".equals(catalog3Id)) {
            if (!"".equals(urlParam)) {
                urlParam += "&";
            }
            urlParam += "catalog3Id=" + catalog3Id;
        } else {
            if (!"".equals(urlParam)) {
                urlParam += "&";
            }
            urlParam += "catalog3Id=";
        }
        if (keyword != null) {
            if (!"".equals(urlParam)) {
                urlParam += "&";
            }
            urlParam += "keyword=" + keyword;
        } else {
            if (!"".equals(urlParam)) {
                urlParam += "&";
            }
            urlParam += "keyword=" + keyword;
        }
        if (pmsSkuAttrValue != null) {
            if (valueId != null) {
                String url = "";
                for (PmsSkuAttrValue skuAttrValue : pmsSkuAttrValue) {
                    if (!valueId.equals(skuAttrValue.getValueId()))
                        url += "&valueId=" + skuAttrValue.getValueId();
                }
                if (url == "") {
                    url = "&valueId=";
                }
                urlParam += url;
            } else {
                for (PmsSkuAttrValue skuAttrValue : pmsSkuAttrValue) {
                    urlParam += "&valueId=" + skuAttrValue.getValueId();
                }
            }
        }

        return urlParam;
    }

}
