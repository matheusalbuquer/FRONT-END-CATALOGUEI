package group.triade.catalogo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // <-- AQUI ESTAVA O PROBLEMA
          .allowedOriginPatterns(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "http://127.0.0.2:*",
            "http://localhost:5500",
            "http://localhost:5173",
            "https://*.vercel.app",
            "https://*.netlify.app"
          )
          .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
          .allowedHeaders("*")
          .exposedHeaders("Authorization")
          .allowCredentials(false)
          .maxAge(3600);
      }
    };
  }
}
