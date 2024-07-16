package synergyhubback.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synergyhubback.auth.dto.LoginDto;
import synergyhubback.auth.dto.TokenDto;
import synergyhubback.auth.type.CustomUser;
import synergyhubback.auth.util.TokenUtils;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.service.EmployeeService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    @Autowired
    private final EmployeeService employeeService;

    @Override
    public UserDetails loadUserByUsername(String emp_code) throws UsernameNotFoundException {
        LoginDto loginDto = employeeService.findByEmpCode(Integer.parseInt(emp_code));

        return User.builder()
                .username(String.valueOf(loginDto.getEmp_code()))
                .password(String.valueOf(loginDto.getEmp_pass()))
                .build();
    }

    public void updateRefreshToken(int emp_code, String refreshToken) {
        employeeService.updateRefreshToken(emp_code, refreshToken);
    }

    public TokenDto checkRefreshTokenAndReIssueToken(String refreshToken) {

        LoginDto loginDto = employeeService.findByRefreshToken(refreshToken);
        String reIssuedRefreshToken = TokenUtils.createRefreshToken();
        String reIssuedAccessToken = TokenUtils.createAccessToken(getEmpInfo(loginDto));
        employeeService.updateRefreshToken(loginDto.getEmp_code(), reIssuedRefreshToken);
        return TokenDto.of(reIssuedAccessToken, reIssuedRefreshToken);

    }

    private Map<String, Object> getEmpInfo(LoginDto loginDto) {
        return Map.of(
                "emp_code", loginDto.getEmp_code()
        );
    }

    public void saveAuthentication(int emp_code) {

        LoginDto loginDto = employeeService.findByEmpCode(emp_code);

        UserDetails user = User.builder()
                .username(String.valueOf(loginDto.getEmp_code()))
                .password(String.valueOf(loginDto.getEmp_pass()))
                .build();

        CustomUser customUser = new CustomUser(loginDto.getEmp_code(), user);

        Authentication authentication
                = new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }


}
