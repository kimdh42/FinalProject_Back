package synergyhubback.auth.type;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUser extends User {

    private int emp_code;

    public CustomUser(int emp_code, UserDetails userDetails) {
        super(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        this.emp_code = emp_code;
    }

}
