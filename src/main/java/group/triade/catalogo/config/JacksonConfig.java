package group.triade.catalogo.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    Jackson2ObjectMapperBuilderCustomizer jsonCustomizer (){
        return b -> b.modulesToInstall(new JavaTimeModule());
    }

}
