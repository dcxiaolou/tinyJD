package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog3;

import java.util.List;

public interface Catalog3Service {

    List<PmsBaseCatalog3> selectCatalog3sByCatalog2Id(Integer catalog2Id);

}
