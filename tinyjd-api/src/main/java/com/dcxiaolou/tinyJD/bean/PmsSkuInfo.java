package com.dcxiaolou.tinyJD.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

public class PmsSkuInfo implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column
    private String productId;
    @Column
    private String price;
    @Column
    private String skuName;
    @Column
    private String skuDesc;
    @Column
    private String weight;
    @Column
    private String tmId;
    @Column
    private String catalog3Id;
    @Column
    private String skuDefaultImg;
    @Transient
    List<PmsSkuAttrValue> pmsSkuAttrValue;
    @Transient
    List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValue;
    @Transient
    List<PmsSkuImage> pmsSkuImages;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getTmId() {
        return tmId;
    }

    public void setTmId(String tmId) {
        this.tmId = tmId;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getSkuDefaultImg() {
        return skuDefaultImg;
    }

    public void setSkuDefaultImg(String skuDefaultImg) {
        this.skuDefaultImg = skuDefaultImg;
    }

    public List<PmsSkuAttrValue> getPmsSkuAttrValue() {
        return pmsSkuAttrValue;
    }

    public void setPmsSkuAttrValue(List<PmsSkuAttrValue> pmsSkuAttrValue) {
        this.pmsSkuAttrValue = pmsSkuAttrValue;
    }

    public List<PmsSkuSaleAttrValue> getPmsSkuSaleAttrValue() {
        return pmsSkuSaleAttrValue;
    }

    public void setPmsSkuSaleAttrValue(List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValue) {
        this.pmsSkuSaleAttrValue = pmsSkuSaleAttrValue;
    }

    public List<PmsSkuImage> getPmsSkuImages() {
        return pmsSkuImages;
    }

    public void setPmsSkuImages(List<PmsSkuImage> pmsSkuImages) {
        this.pmsSkuImages = pmsSkuImages;
    }

    @Override
    public String toString() {
        return "PmsSkuInfo{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", price='" + price + '\'' +
                ", skuName='" + skuName + '\'' +
                ", skuDesc='" + skuDesc + '\'' +
                ", weight='" + weight + '\'' +
                ", tmId='" + tmId + '\'' +
                ", catalog3Id='" + catalog3Id + '\'' +
                ", skuDefaultImg='" + skuDefaultImg + '\'' +
                ", pmsSkuAttrValue=" + pmsSkuAttrValue +
                ", pmsSkuSaleAttrValue=" + pmsSkuSaleAttrValue +
                ", pmsSkuImages=" + pmsSkuImages +
                '}';
    }
}
