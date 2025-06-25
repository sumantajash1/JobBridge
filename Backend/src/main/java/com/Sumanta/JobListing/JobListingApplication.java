package com.Sumanta.JobListing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class JobListingApplication {

//	@Bean
//	public Docket api() {
//		return new Docket(DocumentationType.SWAGGER_2).select()
//				.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)).paths(PathSelectors.any())
//				.build().apiInfo(apiInfo()).useDefaultResponseMessages(false);
//	}
//
//	@Bean
//	public ApiInfo apiInfo() {
//		final ApiInfoBuilder builder = new ApiInfoBuilder();
//		return builder.build();
//	}

	public static void main(String[] args) {
		SpringApplication.run(JobListingApplication.class, args);
	}


}
