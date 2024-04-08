package org.example.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private static final String BEARER = "Bearer";
    private static final String AUTH_URL = "http://identity-service/auth/validate?token=";
    private static final String TOKEN_IS_VALID = "Token is valid";

    private final RouteValidator validator;
    private final WebClient webClient;

    public AuthenticationFilter(RouteValidator validator, WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.validator = validator;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("Missing authorization header");
                }
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith(BEARER + " ")) {
                    authHeader = authHeader.substring(7);
                }
                return this.webClient
                        .get()
                        .uri(AUTH_URL + authHeader)
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(response -> {
                            if(!TOKEN_IS_VALID.equals(response))
                                return Mono.error(new RuntimeException("Token is not valid!"));
                            return chain.filter(exchange);
                        }).onErrorResume(throwable -> {
                            throw new RuntimeException("Unauthorized access to application");
                        });
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}
