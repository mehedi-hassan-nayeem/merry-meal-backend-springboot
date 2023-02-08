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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "delivery")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Delivery {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "delivery_id")
	private Long delivery_id;
	@Column(name = "delivery_number")
	private Integer delivery_number;
	@Column(name = "delivery_status")
	private String status;
	@Column(name = "delivery_address")
	private String delivery_address;
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private User user;
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "meal_id", referencedColumnName = "meal_id")
	private Meal meal;
	@OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "delivery")
	private RideDelivery rideDelivery;
}
