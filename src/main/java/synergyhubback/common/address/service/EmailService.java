package synergyhubback.common.address.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import synergyhubback.employee.domain.entity.Employee;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendNewEmp(Employee employee) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(employee.getEmail());

        helper.setSubject("Synerge Hub 입사를 축하드립니다.");

        helper.setText("안녕하세요 " + employee.getEmp_name() + " 님" + ",\n\n Synerge Hub 입사를 축하드립니다."
                        + "\n\n 자사 그룹웨어 아이디와 패스워드를 안내해 드리겠습니다."
                        + "\n 아이디 : " + employee.getEmp_code()
                        + "\n 비밀번호 : " + employee.getEmp_code() + " 입니다."
                        + "\n\n 비밀번호는 로그인한 이후 바로 변경해주시길 안내드립니다.");

        emailSender.send(message);
    }

    public void autoSendDayOffEmail(Employee employee, int result) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(employee.getEmail());

        helper.setSubject("[경영지원부] 연차 촉진 안내 메일입니다.");

        helper.setText("안녕하세요 " + employee.getEmp_name() + " 님" + ",\n 본 이메일은 현월 기준 연차를 1/2 이상 사용하지 않은 직원 분들에게 자동으로 발송되는 전체 메일입니다."
                + "\n\n" + employee.getEmp_name() + " 님께서 사용해야할 연차는 총 [ " + result + " 개 ] 입니다."
                + "\n 남은 연휴와 일정 참고하시어 일정에 맞게 본인의 연차를 소진할 수 있도록 협조 부탁드립니다."
                + "\n 오늘도 좋은 하루 되세요."
                + "\n\n 경영지원부 드림.");

        emailSender.send(message);
    }

    public void sendDayOffEmail(Employee employee, int result) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(employee.getEmail());

        helper.setSubject("[경영지원부] 연차 촉진 안내 메일입니다.");

        helper.setText("안녕하세요 " + employee.getEmp_name() + " 님" + ",\n 연차 촉진을 위한 안내 이메일입니다."
                + "\n\n" + employee.getEmp_name() + " 님께서 사용해야할 연차는 총 [ " + result + " 개 ] 입니다."
                + "\n 남은 연휴와 일정 참고하시어 일정에 맞게 본인의 연차를 소진할 수 있도록 협조 부탁드립니다."
                + "\n 오늘도 좋은 하루 되세요."
                + "\n\n 경영지원부 드림.");

        emailSender.send(message);
    }

}
