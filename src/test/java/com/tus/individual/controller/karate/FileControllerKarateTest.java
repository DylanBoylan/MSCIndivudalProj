package com.tus.individual.controller.karate;

import org.springframework.boot.test.context.SpringBootTest;

import com.intuit.karate.junit5.Karate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FileControllerKarateTest {
	@Karate.Test
	Karate runAllTests() {
		return Karate.run("classpath:features/upload-file.feature").relativeTo(getClass());
	}
}