package faithcoderlab.divflow.service;

import faithcoderlab.divflow.dto.CompanyDto;
import faithcoderlab.divflow.dto.ScrapedResult;
import faithcoderlab.divflow.exception.CustomException;
import faithcoderlab.divflow.exception.ErrorCode;
import faithcoderlab.divflow.model.Company;
import faithcoderlab.divflow.model.Dividend;
import faithcoderlab.divflow.repository.CompanyRepository;
import faithcoderlab.divflow.repository.DividendRepository;
import faithcoderlab.divflow.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
