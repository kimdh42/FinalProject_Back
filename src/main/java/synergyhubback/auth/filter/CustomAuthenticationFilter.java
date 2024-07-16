package synergyhubback.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private ObjectMapper objectMapper;

    public CustomAuthenticationFilter() {
        // 해당 요청이 올 때 이 필터 동작
        super(new AntPathRequestMatcher("/emp/auth/login", "POST"));
    }

    // 설정된 요청 발생 후 필터 메소드 호출


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        // Request Content-Type 확인
        if (request.getContentType() == null || !request.getContentType().equals("application/json")) {
            throw new AuthenticationServiceException("Content-Type not supported");
        }

        // Request Body 읽어오기
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        // JSON 문자열을 Java Map 타입 변환
        Map<String, String> bodyMap = objectMapper.readValue(body, Map.class);

        // key 값 전달해서 Map id, pwd 꺼내기
        String emp_code = bodyMap.get("emp_code");
        String emp_pass = bodyMap.get("emp_pass");

        // id, pwd가 설정된 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(emp_code, emp_pass);

        // Authentication Manager에게 Authentication Token 전달
        return this.getAuthenticationManager().authenticate(authenticationToken);
    }
}
