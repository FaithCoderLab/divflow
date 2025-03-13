package faithcoderlab.divflow.controller;

import faithcoderlab.divflow.dto.CompanyDto;
import faithcoderlab.divflow.service.CompanyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<?> addCompany(@RequestBody CompanyDto request) {
        log.debug("company add -> " + request.getTicker());
        CompanyDto companyDto = companyService.save(request.getTicker());
        return ResponseEntity.ok(companyDto);
    }
}
