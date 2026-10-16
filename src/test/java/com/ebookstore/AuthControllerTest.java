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
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRegisterNewUser() throws Exception {
        String registerJson = """
            {
                "email": "newuser@example.com",
                "password": "Password123!",
                "fullName": "Alice Wonderland",
                "phoneNumber": "+1-555-9988"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").isNumber())
            .andExpect(jsonPath("$.email").value("newuser@example.com"))
            .andExpect(jsonPath("$.giftPoints").value(100))
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void testRegisterDuplicateEmailFails() throws Exception {
        String duplicateJson = """
            {
                "email": "john.doe@example.com",
                "password": "Password123!",
                "fullName": "John Duplicate"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void testGetUserProfile() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                .header("X-User-Id", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"))
            .andExpect(jsonPath("$.giftPoints").isNumber());
    }
}
