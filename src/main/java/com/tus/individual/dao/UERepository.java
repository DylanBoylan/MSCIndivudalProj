package com.tus.individual.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tus.individual.model.UE;


@Repository
public interface UERepository extends CrudRepository<UE, Integer>{

}
