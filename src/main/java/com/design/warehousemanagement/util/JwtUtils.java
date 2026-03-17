package com.design.warehousemanagement.util;




import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

/**
 * @author wwp
 */
public class JwtUtils {
    private static String signKey="drivingschool";
    private static Long expire=43200000L;
    /**
     *生成jwt
     * @param claims
     * @return
     */
    public static String generateJwt(Map<String,Object>claims){

        String jwt= Jwts.builder()
                //签名算法
                .signWith(SignatureAlgorithm.HS256,signKey)
                //自定义内容（载荷）
                .setClaims(claims)
                //设置有效期为24小时
                .setExpiration(new Date(System.currentTimeMillis()+expire))
                .compact();
        //将token写到响应头里
        return jwt;
    }

    /**
     * 解析JWT令牌
     * @param jwt
     * @return
     */
    public static Claims parseJWT(String jwt){
        Claims claims=Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }
}
