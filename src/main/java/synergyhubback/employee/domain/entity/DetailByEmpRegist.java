package synergyhubback.employee.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "detail_by_emp_regist")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DetailByEmpRegist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int erd_code;

    private String erd_num;

    private String erd_title;

    private String erd_writer;

    private LocalDate erd_registdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_code")
    private Employee employee;

    public DetailByEmpRegist(String erd_num, String erd_title, String erd_writer, LocalDate erd_registdate, Employee employee) {

        this.erd_num = erd_num;
        this.erd_title = erd_title;
        this.erd_writer = erd_writer;
        this.erd_registdate = erd_registdate;
        this.employee = employee;
        employee.addEmpRegistDetail(this); // Employee 엔티티의 메서드를 통해 연결
    }
}
