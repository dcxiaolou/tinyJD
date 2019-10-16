package com.dcxiaolou.tinyJD.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.PmsProductImage;
import com.dcxiaolou.tinyJD.manage.mapper.PmsProductImageMapper;
import com.dcxiaolou.tinyJD.service.PmsProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class PmsProductImageServiceImpl implements PmsProductImageService {

    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;

    @Override
    public List<PmsProductImage> getBySPUId(Integer SPUId) {
        Example e = new Example(PmsProductImage.class);
        e.createCriteria().andEqualTo("productId", SPUId);
        return pmsProductImageMapper.selectByExample(e);
    }
}
