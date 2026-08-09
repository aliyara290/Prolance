package com.dxc.notificationservice.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> tokenHeaders = accessor.getNativeHeader("Authorization");
            
            if (tokenHeaders != null && !tokenHeaders.isEmpty()) {
                String bearerToken = tokenHeaders.get(0);
                String token = bearerToken.replace("Bearer ", "");
                
                try {
                    Jwt jwt = jwtDecoder.decode(token);
                    String userId = jwt.getClaimAsString("sub");
                    
                    if (userId != null) {
                        Principal userPrincipal = () -> userId;
                        accessor.setUser(userPrincipal);
                        log.debug("WebSocket authenticated for user: {}", userId);
                    }
                } catch (Exception e) {
                    log.error("Failed to authenticate STOMP connection", e);
                    throw new IllegalArgumentException("Invalid token", e);
                }
            } else {
                log.warn("No Authorization header provided for WebSocket connection");
            }
        }

        return message;
    }
}
