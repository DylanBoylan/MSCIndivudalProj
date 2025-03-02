package com.tus.individual.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tus.individual.model.MarketOperator;
import com.tus.individual.model.MarketOperatorKey;


@Repository
public interface MarketOperatorRepository extends CrudRepository<MarketOperator, MarketOperatorKey>{

}
