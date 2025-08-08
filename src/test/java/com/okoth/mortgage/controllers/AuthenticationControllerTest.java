package com.okoth.mortgage.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okoth.mortgage.models.dto.AuthRequest;
import com.okoth.mortgage.models.dto.AuthResponse;
import com.okoth.mortgage.models.dto.UserDTO;
import com.okoth.mortgage.services.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

  @Autowired private MockMvc mockMvc;

  @InjectMocks private AuthenticationService authenticationService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldAuthenticateWithValidCredentials() throws Exception {
    AuthRequest authRequest = new AuthRequest("username123", "password123");
    AuthResponse authResponse = new AuthResponse();

    Mockito.when(authenticationService.authenticateUser(any(AuthRequest.class)))
        .thenReturn(authResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

    Mockito.verify(authenticationService, Mockito.times(1))
        .authenticateUser(any(AuthRequest.class));
  }

  @Test
  void shouldReturnBadRequestWhenInvalidPayload() throws Exception {
    AuthRequest invalidAuthRequest = new AuthRequest("", "");

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAuthRequest)))
        .andExpect(status().isBadRequest());

    Mockito.verify(authenticationService, Mockito.never()).authenticateUser(any(AuthRequest.class));
  }

  @Test
  void shouldSignUpSuccessfully() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername("newuser");
    userDTO.setPassword("password");

    Mockito.when(authenticationService.subscribe(any(UserDTO.class)))
        .thenReturn("Successfully signed up");

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/auth/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Successfully signed up"));
  }
}
