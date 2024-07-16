package synergyhubback.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import synergyhubback.auth.filter.CustomAuthenticationFilter;
import synergyhubback.auth.filter.JwtAuthenticationFilter;
import synergyhubback.auth.handler.JwtAccessDeniedHandler;
import synergyhubback.auth.handler.JwtAuthenticationEntryPoint;
import synergyhubback.auth.handler.LoginFailureHandler;
import synergyhubback.auth.handler.LoginSuccessHandler;
import synergyhubback.auth.service.AuthService;

import java.util.Arrays;

@RequiredArgsConstructor    // 메소드 레벨 제어, 반환 값 권한 부여에 활용
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.
                // CSRF 공격 방어 비활성화
                csrf(AbstractHttpConfigurer::disable)
                // 세션으로 로그인 상태 관리 X => STATELESS 설정
                .sessionManagement(sessionManage -> sessionManage.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // formLogin 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // 요청 권한 체크
                .authorizeHttpRequests(auth -> {
                    // image url, login 페이지는 permit, 나머지 url은 전부 authenticatied() 사용
                    auth.requestMatchers(HttpMethod.GET, "/profileimgs/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/approvalimgs/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/postimgs/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/messageimgs/**").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/employee/**").permitAll();
                    auth.requestMatchers("/emp/auth/login").permitAll();
                    auth.anyRequest().permitAll();
                })
                // 로그인 필터 이전 커스텀 로그인 필터 설정
                .addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                // 모든 요청에 대해서 토큰을 확인하는 필터
                .addFilterBefore(jwtAuthenticationFilter(), BasicAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling.accessDeniedHandler(jwtAccessDeniedHandler());
                    exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint());
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .build();
    }

    /* 리소스 허용 여부 결정 */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE", "PATCH"));
        corsConfiguration.setAllowedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "headers",
                "Content-Type", "Authorization", "X-Requested-With", "Access-Token", "Refresh-Token"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Access-Token", "Refresh-Token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(authService);
        return new ProviderManager(provider);
    }

    // 로그인 실패 핸들러 빈
    @Bean
    LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    // 로그인 성공 핸들러 빈
    @Bean
    LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(authService);
    }

    // 로그인 시 동작할 CustomFilter Bean
    @Bean
    CustomAuthenticationFilter customAuthenticationFilter() {

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter();
        // AuthenticationManager 설정
        customAuthenticationFilter.setAuthenticationManager(authenticationManager());
        // Login Fail Handler 설정
        customAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());
        // Login Success Handler 설정
        customAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler());

        return customAuthenticationFilter;
    }

    // JWT token 인증 필터
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authService);
    }

    // 인증 실패 시 동작 핸들러
    @Bean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    // 인가 실패 시 동작 핸들러
    @Bean
    JwtAccessDeniedHandler jwtAccessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }


}
