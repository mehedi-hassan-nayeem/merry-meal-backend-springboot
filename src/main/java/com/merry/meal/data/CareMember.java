package com.merry.meal.data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "care_member")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CareMember {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name = "care_member_id")
	private Long care_member_id;
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "care_giver_id", referencedColumnName = "user_id")
	private User caregiver;
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", referencedColumnName = "user_id")
	private User user;
}
