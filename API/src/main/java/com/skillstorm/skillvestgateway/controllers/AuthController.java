package com.skillstorm.skillvestgateway.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import com.skillstorm.skillvestgateway.models.UserModel;

import java.net.URI;

@Controller
@RequestMapping("/auth")
@CrossOrigin(origins = "https://team-cuttlefish.aws-tfbd.com", allowCredentials = "true") //Domain was changed
//@CrossOrigin(origins = "${app.allowedOrigins}", allowCredentials = "true")
public class AuthController {

    @Autowired
    private ReactiveOAuth2AuthorizedClientService clientService;

    @Value("${app.redirectUrl}")
    String redirectUrl;

    @Value("${app.logoutUrl}")
    String logoutUrl;

    @GetMapping("/login")
    public RedirectView redirectView() {
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/logout")
    @ResponseBody
    public ResponseEntity<Void> logout(ServerWebExchange exchange) {
        // Invalidate the Spring Security session
        SecurityContextHolder.getContext().setAuthentication(null);

        // Invalidate the WebFlux session
        exchange.getSession().doOnNext(WebSession::invalidate).subscribe();

        // TODO need to move to env variable
        // Redirect to Amazon Cognito's logout URL
        exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
        exchange.getResponse().getHeaders().setLocation(URI.create(logoutUrl));

        return ResponseEntity.status(HttpStatus.SEE_OTHER).build();
    }

    @GetMapping("/getInfo")
    @ResponseBody
    public ResponseEntity<UserModel> getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        String firstName = principal.getAttribute("given_name");
        String lastName = principal.getAttribute("family_name");
        // return new TestModel(firstName, email);

        return ResponseEntity.ok().body(new UserModel(firstName, lastName, email));
    }
}
