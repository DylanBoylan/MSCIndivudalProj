package com.tus.individual.controller.karate;

import org.springframework.boot.test.context.SpringBootTest;

import com.intuit.karate.junit5.Karate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthControllerKarateTest {
	@Karate.Test
	Karate runAllTests() {
		return Karate.run(
			 "classpath:features/login-admin.feature",
			 "classpath:features/login-networkEng.feature",
			 "classpath:features/login-customer.feature",
			 "classpath:features/login-support-engineer.feature").relativeTo(getClass());
	}
}