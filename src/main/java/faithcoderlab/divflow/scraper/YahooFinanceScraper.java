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
    private static final String YAHOO_FINANCE_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400;

    @Override
    public ScrapedResult scrap(CompanyDto company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000;

            String url = String.format(YAHOO_FINANCE_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0);

            Element tbody = tableEle.children().get(1);

            List<DividendDto> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + month);
                }

                dividends.add(DividendDto.builder()
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(Float.parseFloat(dividend))
                        .build());
            }
            scrapResult.setDividends(dividends);
        } catch (IOException e) {
            log.error("scraping error", e);
        }

        return scrapResult;
    }

    @Override
    public CompanyDto scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text();

            int titleEndIndex = title.indexOf("(");
            String companyName = title.substring(0, titleEndIndex).trim();

            return CompanyDto.builder()
                    .ticker(ticker)
                    .name(companyName)
                    .build();
        } catch (IOException e) {
            log.error("scraping error", e);
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
