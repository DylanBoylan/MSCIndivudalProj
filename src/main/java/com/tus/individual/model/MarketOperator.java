package com.tus.individual.model;


import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MarketOperator {
	@EmbeddedId
	private MarketOperatorKey key;
	
	@Column
	private String country;
	
	@Column
	private String operator;
}
