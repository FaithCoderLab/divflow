package faithcoderlab.divflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScrapedResult {
    private CompanyDto company;

    @Builder.Default
    private List<DividendDto> dividends = new ArrayList<>();
}
