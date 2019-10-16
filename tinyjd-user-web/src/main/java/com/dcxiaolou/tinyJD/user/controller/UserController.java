package com.dcxiaolou.tinyJD.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dcxiaolou.tinyJD.bean.UmsMember;
import com.dcxiaolou.tinyJD.bean.UmsMemberReceiveAddress;
import com.dcxiaolou.tinyJD.service.UserReceiveAddressService;
import com.dcxiaolou.tinyJD.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {

    @Reference
    private UserService userService;

    @Reference
    private UserReceiveAddressService userReceiveAddressService;

    @ResponseBody
    @RequestMapping("/selectAllUsers")
    public List<UmsMember> selectAllUsers() {
        return userService.selectAllUsers();
    }

    @ResponseBody
    @RequestMapping("/selectAllUserReceiveAddress")
    public List<UmsMemberReceiveAddress> selectAllUserReceiveAddress(Integer id) {
        return userReceiveAddressService.selectAllUserAddress(id);
    }

}
