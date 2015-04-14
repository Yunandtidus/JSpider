package com.jspider.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CRITERION_VAL", schema = "PUBLIC")
public class CriterionVal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CRITERION_VAL_ID")
	private Long criterionValId;

	private String name;
	private String val;

	public Long getCriterionValId() {
		return criterionValId;
	}

	public void setCriterionValId(Long criterionValId) {
		this.criterionValId = criterionValId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public CriterionVal() {
	}
}
