package com.bridgelabz.rabbitmq_userregistration.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.springframework.stereotype.Component;

@Component
public class TokenUtility {
    private static final String TOKEN_SECRET = "Sameer";

    public String createToken(Integer id) {
        //to set algorithm
        try {
            Algorithm algo = Algorithm.HMAC256(TOKEN_SECRET);
            String token = JWT.create().withClaim("id_key", id).sign(algo);
            return token;
        } catch (JWTCreationException exception) {
            exception.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int decodeToken(String token) throws SignatureVerificationException {
        //for verification algorithm
        Verification verification = null;
        try {
            verification = JWT.require(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        JWTVerifier jwtVerifier = verification.build();
        //to decode token
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        Claim idClaim = decodedJWT.getClaim("id_key");
        int id = idClaim.asInt();
        return id;
    }
}
