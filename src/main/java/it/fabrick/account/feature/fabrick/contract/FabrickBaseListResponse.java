package it.fabrick.account.feature.fabrick.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FabrickBaseListResponse<T> {
    private List<T> list;
}
