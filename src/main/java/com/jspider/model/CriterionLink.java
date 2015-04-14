package com.jspider.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CRITERION_LINK", schema = "PUBLIC")
public class CriterionLink {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CRITERION_LINK_ID")
	private Long criterionLinkId;

	@OneToOne
	@JoinColumn(name = "CRITERION_ID")
	private Criterion criterion;

	@OneToMany
	@JoinColumn(name = "CRITERION_LINK_ID")
	private Set<CriterionVal> criterionVals;

	@ManyToOne
	@JoinColumn(name = "SEARCH_ID")
	private Search search;

	public Long getCriterionLinkId() {
		return criterionLinkId;
	}

	public void setCriterionLinkId(Long criterionLinkId) {
		this.criterionLinkId = criterionLinkId;
	}

	public Criterion getCriterion() {
		return criterion;
	}

	public void setCriterion(Criterion criterion) {
		this.criterion = criterion;
	}

	public Set<CriterionVal> getCriterionVals() {
		return criterionVals;
	}

	public void setCriterionVals(Set<CriterionVal> criterionVals) {
		this.criterionVals = criterionVals;
	}

	public CriterionLink() {
	}

}
