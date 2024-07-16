package synergyhubback.common.address.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synergyhubback.common.address.domain.dto.AddressSelect;
import synergyhubback.common.address.domain.repository.AddressDeptRepository;
import synergyhubback.common.address.domain.repository.AddressEmpRepository;
import synergyhubback.common.address.domain.repository.AddressPositionRepository;
import synergyhubback.common.address.domain.repository.AddressTitleRepository;
import synergyhubback.employee.domain.entity.Department;
import synergyhubback.employee.domain.entity.Employee;
import synergyhubback.employee.domain.entity.Position;
import synergyhubback.employee.domain.entity.Title;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {

    private final AddressDeptRepository addressDeptRepository;
    private final AddressPositionRepository addressPositionRepository;
    private final AddressEmpRepository addressEmpRepository;
    private final AddressTitleRepository addressTitleRepository;
    
    /* 전체 회원 정보 조회*/
    public List<AddressSelect> getAllAddress() {
        List<Employee> employees = addressEmpRepository.findAll();

        return employees.stream().map(employee -> {
            Department department = addressDeptRepository.findById(employee.getDepartment().getDept_code()).orElse(null);
            Position position = addressPositionRepository.findById(employee.getPosition().getPosition_code()).orElse(null);
            Title title = addressTitleRepository.findById(employee.getTitle().getTitle_code()).orElse(null);

            return new AddressSelect(
                    employee.getEmp_code(),
                    employee.getEmp_name(),
                    employee.getEmail(),
                    employee.getPhone(),
                    employee.getAddress(),
                    employee.getAccount_num(),
                    employee.getHire_date(),
                    employee.getEmp_status(),
                    department != null ? department.getDept_title() : "",
                    position != null ? position.getPosition_name() : "",
                    title != null ? title.getTitle_name() : ""
            );
        }).collect(Collectors.toList());
    }

    public List<AddressSelect> getMsgBlockAddress(int empCode) {

        List<Employee> employees = addressEmpRepository.findAddressNotBlkEmp(empCode);

        return employees.stream().map(employee -> {
            Department department = addressDeptRepository.findById(employee.getDepartment().getDept_code()).orElse(null);
            Position position = addressPositionRepository.findById(employee.getPosition().getPosition_code()).orElse(null);
            Title title = addressTitleRepository.findById(employee.getTitle().getTitle_code()).orElse(null);

            return new AddressSelect(
                    employee.getEmp_code(),
                    employee.getEmp_name(),
                    employee.getEmail(),
                    employee.getPhone(),
                    employee.getAddress(),
                    employee.getAccount_num(),
                    employee.getHire_date(),
                    employee.getEmp_status(),
                    department != null ? department.getDept_title() : "",
                    position != null ? position.getPosition_name() : "",
                    title != null ? title.getTitle_name() : ""
            );
        }).collect(Collectors.toList());
    }
}
