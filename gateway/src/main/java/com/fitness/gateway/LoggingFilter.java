package com.fitness.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        logger.debug("Request: {} {}",
            exchange.getRequest().getMethod(),
            exchange.getRequest().getURI()
        );

        // log the downstream URL resolved by the gateway (if present)
        Object gatewayUrls = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        if (gatewayUrls != null) {
            logger.debug("Gateway resolved target URL(s): {}", gatewayUrls);
        }

        exchange.getResponse()
            .beforeCommit(() -> {
                logger.debug("Response status: {}",
                    exchange.getResponse().getStatusCode()
                );
                return Mono.empty();
            });

        return chain.filter(exchange);
    }
}
