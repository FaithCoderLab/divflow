package faithcoderlab.divflow.dto;

import faithcoderlab.divflow.model.Dividend;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DividendDto {
    private LocalDateTime date;
    private float dividend;

    public static DividendDto fromEntity(Dividend dividend) {
        return DividendDto.builder()
                .date(dividend.getDate())
                .dividend(dividend.getDividend())
                .build();
    }
}
