package it.fabrick.account.feature.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.fabrick.account.exception.ThirdPartyException;
import it.fabrick.account.fixture.AccountFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static it.fabrick.account.exception.ThirdPartyException.ExceptionSource.FABRICK;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @Test
    void getAccountBalance_shouldWork() throws Exception {
        var expected = AccountFixtures.getValidGetBalanceResponse();
        when(accountService.getAccountBalance(12L)).thenReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/12/balance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void getAccountBalance_shouldReturnError_whenClientCallFails() throws Exception {
        when(accountService.getAccountBalance(12L)).thenThrow(new ThirdPartyException("message", FABRICK, "code", HttpStatusCode.valueOf(400)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/12/balance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.exceptionCode").value("code"));
    }

    @Test
    void createTransfer_shouldWork() throws Exception {
        var expected = AccountFixtures.getValidCreateTransferResponse();
        var request = AccountFixtures.getValidCreateTransferRequest();
        when(accountService.createTransfer(12L, request)).thenReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/12/money-transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void createTransfer_shouldReturnError_whenClientCallFails() throws Exception {
        var request = AccountFixtures.getValidCreateTransferRequest();
        when(accountService.createTransfer(12L, request)).thenThrow(new ThirdPartyException("message", FABRICK, "code", HttpStatusCode.valueOf(400)));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/12/money-transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.exceptionCode").value("code"));
    }
}
