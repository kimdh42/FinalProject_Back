package synergyhubback.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;

import synergyhubback.attendance.domain.entity.Attendance;
import synergyhubback.attendance.domain.entity.AttendanceStatus;
import synergyhubback.attendance.domain.entity.DayOff;
import synergyhubback.attendance.domain.entity.DayOffBalance;
import synergyhubback.attendance.dto.request.AttendanceRegistRequest;
import synergyhubback.attendance.dto.request.DayOffBalanceRequest;
import synergyhubback.attendance.dto.response.DayOffResponse;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(DayOffResponse.class, DayOff.class)
                .setProvider(request -> DayOff.builder()
                        .doCode(((DayOff) request.getSource()).getDoCode())
                        .doReportDate(((DayOff) request.getSource()).getDoReportDate())
                        .doName(((DayOff) request.getSource()).getDoName())
                        .doUsed(((DayOff) request.getSource()).getDoUsed())
                        .doStartDate(((DayOff) request.getSource()).getDoStartDate())
                        .doEndDate(((DayOff) request.getSource()).getDoEndDate())
                        .doStartTime(((DayOff) request.getSource()).getDoStartTime())
                        .doEndTime(((DayOff) request.getSource()).getDoEndTime())
                        .granted(((DayOff) request.getSource()).getGranted())
                        .dbUsed(((DayOff) request.getSource()).getDbUsed())
                        .remaining(((DayOff) request.getSource()).getRemaining())
                        .build());
        modelMapper.typeMap(DayOffBalanceRequest.class, DayOffBalance.class)
                .setProvider(request -> DayOffBalance.builder()
                        .dbCode(((DayOffBalanceRequest) request.getSource()).getDbCode())
                        .granted(((DayOffBalanceRequest) request.getSource()).getGranted())
                        .remaining(((DayOffBalanceRequest) request.getSource()).getRemaining())
                        .dbUsed(((DayOffBalanceRequest) request.getSource()).getDbUsed())
                        .employee(((DayOffBalanceRequest) request.getSource()).getEmployee())
                        .dbInsertDate(((DayOffBalanceRequest) request.getSource()).getDbInsertDate())
                        .build());
        modelMapper.typeMap(AttendanceRegistRequest.class, Attendance.class)
                .setProvider(request -> Attendance.builder()
                        .atdCode(((AttendanceRegistRequest) request.getSource()).getAtdCode())
                        .atdDate(((AttendanceRegistRequest) request.getSource()).getAtdDate())
                        .atdStartTime(((AttendanceRegistRequest) request.getSource()).getAtdStartTime())
                        .atdEndTime(((AttendanceRegistRequest) request.getSource()).getAtdEndTime())
                        .startTime(((AttendanceRegistRequest) request.getSource()).getStartTime())
                        .endTime(((AttendanceRegistRequest) request.getSource()).getEndTime())
                        .employee(((AttendanceRegistRequest) request.getSource()).getEmployee())
                        .attendanceStatus(((AttendanceRegistRequest) request.getSource()).getAttendanceStatus())
                        .overWork(((AttendanceRegistRequest) request.getSource()).getOverWork())
                        .build());
        return modelMapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

}
