package com.dcxiaolou.tinyJD.bean;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class PmsBaseCatalog1 implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Transient //mybatis 暂时的，表明不是数据库中的字段
    private List<PmsBaseCatalog2> catalog2s;

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

    public List<PmsBaseCatalog2> getCatalog2s() {
        return catalog2s;
    }

    public void setCatalog2s(List<PmsBaseCatalog2> catalog2s) {
        this.catalog2s = catalog2s;
    }
}
