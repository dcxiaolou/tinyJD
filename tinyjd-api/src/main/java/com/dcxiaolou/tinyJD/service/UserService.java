package com.dcxiaolou.tinyJD.service;

import com.dcxiaolou.tinyJD.bean.UmsMember;
import com.dcxiaolou.tinyJD.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {

    List<UmsMember> selectAllUsers();

    UmsMember login(UmsMember umsMember);

    void addUserToken(String token, String memberId);

    UmsMember addOauthUSer(UmsMember umsMember);

    int delOauthUSer(UmsMember umsMember);

    UmsMember checkUser(UmsMember checkUmsMember);

    boolean checkUserByUserNameOrPhone(String param, String type);

    List<UmsMemberReceiveAddress> getReceiverAddressByMemberId(String memberId);

    UmsMemberReceiveAddress getReceiverAddressById(String receiveAddressId);

    int register(String username, String password, String phone);
}
