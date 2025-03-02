package com.tus.individual.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tus.individual.model.Action;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
}
