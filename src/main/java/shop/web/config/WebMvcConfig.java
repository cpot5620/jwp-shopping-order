package shop.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import shop.domain.repository.MemberRepository;
import shop.web.auth.AuthInterceptor;
import shop.web.auth.AuthService;
import shop.web.auth.MemberArgumentResolver;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final AuthService authService;
    private final MemberRepository memberRepository;

    public WebMvcConfig(AuthService authService, MemberRepository memberRepository) {
        this.authService = authService;
        this.memberRepository = memberRepository;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(authService))
                .addPathPatterns("/cart-items/**", "/users/**", "/orders/**")
                .excludePathPatterns("/users/login", "/users/join");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new MemberArgumentResolver(memberRepository));
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://monumental-kleicha-ad648a.netlify.app/",
                        "http://localhost:9000", "http://localhost:8080",
                        "https://jourzura2.kro.kr")
                .allowedMethods("*");
    }
}
