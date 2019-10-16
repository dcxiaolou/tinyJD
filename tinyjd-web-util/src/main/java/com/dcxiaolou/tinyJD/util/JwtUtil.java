package com.dcxiaolou.tinyJD.util;

import io.jsonwebtoken.*;

import java.util.Map;

public class JwtUtil {
    /*
    * key 秘钥
    * param 信息（用户信息）
    * salt 盐值（ip、时间）
    * */
    public static String encode(String key, Map<String, Object> param, String salt) {
        if (salt != null) {
            key += salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key);
        jwtBuilder = jwtBuilder.setClaims(param);
        String token = jwtBuilder.compact();
        return token;
    }

    public static Map<String, Object> decode(String token, String key, String salt) {
        Claims claims = null;
        if (salt != null) {
            key += salt;
        }
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            return null;
        }
        return claims;
    }
}
