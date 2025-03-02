package com.tus.individual.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tus.individual.model.FailureClass;


@Repository
public interface FailureClassRepository  extends CrudRepository<FailureClass, Integer> {

}

