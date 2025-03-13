package faithcoderlab.divflow.scraper;

import faithcoderlab.divflow.dto.CompanyDto;
import faithcoderlab.divflow.dto.ScrapedResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperTest implements CommandLineRunner {

    private final YahooFinanceScraper yahooFinanceScraper;

    @Override
    public void run(String... args) {
        try {
            String ticker = "AAPL";

            log.info("스크래핑 테스트 시작: {}", ticker);

            try {
                CompanyDto company = yahooFinanceScraper.scrapCompanyByTicker(ticker);
                if (company != null) {
                    log.info("회사 정보 스크래핑 성공: {}", company);

                    try {
                        ScrapedResult result = yahooFinanceScraper.scrap(company);
                        if (result != null && result.getDividends() != null && !result.getDividends().isEmpty()) {
                            log.info("배당금 정보 스크래핑 성공. 배당금 데이터 수: {}", result.getDividends().size());
                            result.getDividends().stream()
                                    .limit(5)
                                    .forEach(dividend -> log.info("배당금 정보: {} - {}", dividend.getDate(), dividend.getDividend()));
                        } else {
                            log.warn("배당금 정보 스크래핑 결과가 비어있습니다");
                        }
                    } catch (Exception e) {
                        log.error("배당금 정보 스크래핑 중 에러 발생: {}", e.getMessage(), e);
                    }
                } else {
                    log.warn("회사 정보 스크래핑 결과가 null입니다: {}", ticker);
                }
            } catch (Exception e) {
                log.error("회사 정보 스크래핑 중 에러 발생: {}", e.getMessage(), e);
            }

            log.info("스크래핑 테스트 종료");
        } catch (Exception e) {
            log.error("스크래핑 테스트 실행 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
        }
    }
}