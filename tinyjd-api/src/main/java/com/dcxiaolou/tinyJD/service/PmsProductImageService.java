package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.PmsProductImage;

import java.util.List;

public interface PmsProductImageService {

    List<PmsProductImage> getBySPUId(Integer SPUId);

}
