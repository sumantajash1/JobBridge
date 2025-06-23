package com.Sumanta.JobListing.filters;

import com.Sumanta.JobListing.Entity.Role;
import com.Sumanta.JobListing.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String JwtToken = header.substring(7);
        if(JwtToken == null || JwtToken.isEmpty()) {
           Cookie[] cookies = request.getCookies();
           if(cookies != null) {
               for(Cookie cookie : cookies) {
                   if(cookie.getName().equals("jwtToken")) {
                       JwtToken = cookie.getValue();
                       break;
                   }
               }
           }
        }
        if(JwtToken != null && JwtTokenUtil.validateToken(JwtToken)) {
            String userID = JwtTokenUtil.getUserIdFromToken(JwtToken);
            Role role = JwtTokenUtil.getUserRoleFromToken(JwtToken);
            Collection<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + role.name())
            );
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userID, null, authorities
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
