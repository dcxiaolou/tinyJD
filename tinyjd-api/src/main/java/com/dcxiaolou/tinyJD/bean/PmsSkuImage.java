package com.dcxiaolou.tinyJD.bean;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

public class PmsSkuImage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private Integer sku_id;
    @Column
    private String img_name;
    @Column
    private String img_url;
    @Column
    private Integer product_img_id;
    @Column
    private Boolean is_default;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSku_id() {
        return sku_id;
    }

    public void setSku_id(Integer sku_id) {
        this.sku_id = sku_id;
    }

    public String getImg_name() {
        return img_name;
    }

    public void setImg_name(String img_name) {
        this.img_name = img_name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public Integer getProduct_img_id() {
        return product_img_id;
    }

    public void setProduct_img_id(Integer product_img_id) {
        this.product_img_id = product_img_id;
    }

    public Boolean getIs_default() {
        return is_default;
    }

    public void setIs_default(Boolean is_default) {
        this.is_default = is_default;
    }

    @Override
    public String toString() {
        return "PmsSkuImage{" +
                "id=" + id +
                ", sku_id=" + sku_id +
                ", img_name='" + img_name + '\'' +
                ", img_url='" + img_url + '\'' +
                ", product_img_id=" + product_img_id +
                ", is_default=" + is_default +
                '}';
    }
}
