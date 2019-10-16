package com.dcxiaolou.tinyJD.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dcxiaolou.tinyJD.annotations.LoginRequired;
import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog1;
import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog2;
import com.dcxiaolou.tinyJD.service.PmsBaseCatalog1Service;
import com.dcxiaolou.tinyJD.service.PmsBaseCatalog2Service;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin // 跨域问题
@Controller
public class PortalController {

    @Reference
    private PmsBaseCatalog1Service pmsBaseCatalog1Service;

    @Reference
    private PmsBaseCatalog2Service pmsBaseCatalog2Service;

    @RequestMapping("/index")
    @LoginRequired(loginSuccess = false)
    public String showPortalPage() {
        return "portal";
    }

    @ResponseBody
    @RequestMapping("/getCatalog1")
    public List<PmsBaseCatalog1> getCatalog1() {
        return pmsBaseCatalog1Service.getPmsBaseCatalog1();
    }

    @ResponseBody
    @RequestMapping("/getCatalog2/{catalog1Id}")
    public List<PmsBaseCatalog2> getCatalog2(@PathVariable("catalog1Id") Integer catalog1Id) {
        return pmsBaseCatalog2Service.getPmsBaseCatalog2(catalog1Id);
    }

    @ResponseBody
    @RequestMapping("/catalogLoader")
    public String catalogLoader() {
        List<PmsBaseCatalog1> catalog1List = getCatalog1();
        /*List<PmsBaseCatalog2> catalog2List = new ArrayList<>();*/
        String catalogStr = "{";
        for (PmsBaseCatalog1 catalog1 : catalog1List) {
            catalogStr += "\"" + catalog1.getId() + "\": ";
            /*catalog2List.addAll(getCatalog2(catalog1.getId()));*/
            catalogStr += JSON.toJSONString(getCatalog2(catalog1.getId()));
            catalogStr += ",";
        }
        catalogStr = catalogStr.substring(0, catalogStr.length() - 1);
        catalogStr += "}";
        /*String catalogJson = JSON.toJSONString(catalog2List);*/
        catalogStr = catalogStr.replaceAll("catalog1_id", "catalog1Id");
        catalogStr = catalogStr.replaceAll("catalog3s", "catalog3List");
        catalogStr = catalogStr.replaceAll("catalog2_id", "catalog2Id");
        System.out.println(catalogStr);
        return catalogStr;
    }

}
