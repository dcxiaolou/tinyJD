package com.dcxiaolou.tinyJD.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dcxiaolou.tinyJD.annotations.LoginRequired;
import com.dcxiaolou.tinyJD.bean.UmsMember;
import com.dcxiaolou.tinyJD.service.UserService;
import com.dcxiaolou.tinyJD.util.CookieUtil;
import com.dcxiaolou.tinyJD.util.HttpClientUtil;
import com.dcxiaolou.tinyJD.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    private UserService userService;

    @RequestMapping("/login")
    public String login(String returnUrl, Model model) {
        model.addAttribute("returnUrl", returnUrl);
        return "login";
    }

    @RequestMapping("/registerPage")
    public String registerPage(String returnUrl, Model model) {
        model.addAttribute("returnUrl", returnUrl);
        return "register";
    }

    @ResponseBody
    @RequestMapping("/register")
    public String register(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        int index = userService.register(username, password, phone);
        if (index > 0) {
            return "200";
        } else {
            return "error";
        }
    }

    @ResponseBody
    @RequestMapping("user/check/{nameOrPhone}/{type}")
    public String verifyUsernameAndPhoneNumber(@PathVariable("nameOrPhone") String nameOrPhone, @PathVariable("type") String type) {
        boolean b  = userService.checkUserByUserNameOrPhone(nameOrPhone, type);
        return "" + !b;
    }

    @ResponseBody
    @RequestMapping("/loginHandler")
    public String login(UmsMember umsMember, Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String token = "";
        UmsMember member = userService.login(umsMember);
        if (member != null) {
            //登录成功
            //使用jwt制作token
            String memberId = member.getId().toString();
            String username = member.getUsername();

            token = createToken(memberId, username, request);

            CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);

            model.addAttribute("username", username);

            session.setAttribute("username", username);

        } else {
            //登录失败
            token = "fail";
        }
        return token;
    }

    @ResponseBody
    @RequestMapping("/getUserInfo")
    public String getUserInfo(String callback, HttpSession session) {
        Map<String, Object> map=new HashMap<String, Object>();
        Object object = session.getAttribute("username");
        if (object != null) {
            String username = object.toString();
            map.put("username", username);
        } else {
            map.put("username", null);
        }
        return callback+"("+JSON.toJSONString(map)+");";
    }

    @RequestMapping("/logout")
    public String logout(String returnUrl ,HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String username = (String) session.getAttribute("username");
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername(username);
        UmsMember checkUser = userService.checkUser(umsMember);
        String access_token = checkUser.getAccess_token();
        if (access_token != null) {
            // 用户使用的是微博登录，进行注销
            HttpClientUtil.doGet("https://api.weibo.com/oauth2/revokeoauth2?access_token=" + access_token);
            userService.delOauthUSer(umsMember);
        }
        session.removeAttribute("username");
        CookieUtil.deleteCookie(request, response, "oldToken");

        return "redirect:" + returnUrl;
    }

    @ResponseBody
    @RequestMapping("/verify")
    public String verify(String token, String currentIp) {
        System.out.println("token = " + token);
        Map<String, String> map = new HashMap<>();
        //通过JWT校验真假
        //不能直接在这几获取ip，在这里获取到的ip是拦截器请求的应用的ip，而不是用户原始发送的请求的ip
        Map<String, Object> decode = JwtUtil.decode(token, "gmall", currentIp);
        if (decode != null) {
            map.put("status", "success");
            map.put("memberId", decode.get("memberId").toString());
            map.put("username", decode.get("username").toString());
        } else {
            map.put("status", "fail");
        }

        return JSON.toJSONString(map);
    }

    @RequestMapping("/vlogin")
    public String vlogin(String code, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        //使用授权码换取access_token
        /*
        * https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE
        * */
        String getAccessTokenUrl = "https://api.weibo.com/oauth2/access_token?";
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "845972854");
        map.put("client_secret", "e4643afef9ee415b114fd5bcccd96ad6");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://passport.gmall.com:8085/vlogin");
        map.put("code", code); //授权有效期内可以使用，每生成一次授权码，说明用户对第三方数据进行了重新授权，之前的授权码和access_token都会过期不能使用
        String accessTokenJson = HttpClientUtil.doPost(getAccessTokenUrl, map);
        Map<String, String> accessMap = JSON.parseObject(accessTokenJson, Map.class);

        //使用access_token获取用户信息
        //https://api.weibo.com/2/users/show.json
        String uid = accessMap.get("uid");
        String access_token = accessMap.get("access_token");
        String showUserUrl = "https://api.weibo.com/2/users/show.json?access_token=" + access_token + "&uid=" + uid;
        String userJson = HttpClientUtil.doGet(showUserUrl);
        Map<String, String> userMap = JSON.parseObject(userJson, Map.class);
        //将用户信息保存到数据库，用户类型设置为微博用户
        UmsMember umsMember = new UmsMember();

        umsMember.setSource_type(2);
        umsMember.setAccess_code(code);
        umsMember.setAccess_token(access_token);
        umsMember.setSource_uid(userMap.get("idstr"));
        umsMember.setCity(userMap.get("location"));
        umsMember.setStatus("1");
        int g = 0;
        String gender = userMap.get("gender");
        if (gender.equals("m")) {
            g = 1;
        }
        umsMember.setGender("" + g);
        umsMember.setUsername(userMap.get("screen_name"));

        //判断数据库中是否存在该用户
        UmsMember checkUmsMember = new UmsMember();
        checkUmsMember.setSource_uid(umsMember.getSource_uid());
        UmsMember umsMemberChecked = userService.checkUser(checkUmsMember);
        if (umsMemberChecked == null)
            umsMember = userService.addOauthUSer(umsMember);
        else
            umsMember = umsMemberChecked;
        //生成jwt的token，从定向到首页，携带该token
        /*
        * umsMember.getId() 获取到的是null，因为service和controller不在同一主机中，是通过prc调用的，回事主键返回策略失效
        * mybatis的主键返回策略不能跨rpc使用，要在控制层得到生成的主键，需要将返回的db对象返回给控制层
        * */
        String username = umsMember.getUsername();
        String token = createToken(umsMember.getId().toString(), username, request);

        session.setAttribute("username", username);

        CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);

        return "redirect:http://localhost:8083/index?token=" + token;
    }

    private String createToken(String memberId, String username, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        map.put("username", username);

        String ip = request.getHeader("x-forwarded-for");//获取通过nginx转发给客户端的ip
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();//从request中获取ip
            if (StringUtils.isBlank(ip))
                ip = "127.0.0.1";
        }
        //按照设置的加密算法对参数进行加密后，生成token
        String token = JwtUtil.encode("gmall", map, ip);
        //将token存入redis一份
        userService.addUserToken(token, memberId);
        return token;
    }

}
