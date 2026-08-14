package com.nayibit.lifeguide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * {@code @ConfigurationPropertiesScan} discovers every
 * {@code @ConfigurationProperties} class (e.g. {@code JwtProperties}) and
 * binds it via Boot's config-properties binder, which supports records'
 * constructor binding. Plain {@code @Component} on a properties record is
 * a trap: the regular container tries to autowire the constructor's
 * parameters as beans instead of binding them from config, and fails with
 * "No qualifying bean of type 'java.lang.String'" for the first String field.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class LifeguideApplication {

	public static void main(String[] args) {
		SpringApplication.run(LifeguideApplication.class, args);
	}

}
