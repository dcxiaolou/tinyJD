package com.dcxiaolou.tinyJD.bean;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

public class PmsSearchSkuInfo implements Serializable {

    @Id
    private String id;
    private String skuName;
    private String skuDesc;
    private String catalog3Id;
    private double price;
    private String skuDefaultImg;
    private double hotScore;
    private String productId;
    private List<PmsSkuAttrValue> pmsSkuAttrValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuDesc() {
        return skuDesc;
    }

    public void setSkuDesc(String skuDesc) {
        this.skuDesc = skuDesc;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSkuDefaultImg() {
        return skuDefaultImg;
    }

    public void setSkuDefaultImg(String skuDefaultImg) {
        this.skuDefaultImg = skuDefaultImg;
    }

    public double getHotScore() {
        return hotScore;
    }

    public void setHotScore(double hotScore) {
        this.hotScore = hotScore;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public List<PmsSkuAttrValue> getPmsSkuAttrValue() {
        return pmsSkuAttrValue;
    }

    public void setPmsSkuAttrValue(List<PmsSkuAttrValue> pmsSkuAttrValue) {
        this.pmsSkuAttrValue = pmsSkuAttrValue;
    }

    @Override
    public String toString() {
        return "PmsSearchSkuInfo{" +
                "id='" + id + '\'' +
                ", skuName='" + skuName + '\'' +
                ", skuDesc='" + skuDesc + '\'' +
                ", catalog3Id='" + catalog3Id + '\'' +
                ", price=" + price +
                ", skuDefaultImg='" + skuDefaultImg + '\'' +
                ", hotScore=" + hotScore +
                ", productId='" + productId + '\'' +
                ", pmsSkuAttrValue=" + pmsSkuAttrValue +
                '}';
    }
}
