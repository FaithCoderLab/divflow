package faithcoderlab.divflow.security;

import faithcoderlab.divflow.model.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long tokenValidTime;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.tokenValidTime = tokenValidityInSeconds * 1000;
    }

    public String generateToken(Member member) {
        Claims claims = Jwts.claims().setSubject(member.getUsername());
        claims.put("roles", member.getRole());

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
