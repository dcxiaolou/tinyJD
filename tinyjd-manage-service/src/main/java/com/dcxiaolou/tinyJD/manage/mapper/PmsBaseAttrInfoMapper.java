package com.dcxiaolou.tinyJD.manage.mapper;

import com.dcxiaolou.tinyJD.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {
    List<PmsBaseAttrInfo> selectAttrValueByValueId(@Param("valueIdStr") String valueIdStr);
}
