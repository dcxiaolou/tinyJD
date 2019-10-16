package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.PmsSearchParam;
import com.dcxiaolou.tinyJD.bean.PmsSearchSkuInfo;

import java.util.List;

public interface PmsSearchParamService {

    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);

}
