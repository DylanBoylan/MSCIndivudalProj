package com.tus.individual.controller.karate;

import org.springframework.boot.test.context.SpringBootTest;

import com.intuit.karate.junit5.Karate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LogControllerKarateTest {
	@Karate.Test
	Karate runLogTest() {
		return Karate.run("classpath:features/LogControllerTest.feature").relativeTo(getClass());
	}
}