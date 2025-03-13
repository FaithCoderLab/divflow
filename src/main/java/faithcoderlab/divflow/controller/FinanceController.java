package faithcoderlab.divflow.controller;

import faithcoderlab.divflow.dto.ScrapedResult;
import faithcoderlab.divflow.exception.CustomException;
import faithcoderlab.divflow.exception.ErrorCode;
import faithcoderlab.divflow.service.FinanceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/finance")
@AllArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
        log.debug("finance/dividend/{companyName} -> " + companyName);

        try {
            ScrapedResult result = financeService.getDividendByCompanyName(companyName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to get dividend info: " + e.getMessage());
            throw new CustomException(ErrorCode.COMPANY_NOT_FOUND);
        }
    }
}
