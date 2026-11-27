package com.bookzilla.auth.infrastructure.ratelimit;

import com.bookzilla.auth.infrastructure.security.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RedisTokenBucketRateLimiter implements RateLimiter {

    private static final String KEY_PREFIX = "rate_limit:user:";

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<Long> tokenBucketScript;


    public RedisTokenBucketRateLimiter(StringRedisTemplate stringRedisTemplate,
                                       @Value("${bookzilla.rate-limit.script-location}") String scriptLocation) {

        this.stringRedisTemplate = stringRedisTemplate;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource(scriptLocation));
        script.setResultType(Long.class);

        this.tokenBucketScript = script;
    }

    @Override
    public boolean isAllowed(UUID userId) {

        String key = KEY_PREFIX + userId;

        Long result = stringRedisTemplate.execute(
                tokenBucketScript,
                List.of(key)
        );

        if (result == null) {
            throw new IllegalStateException(
                    "Token bucket script returned null"
            );
        }

        return result == 1L;
    }
}
