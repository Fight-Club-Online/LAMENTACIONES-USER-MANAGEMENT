package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.controller;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.controller.UserController;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.RegisterUserUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.LoginUserUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.RegisterGuestUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request.RegisterUserRequest;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request.LoginUserRequest;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request.RegisterGuestRequest;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController controller;

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    @Mock
    private LoginUserUseCase loginUserUseCase;

    @Mock
    private RegisterGuestUseCase registerGuestUseCase;

    @Test
    void shouldRegisterUser() {

        RegisterUserRequest req = RegisterUserRequest.builder()
                .email("oscar@gmail.com")
                .username("oscar")
                .password("arsw")
                .build();

        AuthResponse expected = AuthResponse.builder()
                .userId("sanchez")
                .accessToken("acc")
                .refreshToken("ref")
                .build();

        when(registerUserUseCase.register(any())).thenReturn(expected);

        ResponseEntity<AuthResponse> resp = controller.registerUser(req);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertSame(expected, resp.getBody());
        verify(registerUserUseCase).register(argThat(cmd -> "oscar@gmail.com".equals(cmd.getEmail())));
    }

    @Test
    void shouldLoginUser() {
  
        LoginUserRequest req = LoginUserRequest.builder()
                .email("oscar@gmail.com")
                .password("arsw")
                .build();

        AuthResponse expected = AuthResponse.builder().accessToken("a").build();
        when(loginUserUseCase.login(any())).thenReturn(expected);

        ResponseEntity<AuthResponse> resp = controller.loginUser(req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(expected, resp.getBody());
        verify(loginUserUseCase).login(argThat(cmd -> "oscar@gmail.com".equals(cmd.getEmail())));
    }

    @Test
    void shouldRegisterGuest() {

        RegisterGuestRequest req = RegisterGuestRequest.builder()
                .username("guest")
                .build();

        AuthResponse expected = AuthResponse.builder().userId("g1").build();
        when(registerGuestUseCase.registerGuest(any())).thenReturn(expected);

        ResponseEntity<AuthResponse> resp = controller.registerGuest(req);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertSame(expected, resp.getBody());
        verify(registerGuestUseCase).registerGuest(any());
    }
}