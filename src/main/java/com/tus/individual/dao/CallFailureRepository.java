package com.tus.individual.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tus.individual.model.CallFailure;


@Repository
public interface CallFailureRepository extends CrudRepository<CallFailure, Long> {
	@Query("SELECT cf.eventId, cf.causeCode, cf.imsi, cf.dateTime FROM CallFailure cf " +
			"JOIN UE ue ON cf.ueType = ue.tac " +
			"WHERE ue.model = :phoneModel " +
			"AND cf.dateTime BETWEEN :startDate AND :endDate")
	List<Object[]>  countFailuresByPhoneModelAndTimeRange(String phoneModel, LocalDateTime startDate, LocalDateTime endDate);

	@Query("SELECT DISTINCT cf.imsi FROM CallFailure cf " +
			"WHERE cf.dateTime BETWEEN :startDate AND :endDate")
	List<Long> findDistinctImsisWithFailures(LocalDateTime startDate, LocalDateTime endDate);

	@Query("SELECT cf.eventId, cf.causeCode, ec.description, cf.dateTime FROM CallFailure cf " +
			"LEFT JOIN EventCause ec ON cf.eventId = ec.key.eventId AND (cf.causeCode IS NULL OR cf.causeCode = ec.key.causeCode) " +
			"WHERE cf.imsi = :imsi")
	List<Object[]> findEventIdCauseCodeAndDescriptionByImsi(Long imsi);

	@Query("SELECT cf.imsi, COUNT(cf), SUM(cf.duration) FROM CallFailure cf WHERE cf.dateTime BETWEEN :startDate AND :endDate GROUP BY cf.imsi")
	List<Object[]> countFailuresAndDurationByImsi(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	//US12
	@Query("SELECT cf.eventId, cf.causeCode, cf.dateTime FROM CallFailure cf " +
			"WHERE cf.imsi = :imsi " +
			"AND cf.dateTime BETWEEN :startDate AND :endDate")
	List<Object[]>  findFailuresByImsiAndTimePeriod(Long imsi, LocalDateTime startDate, LocalDateTime endDate);

	//US8
	@Query("SELECT cf.eventId, cf.causeCode, COUNT(*) AS occurrences " +
			"FROM CallFailure cf " +
			"JOIN UE ue ON cf.ueType = ue.tac " +
			"WHERE ue.model = :phoneModel " +
			"GROUP BY cf.eventId, cf.causeCode")
	List<Object[]> findFailureSummaryByModel(@Param("phoneModel") String phoneModel);
	
	 //US15
	 @Query("SELECT cf.eventId, cf.causeCode, cf.market, cf.operator, cf.cellId, COUNT(cf.id) AS failureCount FROM CallFailure cf "
	 		+ "WHERE cf.dateTime BETWEEN :startDate AND :endDate GROUP BY cf.eventId, cf.causeCode, cf.market, cf.operator, cf.cellId "
	 		+ "ORDER BY failureCount DESC LIMIT 10")
	    List<Object[]> findTop10ByDateRange(LocalDateTime startDate, LocalDateTime endDate);
	    
	 //US18
	 @Query("SELECT cf.imsi, COUNT(cf.id) AS failureCount FROM CallFailure cf " +
	       "WHERE cf.dateTime BETWEEN :startDate AND :endDate " +
	       "GROUP BY cf.imsi " +
	       "ORDER BY failureCount DESC LIMIT 10")
	    List<Object[]> findTop10ImsisByFailureCount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


}
