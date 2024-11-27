package com.crm.crmgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;

@Configuration
public class GatewayConfiguration {

    @Bean
    public CustomTokenRelayGatewayFilterFactory customTokenRelayGatewayFilterFactory(
            ReactiveOAuth2AuthorizedClientService clientService) {
        return new CustomTokenRelayGatewayFilterFactory(clientService);
    }
}
