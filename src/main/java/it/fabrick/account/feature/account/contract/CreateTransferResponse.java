package it.fabrick.account.feature.account.contract;

import it.fabrick.account.feature.fabrick.contract.FabrickTransferResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateTransferResponse {
    private FabrickTransferResponse.TransferStatus status;

    public static CreateTransferResponse from(FabrickTransferResponse fabrickResponse) {
        return CreateTransferResponse.builder()
                .status(fabrickResponse.getStatus())
                .build();
    }
}
