package com.Sumanta.JobListing.utils;

import com.Sumanta.JobListing.Entity.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

import static io.jsonwebtoken.Jwts.builder;

public class  JwtTokenUtil {
    private static final String secret = "dsdsgdskj523kj523kjkljdkghsdgsekj23523542354j23klj23klt4kwhntgewklgkljgikejtk2jtk23j5kl23j52";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes()) ;

    public static String GenerateToken(String userId, Role role ) {
        return builder().
                        setSubject(userId)
                        .claim("role", role)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*10))
                        .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                        .compact();
    }

    public static boolean validateToken(String JwtToken) {
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

    public static String getUserIdFromToken(String jwtToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();
    }

    public static Role getUserRoleFromToken(String jwtToken) {
        String roleStr = Jwts
                .parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .get("role", String.class);
        return Role.valueOf(roleStr);
    }

    public static String extractTokenFromRequest(HttpServletRequest request) {
        String Bearer = request.getHeader("Authorization");
        if(Bearer == null || !Bearer.startsWith("Bearer ")) {
            return "notFound";
        }
        return Bearer.substring(7);
    }
}
