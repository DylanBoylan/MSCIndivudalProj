package com.tus.individual.validation;

import java.util.List;

import com.tus.individual.exception.CallFailureValidationException;
import com.tus.individual.model.CallFailure;
import com.tus.individual.model.EventCause;
import com.tus.individual.model.FailureClass;
import com.tus.individual.model.MarketOperator;
import com.tus.individual.model.UE;

public interface ICallFailureValidator {
	public void validateCallFailure(CallFailure callFailure, List<EventCause> eventCauses, List<FailureClass> failureClasses, List<UE> ues, List<MarketOperator> marketOperators) throws CallFailureValidationException;
}
