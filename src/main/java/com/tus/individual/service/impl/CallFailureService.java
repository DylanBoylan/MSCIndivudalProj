package com.tus.individual.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tus.individual.service.ICallFailureService;

@Service
public class CallFailureService implements ICallFailureService {
    /*
	private CallFailureRepository callFailureRepository;
    
    @Autowired
    public CallFailureService(CallFailureRepository callFailureRepository) {
    	this.callFailureRepository=callFailureRepository;
	}


	public List<Object[]> getCallFailureCount(String phoneModel, LocalDateTime startDate, LocalDateTime endDate) {
        return callFailureRepository.countFailuresByPhoneModelAndTimeRange(phoneModel, startDate, endDate);
    }
	
	public List<Long> getImsisWithFailures(LocalDateTime startTime, LocalDateTime endTime) {
        return callFailureRepository.findDistinctImsisWithFailures(startTime, endTime);
    }
	public List<Object[]> getFailuresByImsi(Long imsi) {
        return callFailureRepository.findEventIdCauseCodeAndDescriptionByImsi(imsi);
    }

	public List<Object[]> getCallFailureStatsByImsi(LocalDateTime startDateTime, LocalDateTime endDateTime) {
	    return callFailureRepository.countFailuresAndDurationByImsi(startDateTime, endDateTime);
	}
	//US12
	@Override
	public List<Object[]> findFailuresByImsiAndTimePeriod(Long imsi, LocalDateTime startDate, LocalDateTime endDate) {

		return callFailureRepository.findFailuresByImsiAndTimePeriod(imsi, startDate, endDate);
	}
	
	//US15
	public List<Object[]> getTop10Failures(LocalDateTime startDate, LocalDateTime endDate) {
        return callFailureRepository.findTop10ByDateRange(startDate, endDate);
    }
	
	//US8
	public List<Object[]> getFailureSummaryByModel(String phoneModel) {
        return callFailureRepository.findFailureSummaryByModel(phoneModel);
    }
	
	//US18
	public List<Object[]> getTop10ImsisWithFailures(LocalDateTime startDate, LocalDateTime endDate) {
	    return callFailureRepository.findTop10ImsisByFailureCount(startDate, endDate);
	}
*/
}
