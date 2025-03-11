package com.tus.individual.models;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tus.individual.dao.ActionRepository;
import com.tus.individual.model.Action;
import com.tus.individual.service.IQueryControllerService;
import com.tus.individual.service.impl.QueryControllerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ActionServiceTest {

    @Mock
    private ActionRepository actionRepository;  // ✅ Mock the repository

    @InjectMocks
    private QueryControllerService queryControllerService;  // ✅ Test service

    @BeforeEach
    void setUp() {
        queryControllerService = new QueryControllerService(actionRepository, null, null, null);
    }

    // ✅ Test retrieving all actions
    @Test
    void testGetAllActions() {
        Action mockAction = new Action();
        mockAction.setPlayerId(10);
        mockAction.setMatchId(5);
        mockAction.setGoals(2);
        mockAction.setShotEfficiency(80.5);

        when(actionRepository.getAllActions()).thenReturn(List.of(mockAction));

        List<Action> result = queryControllerService.getAllActions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getPlayerId());
        assertEquals(5, result.get(0).getMatchId());
        assertEquals(2, result.get(0).getGoals());
        assertEquals(80.5, result.get(0).getShotEfficiency());

        verify(actionRepository, times(1)).getAllActions();
    }
}
