package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.TransferStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TransferResponseDto {

    private UUID operationId;
    private TransferStatus status;
}
