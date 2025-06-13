package trinity.play2learn.backend.user.services.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm; 
import io.jsonwebtoken.security.Keys;
import trinity.play2learn.backend.user.services.jwt.interfaces.IJwtService;

@Service
public class JwtService implements IJwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(getExtraClaims(userDetails), userDetails);
    }

    // Sobrecarga para generar token con claims adicionales
    @Override
    public String generateToken( Map<String, Object> extraClaims, UserDetails userDetails) {


        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + getExpirationTime()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256) 
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Setea el tiempo de expiracion
    private long getExpirationTime() {
        return 1000 * 60 * 60 * 24; // Token expira en 24 horas
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Devuelve las extraclaims del JWT. Por el momento se usa unicamente para el rol.
    private HashMap<String , Object> getExtraClaims(UserDetails userDetails) {
        HashMap<String , Object> claims = new HashMap<>();
        
        //Extraigo el rol del UserDetails.Esto teniendo en cuenta que los usuarios tienen un unico rol en el sistema
        String role = userDetails.getAuthorities()
        .stream()
        .findFirst()
        .map(grantedAuthority -> grantedAuthority.getAuthority())
        .orElse("ROLE_STUDENT"); // De no tener ningun rol asignado, se le asigna el de STUDENT por defecto (Para evitar nullPointerException)


        claims.put("role" , role);

        return claims;
    }
    
}