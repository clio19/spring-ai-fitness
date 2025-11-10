package com.fitness.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Component
public class TargetLoggingGlobalFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(TargetLoggingGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        Object urls = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        if (urls != null) {
            log.debug("[TargetLogging] Resolved gateway target URL(s): {}", urls);
        } else {
            log.debug("[TargetLogging] No resolved gateway target URL available yet.");
        }

        return chain.filter(exchange).doOnSuccess(aVoid -> {
            // after downstream handled the request
            Object responseStatus = exchange.getResponse().getStatusCode();
            log.debug("[TargetLogging] After chain response status: {}", responseStatus);
        }).doOnError(ex -> {
            log.warn("[TargetLogging] Downstream handling produced error: {}", ex.toString());
        });
    }

    @Override
    public int getOrder() {
        // Run fairly late in the filter chain but before the write response; LOW_PRECEDENCE-ish
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}

