package com.tus.individual.validation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.tus.individual.exception.CallFailureValidationException;
import com.tus.individual.model.CallFailure;
import com.tus.individual.model.EventCause;
import com.tus.individual.model.FailureClass;
import com.tus.individual.model.MarketOperator;
import com.tus.individual.model.UE;

@Component
public class CallFailureValidator implements ICallFailureValidator {
	
	@Override
	public void validateCallFailure(CallFailure callFailure, List<EventCause> eventCauses, List<FailureClass> failureClasses, List<UE> ues, List<MarketOperator> marketOperators) throws CallFailureValidationException {
		checkDateTimes(callFailure);
		checkUEs(callFailure, ues);
		checkMncMcc(callFailure, marketOperators);
		checkEventIdAndCauseCode(callFailure, eventCauses);
		checkFailureClass(callFailure, failureClasses);
	}
	
	//This will check to ensure the date/time row is in DD/MM/YYYY HH:mm format
	private void checkDateTimes(CallFailure callFailure) throws CallFailureValidationException {
	    if (callFailure.getDateTime() == null) {
	        throw new CallFailureValidationException("Date/Time cannot be null.");
	    }

	    try {
	        LocalDateTime dateTime = callFailure.getDateTime();
	        DateTimeFormatter europeanFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	        // Check if the formatted date matches expected pattern
	        String formattedDate = dateTime.format(europeanFormat);

	    } catch (Exception e) {
	        throw new CallFailureValidationException("Incorrect time or date format for Call Failure record.");
	    }
	}
	
	//This will check to ensure the UE Type in Base Data matches a TAC in the UE table
	private void checkUEs(CallFailure callFailure, List<UE> ues) throws CallFailureValidationException {
	    if (ues == null || ues.isEmpty()) {
	        throw new CallFailureValidationException("No UEs available to validate TAC.");
	    }

	    boolean isValidTAC = ues.stream()
	            .anyMatch(ue -> ue.getTac().equals(callFailure.getUeType()));

	    if (!isValidTAC) {
	        throw new CallFailureValidationException("Invalid TAC: " + callFailure.getUeType());
	    }
	}

	
	//This will check to ensure the MNC-MCC combination matches a valid combination in the MNC table
	private void checkMncMcc(CallFailure callFailure, List<MarketOperator> marketOperators) throws CallFailureValidationException {
	    if (marketOperators == null || marketOperators.isEmpty()) {
	        throw new CallFailureValidationException("No Market Operators available to validate MCC-MNC.");
	    }

	    boolean isValidMarketOperator = marketOperators.stream()
	            .anyMatch(mo -> mo.getKey().getMcc().equals(callFailure.getMarket()) 
	                         && mo.getKey().getMnc().equals(callFailure.getOperator()));

	    if (!isValidMarketOperator) {
	        throw new CallFailureValidationException("Invalid MCC-MNC combination: " 
	            + callFailure.getMarket() + "-" + callFailure.getOperator());
	    }
	}
	
	//This will check to ensure the EventID and CauseCodes match a valid combination
	private void checkEventIdAndCauseCode(CallFailure callFailure, List<EventCause> eventCauses) throws CallFailureValidationException {
	    if (eventCauses == null || eventCauses.isEmpty()) {
	        throw new CallFailureValidationException("No Event Causes available to validate Event ID and Cause Code.");
	    }

	    boolean isValid = eventCauses.stream()
	            .anyMatch(eventCause -> eventCause.getKey().getEventId().equals(callFailure.getEventId()) &&
	                                    eventCause.getKey().getCauseCode().equals(callFailure.getCauseCode()));

	    if (!isValid) {
	        throw new CallFailureValidationException("Invalid Event ID (" + callFailure.getEventId() + 
	                                                 ") and Cause Code (" + callFailure.getCauseCode() + ").");
	    }
	}
	
	// This will check to ensure the Failure Class matches a valid entry in the FailureClass table
	private void checkFailureClass(CallFailure callFailure, List<FailureClass> failureClasses) throws CallFailureValidationException {
	    boolean isValidFailureClass = failureClasses.stream()
	            .anyMatch(fc -> fc.getFailureClassId().equals(callFailure.getFailureClass()));

	    if (!isValidFailureClass) {
	        throw new CallFailureValidationException("Invalid Failure Class: " + callFailure.getFailureClass());
	    }
	}


}

	