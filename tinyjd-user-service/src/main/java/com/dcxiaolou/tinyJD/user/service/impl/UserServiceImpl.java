package com.dcxiaolou.tinyJD.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dcxiaolou.tinyJD.bean.UmsMember;
import com.dcxiaolou.tinyJD.bean.UmsMemberReceiveAddress;
import com.dcxiaolou.tinyJD.service.UserService;
import com.dcxiaolou.tinyJD.user.mapper.UmsMemberReceiveAddressMapper;
import com.dcxiaolou.tinyJD.user.mapper.UserMapper;
import com.dcxiaolou.tinyJD.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    private RedisUtil redisUtil;

    public List<UmsMember> selectAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public UmsMember login(UmsMember umsMember) {

        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            if (jedis != null) {
                String umsMemberStr = jedis.get("user:" + umsMember.getUsername() + umsMember.getPassword() + ":info");
                if (StringUtils.isNotBlank(umsMemberStr)) {
                    //密码正确
                    UmsMember member = JSON.parseObject(umsMemberStr, UmsMember.class);
                    return member;
                }
            }
            //连接redis失败，开启数据库
            UmsMember umsMemberFromDb = loginFromDb(umsMember);
            if (umsMemberFromDb != null) {
                jedis.setex("user:" + umsMember.getUsername()+umsMember.getPassword() + ":info", 60 * 60 * 24, JSON.toJSONString(umsMemberFromDb));
            }
            return umsMemberFromDb;
        } finally {
            jedis.close();
        }
    }

    @Override
    public void addUserToken(String token, String memberId) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:" + memberId + ":token", 60 * 60 * 24, token);
        jedis.close();
    }

    @Override
    public UmsMember addOauthUSer(UmsMember umsMember) {
        userMapper.insert(umsMember);
        return umsMember; //返回umsMember，防止rpc使主键生成策略失效
    }

    @Override
    public int delOauthUSer(UmsMember umsMember) {
        int delete = userMapper.delete(umsMember);
        return delete;
    }

    @Override
    public UmsMember checkUser(UmsMember checkUmsMember) {
        return userMapper.selectOne(checkUmsMember);
    }

    @Override
    public boolean checkUserByUserNameOrPhone(String param, String type) {
        UmsMember umsMember = new UmsMember();
        if ("1".equals(type)) {
            umsMember.setUsername(param);
            List<UmsMember> memberList = userMapper.select(umsMember);
            if (memberList != null && memberList.size() > 0)
                return true;
        } else if ("2".equals(type)) {
            umsMember.setPhone(param);
            List<UmsMember> memberList = userMapper.select(umsMember);
            if (memberList != null && memberList.size() > 0)
                return true;
        }
        return false;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiverAddressByMemberId(String memberId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMember_id(memberId);
        return umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
    }

    @Override
    public UmsMemberReceiveAddress getReceiverAddressById(String receiveAddressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(receiveAddressId);
        return umsMemberReceiveAddressMapper.selectOne(umsMemberReceiveAddress);
    }

    @Override
    public int register(String username, String password, String phone) {
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername(username);
        umsMember.setPassword(password);
        umsMember.setPhone(phone);
        return userMapper.insert(umsMember);
    }

    private UmsMember loginFromDb(UmsMember umsMember) {
        List<UmsMember> umsMembers = userMapper.select(umsMember);
        if (umsMembers != null && umsMembers.size() > 0)
            return umsMembers.get(0);
        return null;
    }

}
