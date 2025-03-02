package com.tus.individual.service;

import java.time.LocalDateTime;
import java.util.List;

public interface ICallFailureService {
	public List<Object[]> getCallFailureCount(String phoneModel, LocalDateTime startDate, LocalDateTime endDate);
	
	public List<Long> getImsisWithFailures(LocalDateTime startTime, LocalDateTime endTime);
	
	public List<Object[]> getFailuresByImsi(Long imsi);
	
	public List<Object[]> getCallFailureStatsByImsi(LocalDateTime startDateTime, LocalDateTime endDateTime);

	//US12
	public List<Object[]>  findFailuresByImsiAndTimePeriod(Long imsi, LocalDateTime startDate, LocalDateTime endDate);
	
	//US8
	public List<Object[]> getFailureSummaryByModel(String phoneModel);
	
	//US15
	public List<Object[]> getTop10Failures(LocalDateTime startDate, LocalDateTime endDate);
	
	//US18
	public List<Object[]> getTop10ImsisWithFailures(LocalDateTime startDate, LocalDateTime endDate);

}
