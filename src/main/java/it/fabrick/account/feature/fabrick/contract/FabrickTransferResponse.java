package it.fabrick.account.feature.fabrick.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FabrickTransferResponse {
    private TransferStatus status;

    public enum TransferStatus {
        EXECUTED,
        BOOKED,
        WORK_IN_PROGRESS,
        CANCELLED,
        REJECTED
    }
}
