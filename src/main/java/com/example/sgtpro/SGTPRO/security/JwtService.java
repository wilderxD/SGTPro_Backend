package com.example.sgtpro.SGTPRO.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    
    private final String secretKey;
    private final long jwtExpiration;
    private final long refreshExpiration;
    
    public JwtService(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.expiration}") long jwtExpiration,
            @Value("${jwt.refresh-expiration:604800000}") long refreshExpiration){
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;
    }
    
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }
    
    public String generateToken(HashMap<String, Object> extraClaims, UserDetails userDetails){
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }
    
    public String generateRefreshToken(HashMap<String, Object> extraClaims, UserDetails userDetails){
        return buildToken(extraClaims, userDetails, refreshExpiration);
    }
    
    private String buildToken(HashMap<String, Object> extraClaims, UserDetails userDetails, long expiration){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    
    public boolean canBeRefreshed(String token){
        try {
            Claims claims = extractAllClaimsNoExpirationCheck(token);
            Date expiration = claims.getExpiration();
            long maxRefreshMs = 7L * 24 * 60 * 60 * 1000;
            return expiration.getTime() + maxRefreshMs > System.currentTimeMillis();
        } catch (Exception e) {
            return false;
        }
    }
    
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsernameNoExpirationCheck(String token){
        return extractAllClaimsNoExpirationCheck(token).getSubject();
    }

    private Claims extractAllClaimsNoExpirationCheck(String token){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    
    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
}
