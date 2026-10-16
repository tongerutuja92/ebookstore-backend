package com.ebookstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentAndRecommendationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetRelatedBooks() throws Exception {
        mockMvc.perform(get("/api/books/1/related").param("limit", "4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testGetOrderHistoryRecommendations() throws Exception {
        mockMvc.perform(get("/api/recommendations/order-history")
                .header("X-User-Id", "1")
                .param("limit", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testPaymentWorkflow() throws Exception {
        // Initiate Payment for Order #2
        String payReq = """
            {
                "orderId": 2,
                "paymentMethod": "CREDIT_CARD",
                "cardHolderName": "John Doe",
                "cardNumber": "************4242"
            }
            """;

        String payResponse = mockMvc.perform(post("/api/payments/initiate")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payReq))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(2))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andReturn().getResponse().getContentAsString();

        // Extract ID or confirm payment on payment ID 2
        String confirmReq = """
            {
                "transactionReference": "TXN-CONFIRM-9999",
                "status": "SUCCESS"
            }
            """;

        mockMvc.perform(post("/api/payments/2/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(confirmReq))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
