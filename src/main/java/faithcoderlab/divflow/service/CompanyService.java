package faithcoderlab.divflow.service;

import faithcoderlab.divflow.dto.CompanyDto;
import faithcoderlab.divflow.dto.ScrapedResult;
import faithcoderlab.divflow.exception.CustomException;
import faithcoderlab.divflow.exception.ErrorCode;
import faithcoderlab.divflow.model.Company;
import faithcoderlab.divflow.model.Dividend;
import faithcoderlab.divflow.model.constants.CacheKey;
import faithcoderlab.divflow.repository.CompanyRepository;
import faithcoderlab.divflow.repository.DividendRepository;
import faithcoderlab.divflow.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyService {

    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final CacheManager redisCacheManager;

    @Transactional
    public CompanyDto save(String ticker) {
        boolean exists = companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_TICKER);
        }

        return storeCompanyAndDividend(ticker);
    }

    private CompanyDto storeCompanyAndDividend(String ticker) {
        CompanyDto companyDto = yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (companyDto == null) {
            throw new CustomException(ErrorCode.TICKER_NOT_FOUND);
        }

        Company company = companyRepository.save(
                Company.builder()
                        .ticker(companyDto.getTicker())
                        .name(companyDto.getName())
                        .build()
        );

        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(companyDto);

        List<Dividend> dividends = scrapedResult.getDividends().stream()
                .map(e -> Dividend.builder()
                        .company(company)
                        .date(LocalDateTime.parse(e.getDate()))
                        .dividend(e.getDividend())
                        .build())
                .collect(Collectors.toList());

        dividendRepository.saveAll(dividends);

        return companyDto;
    }

    public List<String> getCompanyNamesByPrefix(String prefix) {
        List<Company> companies = companyRepository.findByNameStartingWithIgnoreCase(prefix);
        return companies.stream()
                .map(Company::getName)
                .limit(10)
                .collect(Collectors.toList());
    }

    public Page<CompanyDto> getAllCompanies(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Company> companyPage = companyRepository.findAll(pageRequest);
        return companyPage.map(CompanyDto::fromEntity);
    }

    public void deleteCompany(String ticker) {
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKER_NOT_FOUND));

        clearCompanyCache(company.getName());

        dividendRepository.deleteAllByCompany(company);
        companyRepository.delete(company);

        log.info("회사 정보 삭제 완료: {}", company.getName());
    }

    private void clearCompanyCache(String companyName) {
        redisCacheManager.getCache(CacheKey.KEY_FINANCE)
                .evict(companyName);

        log.info("캐시 삭제 완료: {}", companyName);
    }
}
