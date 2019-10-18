package com.dcxiaolou.tinyJD.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dcxiaolou.tinyJD.bean.OmsCartItem;
import com.dcxiaolou.tinyJD.cart.mapper.CartMapper;
import com.dcxiaolou.tinyJD.service.CartService;
import com.dcxiaolou.tinyJD.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public OmsCartItem ifCartExistByUser(String memberId, String skuId) {
        OmsCartItem t = new OmsCartItem();
        t.setMemberId(memberId);
        t.setProductSkuId(skuId);
        return cartMapper.selectOne(t);
    }

    @Override
    public void save(OmsCartItem omsCartItem) {
        cartMapper.insert(omsCartItem);
    }

    @Override
    public void update(OmsCartItem omsCartItemFromBd) {
        Example e = new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("id", omsCartItemFromBd.getId());
        cartMapper.updateByExample(omsCartItemFromBd, e);
    }

    @Override
    public void flushCartCache(String memberId) {
        Jedis jedis = redisUtil.getJedis();
        OmsCartItem t = new OmsCartItem();
        t.setMemberId(memberId);
        List<OmsCartItem> omsCartItemList = cartMapper.select(t);
        Map<String, String> map = new HashMap<>();
        for (OmsCartItem omsCartItem : omsCartItemList) {
            map.put(omsCartItem.getProductSkuId(), JSON.toJSONString(omsCartItem));
        }
        if (map.isEmpty())
            jedis.del("user:" + memberId + ":cart");
        else
            jedis.hmset("user:" + memberId + ":cart", map);
        jedis.close();
    }

    @Override
    public List<OmsCartItem> cartList(String memberId) {

        List<OmsCartItem> omsCartItems = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals("user:" + memberId + ":cart");
        for (String hval : hvals) {
            omsCartItems.add(JSON.parseObject(hval, OmsCartItem.class));
        }
        jedis.close();
        return omsCartItems;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {

        Example e = new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("memberId", omsCartItem.getMemberId()).andEqualTo("productSkuId", omsCartItem.getProductSkuId());
        cartMapper.updateByExampleSelective(omsCartItem, e);

        //同步缓存
        flushCartCache(omsCartItem.getMemberId());
    }

    @Override
    public void delCartItemBySkuId(String productSkuId) {
        // System.out.println("productSkuId = " + productSkuId);
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setProductSkuId(productSkuId);
        omsCartItem = cartMapper.selectOne(omsCartItem);
        System.out.println("delCartItemBySkuId: " + omsCartItem);
        if (omsCartItem != null) {
            String memberId = omsCartItem.getMemberId();
            cartMapper.delete(omsCartItem);
            // 同步缓存
            flushCartCache(memberId);
        }
    }
}
