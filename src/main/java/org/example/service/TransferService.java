package org.example.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.TransferStatus;
import org.example.dto.TransferRequestDto;
import org.example.dto.TransferResponseDto;
import org.example.entities.Card;
import org.example.entities.Transfer;
import org.example.exception.InvalidDataException;
import org.example.repository.CardRepository;
import org.example.repository.TransferRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TransferService {
    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;
    private final BigDecimal FEE_RATE = new BigDecimal("0.01");

    @Transactional
    public TransferResponseDto transfer(TransferRequestDto requestDto) {

        Card cardFrom = cardRepository.findByCardNumber(requestDto.getCardFromNumber())
                .orElseThrow(() -> new InvalidDataException("Card from not found"));
        Card cardTo = cardRepository.findByCardNumber(requestDto.getCardToNumber())
                .orElseThrow(() -> new InvalidDataException("Card to not found"));

        if (!Boolean.TRUE.equals(cardFrom.getIsActive()) || !Boolean.TRUE.equals(cardTo.getIsActive())) {
            throw new InvalidDataException("One of the cards is inactive");
        }

        if (!cardFrom.getCvv().equals(requestDto.getCardFromCVV())) {
            throw new InvalidDataException("Invalid CVV");
        }

        BigDecimal amount = requestDto.getAmount().getValue();
        BigDecimal fee = calculateFee(amount);
        BigDecimal total = amount.add(fee);

        if (total.compareTo(cardFrom.getBalance()) > 0) {
            TransferLogger.getInstance().logTransfer(
                    cardFrom.getCardNumber(),
                    cardTo.getCardNumber(),
                    amount.doubleValue(),
                    fee.doubleValue(),
                    "FAILED: Insufficient funds"
            );
            throw new InvalidDataException("Insufficient funds for the transfer including fee");
        }

        cardFrom.setBalance(cardFrom.getBalance().subtract(total));
        cardTo.setBalance(cardTo.getBalance().add(amount));

        cardRepository.save(cardFrom);
        cardRepository.save(cardTo);

        TransferLogger.getInstance().logTransfer(
                cardFrom.getCardNumber(),
                cardTo.getCardNumber(),
                amount.doubleValue(),
                fee.doubleValue(),
                "SUCCESS"
        );

        Transfer transfer = new Transfer();
        transfer.setFromCard(cardFrom);
        transfer.setToCard(cardTo);
        transfer.setAmount(amount);
        transfer.setFee(fee);
        transfer.setCurrency(requestDto.getAmount().getCurrency());
        transfer.setStatus(TransferStatus.SUCCESS);
        transfer.setCreatedAt(LocalDateTime.now());

        transferRepository.save(transfer);
        return new TransferResponseDto(transfer.getId(), transfer.getStatus());
    }

    private BigDecimal calculateFee(BigDecimal amount) {
        return amount.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
