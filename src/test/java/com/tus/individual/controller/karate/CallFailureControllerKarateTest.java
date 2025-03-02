package com.tus.individual.controller.karate;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.intuit.karate.junit5.Karate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CallFailureControllerKarateTest {
	@Karate.Test
	@DirtiesContext
	Karate runOtherTests() {
		return Karate.run(
				"classpath:features/count-by-imsi.feature", 
				"classpath:features/failure-summary.feature",
				"classpath:features/Top10Imsi.feature"
		).relativeTo(getClass());
	}
	
	@Karate.Test
	@DirtiesContext
	Karate testCustomerServiceCallFailuresByIMSI() {
		return Karate.run("classpath:features/CallFailureControllerCustServTest.feature").relativeTo(getClass());
	}
	
	@Karate.Test
	@DirtiesContext
	Karate testSupportEngCountByPhoneModelAndDatetime() {
		return Karate.run("classpath:features/SupportEngCountByPhoneModelAndDatetime.feature").relativeTo(getClass());
	}
	
	@Karate.Test
	@DirtiesContext
	Karate testNetworkEngTop10IMSIByTime() {
		return Karate.run("classpath:features/Top10Imsi.feature").relativeTo(getClass());
	}
	
	
	
}