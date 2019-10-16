package com.dcxiaolou.tinyJD.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dcxiaolou.tinyJD.bean.PmsSearchSkuInfo;
import com.dcxiaolou.tinyJD.bean.PmsSkuInfo;
import com.dcxiaolou.tinyJD.service.PmsSkuInfoService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TinyjdSearchWebApplicationTests {

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @Autowired
    private JestClient jestClient;

    @Test
    public void contextLoads() throws InvocationTargetException, IllegalAccessException, IOException {
        //查询mysql
        List<PmsSkuInfo> skuInfos = pmsSkuInfoService.getAll();
        //转换为es的数据结构
        // 使用commons.bean工具类
        List<PmsSearchSkuInfo> searchSkuInfos = new ArrayList<>();
        for (PmsSkuInfo skuInfo : skuInfos) {
            PmsSearchSkuInfo searchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(searchSkuInfo, skuInfo);
            searchSkuInfos.add(searchSkuInfo);
        }
        // 导入es

        for (PmsSearchSkuInfo skuInfo : searchSkuInfos) {
            Index index = new Index.Builder(skuInfo).index("gmall_pms").type("PmsSkuInfo").id(skuInfo.getId()).build();
            jestClient.execute(index);
        }
    }

}
