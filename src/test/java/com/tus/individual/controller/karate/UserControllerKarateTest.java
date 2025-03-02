package com.tus.individual.controller.karate;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.intuit.karate.junit5.Karate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerKarateTest {
    // CRUD operations on users
	@DirtiesContext
    @Karate.Test
    Karate runCreateUserTest() {
    	return Karate.run("classpath:features/create-user.feature").relativeTo(getClass());
    }
    
	@DirtiesContext
    @Karate.Test
    Karate runUpdateUserTest() {
    	return Karate.run("classpath:features/update-user.feature").relativeTo(getClass());
    }
    
	@DirtiesContext
    @Karate.Test
    Karate runDeleteUserTest() {
    	return Karate.run("classpath:features/delete-user.feature").relativeTo(getClass());
    }
    
}