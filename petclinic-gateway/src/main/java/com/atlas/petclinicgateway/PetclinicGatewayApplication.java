package com.atlas.petclinicgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
//@EnableOAuth2Client
@EnableOAuth2Sso
public class PetclinicGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetclinicGatewayApplication.class, args);
	}
	
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				
				
				/* Single entry point for all the requests from the browser. 
				 * After Authentication, the browser will be redirected to "http://localhost:8080/petclinic#/h/petclinicdashboard/vpDashboard".  So this url should be used to open the app.
				 * 
				 * All Angular code will be loaded into the browser as a starting point from 4200. Any server calls made by the browser will be addressed by this gateway application and routes 
				 * to respective microservice.  If any of these need a static UI resource, it will be routed to 4200.
				 * 
				 * 
				 * When using docker, the routes should be defined using the container name like this: 				.route("javascript", p -> p.path("/*.js")
						                                                                                            	.uri("http://angular-app:4200"))
						                                                                                            	
				
				 * Since docker uses its own DNS based discovery service, load balancing would be addressed by docker when more than one node instance is present.  
				 * This has been tested through a local swarm cluster. 
				 * 
				 */
				
				.route("javascript", p -> p.path("/*.js")
						.uri("http://localhost:4200"))
				
				.route("css", p -> p.path("/*.css")
						.uri("http://localhost:4200"))	
				
				.route("images", p -> p.path("/petclinic/images/*")
						.uri("http://localhost:4200"))
				
				.route("fontawesome", p -> p.path("/fontawesome**")
						.uri("http://localhost:4200"))
				
				.route("primeicons", p -> p.path("/primeicons**")
						.uri("http://localhost:4200"))
							
				.route("sock-js", p -> p.path("/sockjs-*/*")
						.uri("http://localhost:4200"))
				
				.route("petclinic-web", p -> p.path("/petclinic/client/org/app/p/**")
						.uri("http://localhost:8082"))
				
				.route("petclinic", p -> p.path("/petclinic*")
						.uri("http://localhost:4200"))
				
				.route("petclinic2", p -> p.path("/petclinic/")
						.uri("http://localhost:4200"))
				
				.route("petclinic3", p -> p.path("/petclinic/**")
						.uri("http://localhost:4200"))
				
				
				.route("eureka-client",p->p.path("/test/**")
						.filters(f -> f.hystrix(c -> c.setFallbackUri("forward:/fallback2")))
						.uri("http://localhost:8086"))
				.build();
		
		
		
	/***	Example Route Configuration if vet and owner has been split into ins own horizontal slice. ***/
		
		
				//		.route("vet", p -> p.path("/petclinic/client/org/app/p/vet*/**").or().path("/petclinic/client/org/app/p/petclinicdashboard/**")
				//		.or()
				//		.path("/petclinic/login")
				//		.filters(f -> f.hystrix(c -> c.setFallbackUri("forward:/fallback")))
				//		.uri("lb://vet"))
				//        .route("owners", p -> p.path("/petclinic/client/org/app/p/owner*/**").or().path("/petclinic/client/org/app/p/visit*/**")
				//		.or().path("/petclinic/client/org/app/p/petview*/**")
				//		.filters(f-> f.hystrix(c-> c.setName("owner").setFallbackUri("forward:/fallback")))
				//		.uri("lb://owner"))


	}
	
	@RequestMapping(value="/fallback")
	public String fallback() {
		return "Petclinic Application is currently down, please retry later....";
	}
	
	@RequestMapping(value="/fallback2")
	public String fallback2() {
		return "Fallback 2 ";
	}
}
