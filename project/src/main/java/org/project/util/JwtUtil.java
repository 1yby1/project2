package org.project.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET="ybybybybybybyby";
    private static final long EXPIRATION_TIME = 14400000; // 1 hour in milliseconds

    public static String generateToken(Long userId) {
        // Implement JWT token generation logic here
        // This is a placeholder implementation
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
}
    public static Long getUserIdFromToken(String token) {
        // Implement JWT token parsing logic here
        // This is a placeholder implementation
        try {
            return Long.parseLong(Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject());
        } catch (Exception e) {
            return null; // Invalid token
        }
    }

    public static boolean validateToken(String token) {
        // Implement JWT token validation logic here
        // This is a placeholder implementation
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Invalid token
        }
    }
}
