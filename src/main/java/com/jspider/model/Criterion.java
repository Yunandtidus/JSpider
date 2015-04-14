package com.jspider.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CRITERION", schema = "PUBLIC")
public class Criterion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CRITERION_ID")
	private Long criterionId;

	/**
	 * Valeurs possible : Keyword, minPrice, maxPrice, region, mode
	 */
	private String name;

	public Long getCriterionId() {
		return criterionId;
	}

	public void setCriterionId(Long criterionId) {
		this.criterionId = criterionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	private String clazz;

	public Criterion() {
	}
}
