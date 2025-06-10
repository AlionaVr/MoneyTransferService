package org.example.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AmountDto {
    private BigDecimal value;
    private String currency;
}
