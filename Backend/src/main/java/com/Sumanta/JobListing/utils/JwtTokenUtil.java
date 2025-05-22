package com.Sumanta.JobListing.utils;

import com.Sumanta.JobListing.Entity.Role;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

import static io.jsonwebtoken.Jwts.builder;

@Component
public class JwtTokenUtil {
    private SecretKey SECRET_KEY;
    public void init() {
        String secret = "dsdsgdskj523kj523kjkljdkghsdgsekj23523542354j23klj23klt4kwhntgewklgkljgikejtk2jtk23j5kl23j52";
        SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String GenerateToken(String userId, Role role ) {
        return builder().
                        setSubject(userId)
                        .claim("role", role)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*10))
                        .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                        .compact();
    }


}
