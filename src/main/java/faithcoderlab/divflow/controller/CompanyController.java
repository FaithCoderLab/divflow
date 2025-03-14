package faithcoderlab.divflow.controller;

import faithcoderlab.divflow.dto.CompanyDto;
import faithcoderlab.divflow.service.CompanyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        List<String> result = companyService.getCompanyNamesByPrefix(keyword);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<?> getAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("company get all -> page: {}, size: {}", page, size);
        Page<CompanyDto> companies = companyService.getAllCompanies(page, size);
        return ResponseEntity.ok(companies);
    }

    @DeleteMapping("/{ticker}")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        log.debug("company delete -> " + ticker);
        companyService.deleteCompany(ticker);
        return ResponseEntity.ok().build();
    }
}
