package com.dcxiaolou.tinyJD.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dcxiaolou.tinyJD.bean.PmsSkuAttrValue;
import com.dcxiaolou.tinyJD.bean.PmsSkuImage;
import com.dcxiaolou.tinyJD.bean.PmsSkuInfo;
import com.dcxiaolou.tinyJD.bean.PmsSkuSaleAttrValue;
import com.dcxiaolou.tinyJD.manage.mapper.PmsSkuAttrValueMapper;
import com.dcxiaolou.tinyJD.manage.mapper.PmsSkuImageMapper;
import com.dcxiaolou.tinyJD.manage.mapper.PmsSkuInfoMapper;
import com.dcxiaolou.tinyJD.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.dcxiaolou.tinyJD.service.PmsSkuInfoService;
import com.dcxiaolou.tinyJD.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.UUID;

@Service
public class PmsSkuInfoServiceImpl implements PmsSkuInfoService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String save(PmsSkuInfo pmsSkuInfo) {
        int result = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        List<PmsSkuAttrValue> pmsSkuAttrValue = pmsSkuInfo.getPmsSkuAttrValue();
        for (PmsSkuAttrValue skuAttrValue : pmsSkuAttrValue) {
            skuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insertSelective(skuAttrValue);
        }
        result += 1;
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValue = pmsSkuInfo.getPmsSkuSaleAttrValue();
        for (PmsSkuSaleAttrValue skuSaleAttrValue : pmsSkuSaleAttrValue) {
            skuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
        }
        result += 1;
        List<PmsSkuImage> pmsSkuImages = pmsSkuInfo.getPmsSkuImages();
        for (PmsSkuImage pmsSkuImage : pmsSkuImages) {
            pmsSkuImage.setSku_id(Integer.parseInt(pmsSkuInfo.getId()));
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
        result += 1;
        if (result == 4)
            return "success";
        else
            return "fail";
    }

    @Override
    public PmsSkuInfo getBySkuId(Integer skuId) {
        PmsSkuInfo pmsSkuInfo;
        //连接缓存
        Jedis jedis = redisUtil.getJedis();
        //查询缓存
        String key = "sku:" + skuId + ":info";
        String skuJson = jedis.get(key);
        if (StringUtils.isNotBlank(skuJson)) {
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
        } else {
            //缓存中不存在，查询DB
            //为了防止缓存击穿和缓存雪崩，设置分布式锁
            String token = UUID.randomUUID().toString();
            /*
            * token的作用
            * 问题：如果在redis中的锁已经过期，然后锁过期的那个请求又执行完，回来删锁，此时删除的锁是其他请求
            * 创建的锁（因为此时redis中还没有相应的数据，其他请求来获取数据时就会先创建锁，
            * 创建的锁的key与上一个请求的key相同）
            * 解决：将锁的值设置为特定的值，只有查询出来的值为自己创建的值，才能删除
            * */
            String ok = jedis.set("sku:" + skuId + ":lock", token, "nx", "px", 10 * 1000);
            if (StringUtils.isNotBlank(ok) && ok.equals("OK")) {
                //缓存锁设置成功，有权在10秒内访问数据库
                pmsSkuInfo = getBySkuIdFromDB(skuId);
                if (pmsSkuInfo != null) {
                    //将查询结果存入redis
                    jedis.set(key, JSON.toJSONString(pmsSkuInfo));
                } else {
                    //数据库中不存在该sku
                    //为了防止缓存渗透，将null或空字符串设置给redis
                    jedis.setex(key, 60 * 3, JSON.toJSONString(""));
                }
                //在访问数据库后，将redis的分布式锁释放
                String localToken = jedis.get("sku:" + skuId + ":lock");
                if (StringUtils.isNotBlank(localToken) && localToken.equals(token)) {
                    /*
                     * 问题：如果碰巧在查询redis锁还没有删除的时候，正在网络传输时，锁过期了。也就是在
                     * jedis.get("sku:" + skuId + ":lock");时锁还没有过期，但在
                     * if (StringUtils.isNotBlank(localToken) && localToken.equals(token))时，锁过时了，
                     * 此时进行jedis.del("sku:" + skuId + ":lock");删除操作，删除的是其他请求的锁
                     * 解决：可以使用lua脚本，在查询到key的同时删除该key，防止高并发下的意外发生
                     *
                     * 其他方法：可以不删除锁的key，让其自动过期，但会影响性能，不推荐
                     * */
                    jedis.del("sku:" + skuId + ":lock");
                }
            } else {
                //分布式锁设置失败，自旋（该线程在睡眠几秒后，重新尝试访问该方法）
                /*
                * 有可能在该线程睡眠期间，其他线程已经获取到了相关数据，并将结果缓存到redis中，
                * 那么在线程重新访问该方法时，就可以直接从redis中获取
                * */
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /*
                * 注意一定要return getBySkuId(skuId);不能是getBySkuId(skuId);
                * 如果只是getBySkuId(skuId);则会开启一个新线程，原来的线程获取不到新的线程取到的值，会一直等待，
                * 新的线程会变成孤儿线程
                * */
                return getBySkuId(skuId);
            }
        }
        //关闭连接
        jedis.close();
        return pmsSkuInfo;
    }

    private PmsSkuInfo getBySkuIdFromDB(Integer skuId) {
        Example e = new Example(PmsSkuInfo.class);
        e.createCriteria().andEqualTo("id", skuId);
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectByExample(e).get(0);

        e = new Example(PmsSkuAttrValue.class);
        e.createCriteria().andEqualTo("skuId", skuId);
        pmsSkuInfo.setPmsSkuAttrValue(pmsSkuAttrValueMapper.selectByExample(e));
        e = new Example(PmsSkuSaleAttrValue.class);
        e.createCriteria().andEqualTo("skuId", skuId);
        pmsSkuInfo.setPmsSkuSaleAttrValue(pmsSkuSaleAttrValueMapper.selectByExample(e));
        e = new Example(PmsSkuImage.class);
        e.createCriteria().andEqualTo("sku_id", skuId);
        pmsSkuInfo.setPmsSkuImages(pmsSkuImageMapper.selectByExample(e));

        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(Integer skuId) {
        return pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(skuId);
    }

    @Override
    public List<PmsSkuInfo> getAll() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> pmsSkuAttrValueList = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setPmsSkuAttrValue(pmsSkuAttrValueList);
        }

        return pmsSkuInfos;
    }

    @Override
    public boolean checkPrice(String productSkuId, String price) {
        boolean b = false;
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(productSkuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        if (skuInfo.getPrice().equals(price))
            b = true;
        return b;
    }
}
