package synergyhubback.auth.filter;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;
import synergyhubback.auth.dto.TokenDto;
import synergyhubback.auth.service.AuthService;
import synergyhubback.auth.util.TokenUtils;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. 사용자 헤더에서 refresh token 추출
        String refreshToken = TokenUtils.getToken(request.getHeader("Refresh-Token"));

        // 2. Refresh Token이 있을 때 -> DB에서 Token 일치 여부 후 일치하면 Token 재발급 후 응답
        if (refreshToken != null && TokenUtils.isValidToken(refreshToken)) {
            TokenDto token = authService.checkRefreshTokenAndReIssueToken(refreshToken);
            response.setHeader("Access-Token", token.getAccessToken());
            response.setHeader("Refresh-Token", token.getRefreshToken());
            return;
        }

        // 3. Refresh Token 존재 X -> Access Token 추출 후 유효성 체크 이후 Authentication 저장 후 기능 수행
        String accessToken = TokenUtils.getToken(request.getHeader("Access-Token"));

        if (accessToken != null && TokenUtils.isValidToken(accessToken)) {
            String emp_code = TokenUtils.getEmp_Code(accessToken);
            authService.saveAuthentication(Integer.parseInt(emp_code));
        }

        // access token을 전달한 경우 다음 필터로 진행
        // token 없이 요청 발생한 경우 다음 필터로 진행
        filterChain.doFilter(request, response);

    }

}
