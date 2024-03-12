package it.fabrick.account.feature.fabrick.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FabrickCreditor {
    private String name;
    private Account account;
    private Address address;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Account {
        private String accountCode;
        private String bicCode;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Address {
        private String address;
        private String city;
        private String countryCode;
    }
}
