package com.dcxiaolou.tinyJD.interceptor;

import com.alibaba.fastjson.JSON;
import com.dcxiaolou.tinyJD.annotations.LoginRequired;
import com.dcxiaolou.tinyJD.util.HttpClientUtil;
import com.dcxiaolou.tinyJD.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("interceptor...");
        //判断被拦截的请求方法的注解（是否是需要拦截的）
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Annotation annotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
        if (annotation == null)
            return true;
        //获取token以验证
        String token = "";

        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        if (StringUtils.isNotBlank(oldToken))
            token = oldToken;

        String newToken = request.getParameter("token");
        if (StringUtils.isNotBlank(newToken))
            token = newToken;

        //获取该请求是否必须登陆成功
        boolean loginSuccess = ((LoginRequired) annotation).loginSuccess();

        String success = "fail";
        Map<String, String> successMap = new HashMap<>();
        if (StringUtils.isNotBlank(token)) {
            System.out.println(token);
            //调用验证中心进行验证

            //这里获取的ip为用户原始发送的请求的ip
            String ip = request.getHeader("x-forwarded-for");//获取通过nginx转发给客户端的ip
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();//从request中获取ip
                if (StringUtils.isBlank(ip))
                    ip = "127.0.0.1";
            }
            String successJson = HttpClientUtil.doGet("http://passport.gmall.com:8085/verify?token=" + token + "&currentIp=" + ip);

            successMap = JSON.parseObject(successJson, Map.class);

            success = successMap.get("status");
        }

        if (loginSuccess) {
            //用户必须登录
            if (!"success".equals(success)) {
                //踢回验证中心
                StringBuffer url = request.getRequestURL();

                //拦截器所在的应用服务会重新发送一个请求，该请求为用户所要请求的应用
                response.sendRedirect("http://passport.gmall.com:8085/login?returnUrl=" + url);
                return false;
            }
            //将token携带的用户数据写入
            System.out.println(successMap.get("memberId") + " " + successMap.get("username"));
            request.setAttribute("memberId", successMap.get("memberId"));
            request.setAttribute("username", successMap.get("username"));

            //验证通过，覆盖cookie中的token
            if (StringUtils.isNotBlank(token)) {
                CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
            }
        } else {
            //用户未登录也可以使用，但必须验证
            if ("success".equals(success)) {
                request.setAttribute("memberId", successMap.get("memberId"));
                request.setAttribute("username", successMap.get("username"));

                //验证通过，覆盖cookie中的token
                if (StringUtils.isNotBlank(token)) {
                    CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
                }
            }
        }

        return true;
    }
}
