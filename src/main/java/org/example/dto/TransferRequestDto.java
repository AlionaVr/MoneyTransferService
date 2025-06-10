package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TransferRequestDto {
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    @NotBlank(message = "Card number is required")
    private String cardFromNumber;
    private String cardFromValidTill;
    @Size(min = 3, max = 3)
    private String cardFromCVV;
    @Size(min = 16, max = 16)
    @NotBlank
    private String cardToNumber;
    private AmountDto amount;
}