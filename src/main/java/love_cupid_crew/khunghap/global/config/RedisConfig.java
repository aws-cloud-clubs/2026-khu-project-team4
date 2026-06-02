package love_cupid_crew.khunghap.global.config;

import jakarta.annotation.PostConstruct;
import love_cupid_crew.khunghap.chat.ChoiceExpiryListener;
import love_cupid_crew.khunghap.chat.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void enableKeyspaceNotifications() {
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .setConfig("notify-keyspace-events", "Ex");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisSubscriber redisSubscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisSubscriber, new PatternTopic("chat:room:*"));
        return container;
    }

    @Bean
    public RedisMessageListenerContainer keyspaceListenerContainer(
            RedisConnectionFactory connectionFactory,
            ChoiceExpiryListener choiceExpiryListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(choiceExpiryListener, new PatternTopic("__keyevent@*__:expired"));
        return container;
    }
}
