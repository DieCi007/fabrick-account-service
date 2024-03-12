package it.fabrick.account.feature.fabrick.contract;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class FabrickBaseResponse<T> {
    private static final ObjectMapper mapper = new ObjectMapper();

    private ResponseStatus status;
    private List<FabrickErrorResponse> errors;
    private List<FabrickErrorResponse> error; // I noticed when http status returns 2xx, this field is called error (not errors)
    private T payload;

    @SneakyThrows
    public static FabrickBaseResponse<Void> fromResponseString(String body) {
        return mapper.readValue(body, new TypeReference<>() {
        });
    }

    public enum ResponseStatus {
        OK,
        KO
    }
}
