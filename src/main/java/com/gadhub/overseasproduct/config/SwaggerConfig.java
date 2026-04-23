package com.gadhub.overseasproduct.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("海外电商产品管理系统 API")
                        .version("1.0")
                        .description("提供用户、商品、购物车、订单、支付等功能接口")
                        .contact(new Contact()
                                .name("GadHua")
                                .email("H2845244813@outlook.com")));
    }
}
