package com.dcxiaolou.tinyJD.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog1;
import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog2;
import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog3;
import com.dcxiaolou.tinyJD.service.Catalog1Service;
import com.dcxiaolou.tinyJD.service.Catalog2Service;
import com.dcxiaolou.tinyJD.service.Catalog3Service;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CatalogController {

    @Reference
    private Catalog1Service catalog1Service;

    @Reference
    private Catalog2Service catalog2Service;

    @Reference
    private Catalog3Service catalog3Service;

    @ResponseBody
    @RequestMapping("/getCatalog1")
    public List<PmsBaseCatalog1> showCatalog1s() {
        return catalog1Service.selectAllCatalog1s();
    }

    @ResponseBody
    @RequestMapping("/getCatalog2/{catalog1Id}")
    public List<PmsBaseCatalog2> showCatalog2s(@PathVariable("catalog1Id") Integer catalog1Id) {
        return catalog2Service.selectCatalog2sByCatalog1Id(catalog1Id);
    }

    @ResponseBody
    @RequestMapping("/getCatalog3/{catalog2Id}")
    public List<PmsBaseCatalog3> showCatalog3s(@PathVariable("catalog2Id") Integer catalog2Id) {
        return catalog3Service.selectCatalog3sByCatalog2Id(catalog2Id);
    }

}
