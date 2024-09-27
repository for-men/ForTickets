package com.fortickets.jpa;

import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String username = request.getHeader("X-User-Name");

        return Optional.of(Objects.requireNonNullElse("username@test.com", "system"));
    }
}