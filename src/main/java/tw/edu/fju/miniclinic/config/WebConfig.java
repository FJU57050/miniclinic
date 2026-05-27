package tw.edu.fju.miniclinic.config;

import tw.edu.fju.miniclinic.interceptor.LoginRequiredInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginRequiredInterceptor loginInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(
                        "/dashboard",
                        "/dashboard/**",
                        "/password",
                        "/password/**",
                        "/api/auth/me",
                        "/api/appointments/*/status"
                )
                .excludePathPatterns("/login", "/logout");
    }
}
