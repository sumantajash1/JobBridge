package com.Sumanta.JobListing.utils;

import com.Sumanta.JobListing.Entity.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

import static io.jsonwebtoken.Jwts.builder;

@Component
public class  JwtTokenUtil {
    private SecretKey SECRET_KEY;

    @PostConstruct
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

    public boolean validateToken(String JwtToken) {
        try {
            Jwts
                    .parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(JwtToken);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserIdFromToken(String jwtToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();
    }

    public Role getUserRoleFromToken(String jwtToken) {
        String roleStr = Jwts
                .parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .get("role", String.class);
        return Role.valueOf(roleStr);
    }
}
