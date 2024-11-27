package com.crm.crmgateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomTokenRelayGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final ReactiveOAuth2AuthorizedClientService clientService;

    public CustomTokenRelayGatewayFilterFactory(ReactiveOAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> exchange.getPrincipal()
                .filter(principal -> principal instanceof Authentication)
                .cast(Authentication.class)
                .flatMap(authentication -> handleAuthentication(exchange, chain, authentication))
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<Void> handleAuthentication(ServerWebExchange exchange, GatewayFilterChain chain, Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            // Handle JWT tokens
            return handleJwtAuthentication(exchange, chain, jwtAuthenticationToken);
        } else if (authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken) {
            // Handle OAuth2 tokens
            return handleOAuth2Authentication(exchange, chain, oauth2AuthenticationToken);
        }
        return Mono.empty();
    }

    private Mono<Void> handleJwtAuthentication(ServerWebExchange exchange, GatewayFilterChain chain, JwtAuthenticationToken jwtAuthenticationToken) {
        String tokenValue = jwtAuthenticationToken.getToken().getTokenValue();
        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.putAll(super.getHeaders());
                headers.setBearerAuth(tokenValue);
                headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
                return headers;
            }
        };
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
        return chain.filter(mutatedExchange);
    }

    private Mono<Void> handleOAuth2Authentication(ServerWebExchange exchange, GatewayFilterChain chain, OAuth2AuthenticationToken oauth2AuthenticationToken) {
        return clientService.loadAuthorizedClient(
                        oauth2AuthenticationToken.getAuthorizedClientRegistrationId(),
                        oauth2AuthenticationToken.getName())
                .flatMap(authorizedClient -> {
                    OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                    if (accessToken != null) {
                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                            @Override
                            public HttpHeaders getHeaders() {
                                HttpHeaders headers = new HttpHeaders();
                                headers.putAll(super.getHeaders());
                                headers.setBearerAuth(accessToken.getTokenValue());
                                headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
                                return headers;
                            }
                        };
                        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                        return chain.filter(mutatedExchange);
                    }
                    return chain.filter(exchange);
                });
    }
}
