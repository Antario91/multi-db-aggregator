package com.multidb.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class
})
public class MultiDBAggregatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiDBAggregatorApplication.class, args);
	}
}
