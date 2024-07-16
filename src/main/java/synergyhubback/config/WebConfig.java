package synergyhubback.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:3000")
//                .allowedMethods("GET", "POST", "PUT", "DELETE")
//                .allowedHeaders("Authorization", "Content-Type", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers","Access-Token")
//                .exposedHeaders("Access-Token", "Refresh-Token")
//                .allowedOriginPatterns("/**")
//                .allowCredentials(true)
//                .maxAge(3600);
//    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {


        //각자 폴더의 이름에 맞게 DIR 수정해주세요

        // 상품 이미지 경로 추가
        registry.addResourceHandler("/post/**")
                .addResourceLocations("file:///C:/Synergy/original/productUpload/");




    }

}
