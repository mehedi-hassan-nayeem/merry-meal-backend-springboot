package com.merry.meal.data;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long user_id;

	@Column(name = "name")
	private String name;

	@Column(name = "detail")
	private String detail;

	@Column(name = "phone_number")
	private String phone_number;

	@Column(name = "birth")
	private String birth;

	@Column(name = "gender")
	private String gender;

	@Column(name = "profile_image")
	private String profile_image;

	@OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", referencedColumnName = "account_id")
	private Account account;
	
	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "rider")
	private List<RideDelivery> rideDeliveries;
	
	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "user")
	private List<Meal> meals;
	
	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "user")
	private List<CareMember> requestedCares;
	
	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "caregiver")
	private List<CareMember> acceptedCares;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
	private List<Session>session;
}
