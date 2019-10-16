package com.dcxiaolou.tinyJD.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dcxiaolou.tinyJD.bean.PmsBaseAttrInfo;
import com.dcxiaolou.tinyJD.bean.PmsBaseAttrValue;
import com.dcxiaolou.tinyJD.service.PmsBaseAttrInfoService;
import com.dcxiaolou.tinyJD.service.PmsBaseAttrValueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PmsBaseAttrController {
    @Reference
    private PmsBaseAttrInfoService pmsBaseAttrInfoService;

    @Reference
    private PmsBaseAttrValueService pmsBaseAttrValueService;

    @ResponseBody
    @RequestMapping("/attrInfoList/{catalog3Id}")
    public List<PmsBaseAttrInfo> showPmsBaseAttrInfo(@PathVariable("catalog3Id") Integer catalog3Id) {
        return pmsBaseAttrInfoService.getAttrInfoByCatalog3Id(catalog3Id);
    }

    @RequestMapping("/saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(String attr_name, Integer catalog3_id, String value_name){
        PmsBaseAttrInfo attrInfo = new PmsBaseAttrInfo();
        attrInfo.setAttr_name(attr_name);
        attrInfo.setCatalog3_id(catalog3_id);
        String[] strings = value_name.substring(1, value_name.length() - 1).split(",");
        List<String> list = new ArrayList<>();
        for (String string : strings) {
            list.add(string.substring(1, string.length() - 1));
        }
        List<PmsBaseAttrValue> attrValues = new ArrayList<>();
        for (String s : list) {
            PmsBaseAttrValue attrValue = new PmsBaseAttrValue();
            attrValue.setValue_name(s);
            attrValues.add(attrValue);
        }
        attrInfo.setAttrValues(attrValues);
        pmsBaseAttrInfoService.insertAttrInfo(attrInfo);
        return "success";
    }

    @RequestMapping("/toModifyAttrPage/{attr_id}/{catalog3Id}")
    public String toModifyAttrPage(@PathVariable Integer attr_id, @PathVariable("catalog3Id") String catalog3Id, Model model) {
        PmsBaseAttrInfo attrInfo = pmsBaseAttrInfoService.getAttrInfoById(attr_id).get(0);
        List<PmsBaseAttrValue> attrValues = pmsBaseAttrValueService.getAttrValuesByAttrInfoId(attrInfo.getId());
        model.addAttribute("attr_name", attrInfo.getAttr_name());
        model.addAttribute("attr_value", attrValues);
        model.addAttribute("attr_id", attr_id);
        model.addAttribute("catalog3_id", catalog3Id);
        return "param-modify";
    }

    @ResponseBody
    @RequestMapping("/modifyAttrInfo")
    public String modifyAttrInfo(String attr_name, Integer attr_id, Integer catalog3_id, String value_name) {
        PmsBaseAttrInfo attrInfo = new PmsBaseAttrInfo();
        attrInfo.setAttr_name(attr_name);
        attrInfo.setId(attr_id);
        attrInfo.setCatalog3_id(catalog3_id);
        System.out.println(catalog3_id);
        String[] strings = value_name.substring(1, value_name.length() - 1).split(",");
        List<String> list = new ArrayList<>();
        for (String string : strings) {
            list.add(string.substring(1, string.length() - 1));
        }
        List<PmsBaseAttrValue> attrValues = new ArrayList<>();
        for (String s : list) {
            PmsBaseAttrValue attrValue = new PmsBaseAttrValue();
            attrValue.setValue_name(s);
            attrValue.setAttr_id(attr_id);
            attrValues.add(attrValue);
        }
        attrInfo.setAttrValues(attrValues);
        pmsBaseAttrInfoService.updateAttrInfo(attrInfo);
        return "success";
    }

}
