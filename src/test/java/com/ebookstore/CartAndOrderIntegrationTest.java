package com.ebookstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CartAndOrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCartWorkflowAndOrderPlacementWith48HCancellation() throws Exception {
        // 1. Get initial cart
        mockMvc.perform(get("/api/cart").header("X-User-Id", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1));

        // 2. Add Book #2 to cart
        String addItemJson = """
            {
                "bookId": 2,
                "quantity": 1
            }
            """;
        mockMvc.perform(post("/api/cart/items")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addItemJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItemCount").isNumber());

        // 3. Place order with gift points redemption
        String orderJson = """
            {
                "addressId": 1,
                "pointsToRedeem": 100
            }
            """;
        String orderResponse = mockMvc.perform(post("/api/orders")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.canCancel").value(true))
            .andExpect(jsonPath("$.pointsRedeemed").value(100))
            .andExpect(jsonPath("$.discountAmount").value(1.0))
            .andReturn().getResponse().getContentAsString();

        // 4. Test 48-Hour Order Cancellation on recent Order #2 (created in data.sql 2 hours ago)
        mockMvc.perform(post("/api/orders/2/cancel")
                .header("X-User-Id", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));

        // 5. Test "Buy It Again" on Order #1
        mockMvc.perform(post("/api/orders/1/buy-again")
                .header("X-User-Id", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItemCount").isNumber());
    }
}
