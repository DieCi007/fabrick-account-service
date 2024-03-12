package it.fabrick.account.feature.fabrick.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.fabrick.account.feature.account.contract.CreateTransferRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FabrickTransferRequest {
    // Mapping only a subset of fields needed for this application
    private FabrickCreditor creditor;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate executionDate;
    private String description;
    private Double amount;
    private String currency;

    public static FabrickTransferRequest from(CreateTransferRequest request) {
        return FabrickTransferRequest.builder()
                .creditor(FabrickCreditor.builder()
                        .name(request.getCreditor().getName())
                        .account(FabrickCreditor.Account.builder()
                                .accountCode(request.getCreditor().getAccount().getAccountCode())
                                .build())
                        .build())
                .description(request.getDescription())
                .currency(request.getCurrency())
                .amount(request.getAmount())
                .executionDate(request.getExecutionDate())
                .build();
    }
}
