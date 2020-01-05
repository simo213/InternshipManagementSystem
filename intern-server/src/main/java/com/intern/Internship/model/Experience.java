package com.intern.Internship.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString
public class Experience implements HasID<String> {
    private static final long serialVersionUID = -8091428983912332212L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String ID;

    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String jobName;
    @ManyToOne
    @JoinColumn
    @JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "@ID")
    private Candidate candidate;

    /**
     * Experience constructor
     * 
     * @param companyName: String
     * @param startDate:   LocalDate
     * @param endDate:     LocalDate
     * @param jobName:     String
     * @param candidate:   Candidate
     */
    public Experience(String companyName, LocalDate startDate, LocalDate endDate, String jobName, Candidate candidate) {
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.jobName = jobName;
        this.candidate = candidate;
    }
}
