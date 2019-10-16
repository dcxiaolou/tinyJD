package com.dcxiaolou.tinyJD.bean;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class PmsBaseCatalog2 implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer catalog1_id;
    @Transient
    private List<PmsBaseCatalog3> catalog3s;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCatalog1_id() {
        return catalog1_id;
    }

    public void setCatalog1_id(Integer catalog1_id) {
        this.catalog1_id = catalog1_id;
    }

    public List<PmsBaseCatalog3> getCatalog3s() {
        return catalog3s;
    }

    public void setCatalog3s(List<PmsBaseCatalog3> catalog3s) {
        this.catalog3s = catalog3s;
    }
}
