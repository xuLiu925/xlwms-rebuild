package com.xlwms.rebuild.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Long 以字符串输出，避免前端 JS 丢失 19 位雪花 ID 的精度。 */
@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer longAsString() {
        return builder -> {
            SimpleModule m = new SimpleModule();
            m.addSerializer(Long.class, ToStringSerializer.instance);
            m.addSerializer(Long.TYPE, ToStringSerializer.instance);
            builder.modulesToInstall(m);
        };
    }
}
