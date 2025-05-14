package com.tander.apigateway.filter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.tander.apigateway.util.SecurityUtil;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RoutesFilter validator;

    @Autowired
    private SecurityUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("no authorization header");
                }
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
                    jwtUtil.validateToken(authHeader);
                } catch (Exception e) {
                    throw new RuntimeException("unauthorized access to application");
                }

                String userId = jwtUtil.getUserId(authHeader);
                System.out.println("UserID: " + userId);

                var request = exchange.getRequest().mutate()
                    .header("userId", userId)
                    .build();

                var mutateExtange = exchange.mutate()
                    .request(request)
                    .build();

                return chain.filter(mutateExtange);
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {
    }
    
}
