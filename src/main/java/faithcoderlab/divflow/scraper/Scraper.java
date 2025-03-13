package faithcoderlab.divflow.scraper;

import faithcoderlab.divflow.dto.CompanyDto;
import faithcoderlab.divflow.dto.ScrapedResult;

public interface Scraper {
    ScrapedResult scrap(CompanyDto companyDto);

    CompanyDto scrapCompanyByTicker(String ticker);
}
