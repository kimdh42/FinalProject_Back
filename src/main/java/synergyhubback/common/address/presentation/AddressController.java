package synergyhubback.common.address.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synergyhubback.common.address.domain.dto.AddressSelect;
import synergyhubback.common.address.service.AddressService;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /* 주소록 회원 전체 조회 */
    @GetMapping("/select")
    public ResponseEntity<List<AddressSelect>> getAddressSelect() {
        List<AddressSelect> addresses = addressService.getAllAddress();

        return ResponseEntity.ok(addresses);
    }

}
