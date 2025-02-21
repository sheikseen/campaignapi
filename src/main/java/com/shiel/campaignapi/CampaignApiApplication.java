package com.shiel.campaignapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CampaignApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampaignApiApplication.class, args);
	}

}
  