package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.PmsBaseCatalog2;

import java.util.List;

public interface Catalog2Service {

    List<PmsBaseCatalog2> selectCatalog2sByCatalog1Id(Integer catalog1Id);

}
