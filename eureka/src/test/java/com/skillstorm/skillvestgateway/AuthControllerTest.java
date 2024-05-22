package com.skillstorm.skillvestgateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import com.skillstorm.skillvestgateway.controllers.AuthController;
import com.skillstorm.skillvestgateway.models.UserModel;

import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthControllerTest {

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private WebSession webSession;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private ServerHttpResponse serverHttpResponse;

    @Captor
    ArgumentCaptor<HttpHeaders> headersCaptor;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private OAuth2User oauth2User;

    @Autowired
    private AuthController authController;

    @Value("${app.redirectUrl}")
    String redirectUrl;

    @Value("${app.logoutUrl}")
    String logoutUrl;

    //JUNIT-MODULE-000
    @Test
    public void testRedirectView() {
        RedirectView result = authController.redirectView();
        assertEquals(redirectUrl, result.getUrl());
    }

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.setContext(securityContext); // sets the SecurityContext on current thread, to simulate a
        // user being authenticated.
        when(exchange.getResponse()).thenReturn(serverHttpResponse); // sets mock response , returns the
        // ServerHttpResponse object
        when(serverHttpResponse.getHeaders()).thenReturn(httpHeaders); // sets mock headers, returns the HttpHeaders
        // object
    }

    //JUNIT-MODULE-001
    @Test
    public void testLogout() {
        when(exchange.getSession()).thenReturn(Mono.just(webSession));
        when(webSession.invalidate()).thenReturn(Mono.empty());

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class); //object that can capture arguments of type URI
        // Act
        ResponseEntity<Void> response = authController.logout(exchange); //we call the logout method from AuthController

        //make sure setLocation was called on the HttpHeaders object and captures the URI
        verify(httpHeaders).setLocation(uriCaptor.capture());
        URI capturedUri = uriCaptor.getValue();

        // Assert http status code
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        //captured URI is the same as the expected URI.
        URI expectedUri = URI.create(logoutUrl);
        assertEquals(expectedUri, capturedUri);
    }

    @BeforeEach
    public void setUpUserInfo() {
        // Set mock behaviors for OAuth2User
        when(oauth2User.getAttribute("email")).thenReturn("test@test.com");
        when(oauth2User.getAttribute("given_name")).thenReturn("John");
        when(oauth2User.getAttribute("family_name")).thenReturn("Doe");
    }

    //JUNIT-MODULE-002
    @Test
    public void testGetUserInfo() {
        // Act
        ResponseEntity<UserModel> response = authController.getUserInfo(oauth2User);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John", response.getBody().getGiven_name());
        assertEquals("test@test.com", response.getBody().getEmail());
    }

}