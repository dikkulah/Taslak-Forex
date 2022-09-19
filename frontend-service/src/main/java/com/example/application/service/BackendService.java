package com.example.application.service;


import com.example.application.model.Account;
import com.example.application.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BackendService {

    WebClient client = WebClient.builder()
            .baseUrl("http://localhost:8080/")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public ResponseEntity<Token> login(String username, String password) {


        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("username", username);
        bodyMap.put("password", password);
        var responseToken = client.post()
                .uri("login")
                .body(BodyInserters.fromValue(bodyMap))
                .retrieve()
                .bodyToFlux(Token.class)
                .onErrorMap(e -> new RuntimeException(e.getCause()))
                .blockLast();


        return new ResponseEntity<>(responseToken, HttpStatus.OK);


    }

    public ResponseEntity<List<Account>> getAccounts(String token)  {

        var response = client.get()
                .uri("accounts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Account[].class)
                .onErrorMap(e -> new RuntimeException(e.getCause())).block();

        assert response != null;
        List<Account> accounts = List.of(response);
        return new ResponseEntity<>(accounts, HttpStatus.OK);


    }
}