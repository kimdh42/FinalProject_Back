package synergyhubback.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class TokenUtils {

    // JWT 키
    private static String jwtSecretKey;
    // 액세스 토큰 만료 시간
    private static Long accessTokenExpiration;
    // 리프레시 토큰 만료 시간
    private static Long refreshTokenExpiration;

    private static final String BEARER = "Bearer ";

    // 액세스 토큰에서 emp_code 값 추출
    public static String getEmp_Code(String accessToken){
        return Jwts.parserBuilder()
                .setSigningKey(createSignature())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .get("emp_code").toString();
    }


    /* 새로 추가함 */
    /* 액세스 토큰에서 dept_code 값 추출 */
    public static String getDeptCode(String accessToken) {

        return Jwts.parserBuilder()
                .setSigningKey(createSignature())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()

                .get("dept_code", String.class);

    }

    /* 새로 추가함 : 박은비 */
    /* 액세스 토큰에서 title_code 값 추출 */
    public static String getTitleCode(String accessToken) {

        return Jwts.parserBuilder()
                .setSigningKey(createSignature())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()

                .get("title_code", String.class);

    }

    // yml 파일에서 secretKey 값을 jwtSecretKey에 설정
    @Value("${jwt.secret}")
    public void setJwtSecretKey(String jwtSecretKey) {
        TokenUtils.jwtSecretKey = jwtSecretKey;
    }

    // yml 파일에서 액세스 시간 값을 accessTokenExpiration에 설정
    @Value("${jwt.access.expiration}")
    public void setAccessTokenExpiration(Long accessTokenExpiration) {
        TokenUtils.accessTokenExpiration = accessTokenExpiration;
    }

    // yml 파일에서 리프레시 시간 값을 refreshTokenExpiration에 설정
    @Value("${jwt.refresh.expiration}")
    public void setRefreshTokenExpiration(Long refreshTokenExpiration) {
        TokenUtils.refreshTokenExpiration = refreshTokenExpiration;
    }


    public static String createAccessToken(Map<String, Object> empInfo) {
        Claims claims = Jwts.claims().setSubject("AccessToken");
        claims.putAll(empInfo);

        return Jwts.builder()
                .setHeader(createHeader())
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(createSignature(), SignatureAlgorithm.HS512)
                .compact();
    }

    public static String createRefreshToken() {

        return Jwts.builder()
                .setHeader(createHeader())
                .setSubject("RefreshToken")
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(createSignature(), SignatureAlgorithm.HS512)
                .compact();
    }

    private static Key createSignature() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    // header 생성, 토큰의 타입과 발행일 포함
    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();

        header.put("type", "jwt");
        header.put("date", System.currentTimeMillis());

        return header;
    }

    public static String getToken(String token) {
        if (token != null && token.startsWith(BEARER)) {
            return token.replace(BEARER, "");
        }
        return null;
    }

    public static boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(createSignature()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }


}
