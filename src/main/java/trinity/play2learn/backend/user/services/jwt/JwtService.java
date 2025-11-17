package trinity.play2learn.backend.user.services.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.services.jwt.interfaces.IJwtService;

@Service
public class JwtService implements IJwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 15; // Token expira en 15 minutos
    private final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 8; // Token expira en 8 horas

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Genera el access token con duracion de 10 minutos
    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    // Genera el refresh token con duracion de 7 dias
    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    @Override
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private String generateToken(UserDetails userDetails, long expirationTime) {
        return generateToken(getExtraClaims(userDetails), userDetails, expirationTime);
    }

    // Sobrecarga para generar token con claims adicionales (Role)
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationTime) {

        return Jwts.builder()
                .setClaims(extraClaims) // Aca se setea el Role
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Este metodo extrae todas las claims del JWT pero ademas, valida la firma del
    // metodo (A traves de parseClaimsJws)
    // Si la firma del metodo es invalida, este metodo lanza una excepcion
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Devuelve las extraclaims del JWT. Por el momento se usa unicamente para el
    // role.
    private Map<String, Object> getExtraClaims(UserDetails userDetails) {

        return getRoleClaim(userDetails);
    }

    private Map<String, Object> getRoleClaim(UserDetails userDetails) {
        return Map.of(
                "role",
                userDetails.getAuthorities()
                        .stream().findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse(Role.ROLE_STUDENT.name()));
    }

}