package com.tus.individual.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class CallFailure {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "date_time")
	private LocalDateTime dateTime;
	
	@Column(name = "event_id")
    private Integer eventId;
	@Column(name = "cause_code")
    private Integer causeCode;
	
	@Column(name = "failure_class")
    private Integer failureClass;
	
    @Column(name = "ue_type")
    private Integer ueType;
    
    @Column(name = "market")
    private Integer market;
    @Column(name = "operator")
    private Integer operator;	
    
    @Column(name = "cell_id")
    private Integer cellId;
    
    @Column(name = "duration")
    private Integer duration;	
    
    @Column(name = "ne_version")
    private String neVersion;	
    
    @Column(name = "imsi")
    private Long imsi;	
    
    @Column(name = "hier3_id")
    private Long hier3Id;	
    @Column(name = "hier32_id")
    private Long hier32Id;	
    @Column(name = "hier321_id")
    private Long hier321Id;
}
