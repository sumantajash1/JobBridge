package com.Sumanta.JobListing.utils;

import com.Sumanta.JobListing.Entity.Role;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import static io.jsonwebtoken.Jwts.builder;

@Component
public class JwtTokenUtil {
    private SecretKey SECRET_KEY;
    public void init() {
        String secret = "dsdsgdskj523kj523kjkljdkghsdgsekj23523542354j23klj23klt4kwhntgewklgkljgikejtk2jtk23j5kl23j52";
        SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String GenerateToken(String mobileNo, Role role ) {
        return builder().
                        setSubject(mobileNo)
                .claim("role", role)
                .

    }
}
