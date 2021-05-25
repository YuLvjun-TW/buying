package com.muke.buying;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtTest {
    @Test
    public void testJwt() {
        long expire = System.currentTimeMillis() + 60 * 1000;
        JwtBuilder jwtBuilder = Jwts.builder()
                //唯一id{"jti":"111"}
                .setId("111")
                //接受的用户{"sub":"xiaoming"}
                .setSubject("xiaoming")
                //签发时间
                .setIssuedAt(new Date())
                //签名算法，密钥   {"alg":"HS256"}
                .signWith(SignatureAlgorithm.HS256, "salt123")
                .setExpiration(new Date(expire))
                .claim("name", "Bob")
                .claim("color","red");

        //签发token
        String token = jwtBuilder.compact();
        System.out.println(token);

        String[] split = token.split("\\.");
        //{"alg":"HS256"}
        System.out.println(Base64Codec.BASE64.decodeToString(split[0]));
        //{"jti":"111","sub":"xiaoming","iat":1621914516,"exp":1621914576,"name":"Bob","color":"red"
        System.out.println(Base64Codec.BASE64.decodeToString(split[1]));
        //这个会乱码
        System.out.println(Base64Codec.BASE64.decodeToString(split[2]));
    }

    @Test
    public void testParseToken() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMTEiLCJzdWIiOiJ4aWFvbWluZyIsImlhdCI6MTYyMTkxNDUxNiwiZXhwIjoxNjIxOTE0NTc2LCJuYW1lIjoiQm9iIiwiY29sb3IiOiJyZWQifQ.wiNwqLhbU2D8-CV4JUsa2Uu_-4XxnHVtQdb93i56c20";
        //解析token，获取claims，jwt中荷载声明的对象
        Claims claims = (Claims) Jwts.parser()
                .setSigningKey("salt123")
                .parse(token)
                .getBody();
        assertEquals("111", claims.getId());
        assertEquals("xiaoming", claims.getSubject());
        assertEquals("2021-05-25", new SimpleDateFormat("yyyy-MM-dd").format(claims.getIssuedAt()));

        assertEquals("Bob",claims.get("name"));
        assertEquals("red",claims.get("color"));
    }
}
