package com.intuit.order.config;

import com.intuit.order.entity.ProductOrder;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import com.fasterxml.classmate.TypeResolver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {

        TypeResolver typeResolver = new TypeResolver();

        return new Docket(DocumentationType.SWAGGER_2)
                .genericModelSubstitutes(Flux.class, Publisher.class)
                .alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(Mono.class,
                        typeResolver.resolve(ResponseEntity.class, ProductOrder.class)), ProductOrder.class))
                .alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(Mono.class,
                        ProductOrder.class), ProductOrder.class))
                .apiInfo(new ApiInfoBuilder()
                        .title("Intuit Product Service")
                        .description("This Microservice holds the following responsibilty for <b>Product Microservice</b> : <br /> "
                                + "<ol>\n" +
                                "  		<li>API to retrieve the list of all available Products.</li>\n" +
                                "  		<li>API to retrieve a Product details by Product Id.</li>\n" +
                                "  		<li>API to responsible to create a new Product.</li>\n" +
                                "  		<li>API to responsible to update the details of an existing Product.</li>\n" +
                                " 		<li>API to responsible to delete the details of an existing Product.</li>\n" +
                                "  </ol>")
                        .version("1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
