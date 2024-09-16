package com.xcentral.xcentralback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.xcentral.xcentralback.models")
public class XcentralbackApplication {

	public static void main(String[] args) {
		SpringApplication.run(XcentralbackApplication.class, args);
	}

}
