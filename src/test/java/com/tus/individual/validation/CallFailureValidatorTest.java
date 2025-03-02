package com.tus.individual.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tus.individual.exception.CallFailureValidationException;
import com.tus.individual.model.*;

@ExtendWith(MockitoExtension.class)
class CallFailureValidatorTest {
    private CallFailureValidator callFailureValidator;

    private CallFailure validCallFailure;

    @BeforeEach
    void setUp() {
    	callFailureValidator = new CallFailureValidator();
    	
        validCallFailure = new CallFailure();
        validCallFailure.setDateTime(LocalDateTime.now());
        validCallFailure.setUeType(100200);
        validCallFailure.setMarket(310);
        validCallFailure.setOperator(410);
        validCallFailure.setEventId(4098);
        validCallFailure.setCauseCode(0);
        validCallFailure.setFailureClass(1);
    }

    @Test
    void testValidateCallFailure_ValidData_NoExceptionThrown() {
        // Ensures a valid CallFailure passes validation without exceptions
        UE ue = new UE();
        ue.setTac(100200);

        MarketOperator marketOperator = new MarketOperator();
        marketOperator.setKey(new MarketOperatorKey(310, 410));

        EventCause eventCause = new EventCause();
        eventCause.setKey(new EventCauseKey(0, 4098));

        FailureClass failureClass = new FailureClass();
        failureClass.setFailureClassId(1);

        assertDoesNotThrow(() -> {
            callFailureValidator.validateCallFailure(validCallFailure, 
                List.of(eventCause), List.of(failureClass), List.of(ue), List.of(marketOperator));
        });
    }

    @Test
    void testCheckDateTimes_NullDateTime_ThrowsException() {
        // Ensures an exception is thrown when the date/time is null
        validCallFailure.setDateTime(null);
        CallFailureValidationException exception = assertThrows(CallFailureValidationException.class, () -> 
            callFailureValidator.validateCallFailure(validCallFailure, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));
        assertEquals("Date/Time cannot be null.", exception.getMessage());
    }

    @Test
    void testCheckUEs_InvalidTac_ThrowsException() {
        // Ensures an exception is thrown when the UE type is invalid
        CallFailureValidationException exception = assertThrows(CallFailureValidationException.class, () -> 
            callFailureValidator.validateCallFailure(validCallFailure, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));
        assertEquals("No UEs available to validate TAC.", exception.getMessage());
    }

    @Test
    void testCheckMncMcc_InvalidCombination_ThrowsException() {
        // Ensures an exception is thrown when the MCC-MNC combination is invalid
        validCallFailure.setMarket(-1235656);
        validCallFailure.setOperator(456);

        UE ue = new UE();
        ue.setTac(100200);

        EventCause eventCause = new EventCause();
        eventCause.setKey(new EventCauseKey(0, 4098));

        MarketOperator unrelatedMarketOperator = new MarketOperator();
        unrelatedMarketOperator.setKey(new MarketOperatorKey(111, 222));

        CallFailureValidationException exception = assertThrows(CallFailureValidationException.class, () -> 
            callFailureValidator.validateCallFailure(validCallFailure, 
                List.of(eventCause), Collections.emptyList(), List.of(ue), List.of(unrelatedMarketOperator)));

        assertEquals("Invalid MCC-MNC combination: -1235656-456", exception.getMessage());
    }

    @Test
    void testCheckEventIdAndCauseCode_InvalidCombination_ThrowsException() {
        // Ensures an exception is thrown when the event ID and cause code combination is invalid
        validCallFailure.setEventId(9999);
        validCallFailure.setCauseCode(8888);
        validCallFailure.setUeType(100200);

        UE ue = new UE();
        ue.setTac(100200);

        EventCause eventCause = new EventCause();
        eventCause.setKey(new EventCauseKey(1111, 2222));

        MarketOperator marketOperator = new MarketOperator();
        marketOperator.setKey(new MarketOperatorKey(310, 410));

        CallFailureValidationException exception = assertThrows(CallFailureValidationException.class, () -> 
            callFailureValidator.validateCallFailure(validCallFailure, 
                List.of(eventCause), Collections.emptyList(), List.of(ue), List.of(marketOperator)));

        assertEquals("Invalid Event ID (9999) and Cause Code (8888).", exception.getMessage());
    }

    @Test
    void testCheckFailureClass_InvalidFailureClass_ThrowsException() {
        // Ensures an exception is thrown when the failure class is invalid
        validCallFailure.setFailureClass(999);
        validCallFailure.setUeType(100200);
        validCallFailure.setMarket(310);
        validCallFailure.setOperator(410);
        validCallFailure.setEventId(4098);
        validCallFailure.setCauseCode(0);

        UE ue = new UE();
        ue.setTac(100200);

        MarketOperator marketOperator = new MarketOperator();
        marketOperator.setKey(new MarketOperatorKey(310, 410));

        EventCause eventCause = new EventCause();
        eventCause.setKey(new EventCauseKey(0, 4098));

        CallFailureValidationException exception = assertThrows(CallFailureValidationException.class, () -> 
            callFailureValidator.validateCallFailure(validCallFailure, 
                List.of(eventCause), List.of(), List.of(ue), List.of(marketOperator)));

        assertEquals("Invalid Failure Class: 999", exception.getMessage());
    }
}
