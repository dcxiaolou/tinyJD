package com.dcxiaolou.tinyJD.bean;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class PmsBaseAttrInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String attr_name;
    private Integer catalog3_id;
    private Boolean is_enabled;
    @Transient
    private List<PmsBaseAttrValue> attrValues;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAttr_name() {
        return attr_name;
    }

    public void setAttr_name(String attr_name) {
        this.attr_name = attr_name;
    }

    public Integer getCatalog3_id() {
        return catalog3_id;
    }

    public void setCatalog3_id(Integer catalog3_id) {
        this.catalog3_id = catalog3_id;
    }

    public Boolean getIs_enabled() {
        return is_enabled;
    }

    public void setIs_enabled(Boolean is_enabled) {
        this.is_enabled = is_enabled;
    }

    public List<PmsBaseAttrValue> getAttrValues() {
        return attrValues;
    }

    public void setAttrValues(List<PmsBaseAttrValue> attrValues) {
        this.attrValues = attrValues;
    }

    @Override
    public String toString() {
        return "PmsBaseAttrInfo{" +
                "id=" + id +
                ", attr_name='" + attr_name + '\'' +
                ", catalog3_id=" + catalog3_id +
                ", is_enabled=" + is_enabled +
                ", attrValues=" + attrValues +
                '}';
    }
}
