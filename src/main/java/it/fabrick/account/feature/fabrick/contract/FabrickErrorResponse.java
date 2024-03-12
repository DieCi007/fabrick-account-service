package it.fabrick.account.feature.fabrick.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FabrickErrorResponse {
    private String code;
    private String description;
    private String params;
}
