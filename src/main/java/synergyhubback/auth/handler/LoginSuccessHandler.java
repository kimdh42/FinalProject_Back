package synergyhubback.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import synergyhubback.auth.service.AuthService;
import synergyhubback.auth.util.TokenUtils;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 로그인 성공 후 저장된 인증 객체에서 정보 꺼내기
        Map<String, Object> empInfo = getEmployeeInfo(authentication);
        log.info("로그인 성공 후 인증 객체에서 꺼낸 정보 : {}", empInfo);

        // access token, refresh token 생성
        String accessToken = TokenUtils.createAccessToken(empInfo);
        String refreshToken = TokenUtils.createRefreshToken();
        log.info("발급된 accessToken : {}", accessToken);
        log.info("발급된 refreshToken : {}", refreshToken);

        // 발급된 refreshToken을 DB에 저장
        authService.updateRefreshToken((Integer) empInfo.get("emp_code"), refreshToken);

        // 응답 헤더에 발급 된 토큰을 담기
        response.setHeader("Access-Token", accessToken);
        response.setHeader("Refresh-Token", refreshToken);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private Map<String, Object> getEmployeeInfo(Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Integer empCode = Integer.parseInt(userDetails.getUsername());

        return Map.of(
                "emp_code", empCode
        );
    }



}
