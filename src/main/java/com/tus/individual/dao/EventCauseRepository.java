package com.tus.individual.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tus.individual.model.EventCause;
import com.tus.individual.model.EventCauseKey;

@Repository
public interface EventCauseRepository extends CrudRepository<EventCause, EventCauseKey>{

}
