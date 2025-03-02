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
public class EventCauseKey {
	private Integer causeCode;
	private Integer eventId;
	
	
	public boolean isNull() {
		return causeCode == null || eventId == null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventCauseKey)) return false;
		
		EventCauseKey that = (EventCauseKey) o;
		return Objects.equals(causeCode,  that.causeCode) && Objects.equals(eventId, that.eventId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(causeCode, eventId);
	}
}