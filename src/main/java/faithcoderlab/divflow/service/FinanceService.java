package faithcoderlab.divflow.service;

import faithcoderlab.divflow.dto.CompanyDto;
import faithcoderlab.divflow.dto.DividendDto;
import faithcoderlab.divflow.dto.ScrapedResult;
import faithcoderlab.divflow.model.Company;
import faithcoderlab.divflow.model.Dividend;
import faithcoderlab.divflow.model.constants.CacheKey;
import faithcoderlab.divflow.repository.CompanyRepository;
import faithcoderlab.divflow.repository.DividendRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.debug("search company -> " + companyName);

        Company company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다: " + companyName));

        List<Dividend> dividends = dividendRepository.findAllByCompany(company);

        List<DividendDto> dividendDtos = dividends.stream()
                .map(DividendDto::fromEntity)
                .collect(Collectors.toList());

        return new ScrapedResult(
                CompanyDto.fromEntity(company),
                dividendDtos
        );
    }
}
