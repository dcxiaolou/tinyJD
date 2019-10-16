package com.dcxiaolou.tinyJD.bean;

import java.io.Serializable;
import java.util.List;

public class PmsSearchParam implements Serializable {

    private String catalog3Id;

    private String keyword;

    private List<PmsSkuAttrValue> pmsSkuAttrValue;

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<PmsSkuAttrValue> getPmsSkuAttrValue() {
        return pmsSkuAttrValue;
    }

    public void setPmsSkuAttrValue(List<PmsSkuAttrValue> pmsSkuAttrValue) {
        this.pmsSkuAttrValue = pmsSkuAttrValue;
    }

    @Override
    public String toString() {
        return "PmsSearchParam{" +
                "catalog3Id='" + catalog3Id + '\'' +
                ", keyword='" + keyword + '\'' +
                ", pmsSkuAttrValue=" + pmsSkuAttrValue +
                '}';
    }
}
