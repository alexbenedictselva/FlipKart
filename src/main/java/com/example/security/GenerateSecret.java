package com.example.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;

import javax.crypto.SecretKey;

public class GenerateSecret {

    public static void main(String[] args) {

        SecretKey key = Jwts.SIG.HS256.key().build();

        String secret =
                Encoders.BASE64.encode(key.getEncoded());

        System.out.println(secret);
    }
}