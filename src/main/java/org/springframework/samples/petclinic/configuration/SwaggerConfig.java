package org.springframework.samples.petclinic.configuration;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.val;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	private List<Parameter> globalParameterList() {
		val authTokenHeader = new ParameterBuilder().name("Authorization").modelRef(new ModelRef("string"))
				.parameterType("header")
				.description("JWT Token -> Bearer [jwt]\nTo access the data you have to be authenticated.").build();

		return Collections.singletonList(authTokenHeader);
	}

	@Bean
	Docket apiDocket() {
		return new Docket(DocumentationType.SWAGGER_2).forCodeGeneration(true)
				.globalOperationParameters(globalParameterList()).select()
				.apis(RequestHandlerSelectors.basePackage("org.springframework.samples.petclinic"))
				.paths(PathSelectors.any()).build().apiInfo(getApiInfo());
	}

	private ApiInfo getApiInfo() {
		return new ApiInfo("PetClinic API", "API for PetClinic application", "1.0", "http://localhost:3000",
				new Contact("Petclinic", "http://localhost:3000", "rafasana9@gmail.com"), "LICENSE", "LICENSE URL",
				Collections.emptyList());
	}
}
