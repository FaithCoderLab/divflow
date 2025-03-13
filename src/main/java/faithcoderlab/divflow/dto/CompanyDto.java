package faithcoderlab.divflow.dto;

import faithcoderlab.divflow.model.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDto {
    private String ticker;
    private String name;

    public static CompanyDto fromEntity(Company company) {
        return CompanyDto.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build();
    }
}
