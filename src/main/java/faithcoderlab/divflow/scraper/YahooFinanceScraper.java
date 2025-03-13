package faithcoderlab.divflow.scraper;

import faithcoderlab.divflow.dto.CompanyDto;
import faithcoderlab.divflow.dto.DividendDto;
import faithcoderlab.divflow.dto.ScrapedResult;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class YahooFinanceScraper implements Scraper {
    private static final String YAHOO_FINANCE_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&frequency=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400;

    @Override
    public ScrapedResult scrap(CompanyDto company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000;

            String url = String.format(YAHOO_FINANCE_URL, company.getTicker(), START_TIME, now);
            log.info("Scraping URL: {}", url);

            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            log.info("Document fetched successfully");

            Elements tables = document.select("table");
            log.info("Found {} tables", tables.size());

            if (tables.isEmpty()) {
                log.error("No tables found");
                return scrapResult;
            }

            Element table = tables.first();

            Element tbody = table.select("tbody").first();
            if (tbody == null) {
                log.error("No tbody found in table");
                return scrapResult;
            }

            Elements rows = tbody.select("tr");
            log.info("Found {} rows in the table", rows.size());

            List<DividendDto> dividends = new ArrayList<>();

            for (Element row : rows) {
                String rowText = row.text();
                log.debug("Row text: {}", rowText);

                if (rowText.contains("Dividend")) {
                    log.info("Found dividend row: {}", rowText);

                    Elements cells = row.select("td");
                    if (cells.size() < 2) {
                        log.warn("Not enough cells in dividend row");
                        continue;
                    }

                    String dateText = cells.get(0).text();
                    log.debug("Date text: {}", dateText);

                    Element dividendElement = null;
                    for (Element cell : cells) {
                        if (cell.text().contains("Dividend")) {
                            dividendElement = cell;
                            break;
                        }
                    }

                    if (dividendElement == null) {
                        log.warn("Could not find dividend cell in row");
                        continue;
                    }

                    String dividendText = dividendElement.text();
                    log.debug("Dividend text: {}", dividendText);

                    String[] dateParts = dateText.split(" ");
                    if (dateParts.length < 3) {
                        log.warn("Invalid date format: {}", dateText);
                        continue;
                    }

                    int month = Month.strToNumber(dateParts[0]);
                    int day = Integer.parseInt(dateParts[1].replace(",", ""));
                    int year = Integer.parseInt(dateParts[2]);

                    if (month < 0) {
                        log.warn("Invalid month: {}", dateParts[0]);
                        continue;
                    }

                    Element spanElement = dividendElement.select("span").first();
                    if (spanElement == null) {
                        log.warn("No span element found for dividend value");
                        continue;
                    }

                    String dividendValueText = spanElement.text();
                    log.debug("Dividend value text: {}", dividendValueText);

                    try {
                        float dividendValue = Float.parseFloat(dividendValueText);

                        DividendDto dividendDto = DividendDto.builder()
                                .date(LocalDateTime.of(year, month, day, 0, 0))
                                .dividend(dividendValue)
                                .build();

                        log.info("Added dividend: {} - {}", dividendDto.getDate(), dividendDto.getDividend());
                        dividends.add(dividendDto);
                    } catch (NumberFormatException e) {
                        log.warn("Failed to parse dividend value: {}", dividendValueText);
                    }
                }
            }

            log.info("Total dividend entries found: {}", dividends.size());
            scrapResult.setDividends(dividends);
        } catch (IOException e) {
            log.error("Scraping error: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during scraping: {}", e.getMessage(), e);
        }

        return scrapResult;
    }

    @Override
    public CompanyDto scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            log.info("Scraping company info URL: {}", url);
            Document document = Jsoup.connect(url).get();

            String pageTitle = document.title();
            log.info("Page title: {}", pageTitle);

            Elements h1Elements = document.select("h1");
            log.info("Found {} h1 elements", h1Elements.size());

            for (int i = 0; i < h1Elements.size(); i++) {
                Element h1 = h1Elements.get(i);
                log.info("H1 #{}: class='{}', text='{}'", i+1, h1.className(), h1.text());
            }

            Element titleEle = document.select("h1.yf-xxbei9").first();
            if (titleEle == null) {
                log.error("No h1 with class y-fxxbei9 found, trying generic h1");
                titleEle = document.select("h1").first();

                if (titleEle == null) {
                    log.error("No h1 element found at all");
                    return null;
                }
            }

            String title = titleEle.text();
            log.info("Found title: {}", title);

            int titleEndIndex = title.indexOf("(");
            if (titleEndIndex <= 0) {
                log.warn("Invalid title format, using full title as company name");
                return CompanyDto.builder()
                        .ticker(ticker)
                        .name(title)
                        .build();
            }

            String companyName = title.substring(0, titleEndIndex).trim();
            log.info("Extracted company name: {}", companyName);

            return CompanyDto.builder()
                    .ticker(ticker)
                    .name(companyName)
                    .build();
        } catch (IOException e) {
            log.error("Scraping error: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during company scraping: {}", e.getMessage(), e);
        }

        return null;
    }

    private enum Month {
        JAN("Jan", 1),
        FEB("Feb", 2),
        MAR("Mar", 3),
        APR("Apr", 4),
        MAY("May", 5),
        JUN("Jun", 6),
        JUL("Jul", 7),
        AUG("Aug", 8),
        SEP("Sep", 9),
        OCT("Oct", 10),
        NOV("Nov", 11),
        DEC("Dec", 12);

        private String s;
        private int number;

        Month(String s, int number) {
            this.s = s;
            this.number = number;
        }

        public static int strToNumber(String s) {
            for (Month m : Month.values()) {
                if (m.s.equals(s)) {
                    return m.number;
                }
            }

            return -1;
        }
    }
}
