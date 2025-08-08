package trinity.play2learn.backend.configs.ArgumentSolvers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;

@Configuration
public class ArgumentSolversConfig implements WebMvcConfigurer {

    @Autowired
    private SessionUserArgumentResolver currentUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}
