package com.tus.individual.model;

import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class MarketOperatorKey {
	private Integer mcc;
	private Integer mnc;
	
	public boolean isNull() {
		return mcc == null || mnc == null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MarketOperator)) return false;
		
		MarketOperatorKey that = (MarketOperatorKey) o;
		return Objects.equals(mcc,  that.mcc) && Objects.equals(mnc, that.mnc);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(mcc, mnc);
	}
}
