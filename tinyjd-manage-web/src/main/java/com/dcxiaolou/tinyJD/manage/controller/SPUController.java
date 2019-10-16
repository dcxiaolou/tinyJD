package com.dcxiaolou.tinyJD.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dcxiaolou.tinyJD.bean.*;
import com.dcxiaolou.tinyJD.manage.util.PmsUploadUtil;
import com.dcxiaolou.tinyJD.service.PmsBaseSaleAttrService;
import com.dcxiaolou.tinyJD.service.PmsProductInfoService;
import com.dcxiaolou.tinyJD.service.PmsSkuInfoService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SPUController {

    @Reference
    private PmsProductInfoService pmsProductInfoService;

    @Reference
    private PmsBaseSaleAttrService pmsBaseSaleAttrService;

    private static final List<String> imgUrls = new ArrayList<>();

    @ResponseBody
    @RequestMapping("/spuList/{catalog3Id}")
    public List<PmsProductInfo> spuList(@PathVariable("catalog3Id") Integer catalog3Id) {
        return pmsProductInfoService.selectByCatalog3Id(catalog3Id);
    }

    @RequestMapping("/toAddSPUPage/{catalog3_id}")
    public String toAddSPUPage(@PathVariable("catalog3_id") String catalog3Id, Model model) {
        model.addAttribute("catalog3_id", catalog3Id);
        return "spu-add";
    }

    @ResponseBody
    @RequestMapping("/getAllPmsBaseSaleAttr")
    public List<PmsBaseSaleAttr> getAllPmsBaseSaleAttr() {
        return pmsBaseSaleAttrService.getAll();
    }

    @ResponseBody
    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file) {
        String imgUrl = PmsUploadUtil.uploadImage(file);
        System.out.println(imgUrl);
        String result = null;
        if (imgUrl != null) {
            result = "{\"success\":  true}";
            imgUrls.add(imgUrl);
        } else {
            result = "{\"success\":  false, ";
            result += "\"message\":  \"error\"}";
        }
        return result;
    }

    @ResponseBody
    @RequestMapping("/postData")
    public String postData(@RequestBody PmsProductInfo pmsProductInfo) {
        PmsProductImage pmsProductImage;
        List<PmsProductImage> list = new ArrayList<>();
        String imgName = null;
        for (String imgUrl : imgUrls) {
            imgName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
            pmsProductImage = new PmsProductImage();
            pmsProductImage.setImgUrl(imgUrl);
            pmsProductImage.setImgName(imgName);
            list.add(pmsProductImage);
        }
        pmsProductInfo.setPmsProductImageList(list);
        System.out.println(pmsProductInfo);
        imgUrls.clear();
        return pmsProductInfoService.save(pmsProductInfo);
    }

}
