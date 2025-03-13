package faithcoderlab.divflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import faithcoderlab.divflow.model.Dividend;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DividendDto {
    private String date;
    private float dividend;

    public static DividendDto fromEntity(Dividend dividend) {
        return DividendDto.builder()
                .date(dividend.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .dividend(dividend.getDividend())
                .build();
    }
}
