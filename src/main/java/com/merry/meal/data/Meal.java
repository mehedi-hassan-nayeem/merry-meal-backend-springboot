package com.merry.meal.data;

import java.util.List;

import javax.annotation.Generated;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "meal")
@Getter
@Setter
public class Meal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "meal_id")
	private Long mealId;
	@Column(name = "meal_img")
	private String image;
	@Column(name = "status")
	private String status;
	@Column(name = "meal_name")
	private String meal_name;
	@Column(name = "meal_desc")
	private String meal_desc;
	@Column(name = "category")
	private String category;
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private User user;
	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "meal")
	private List<Delivery> delivery;
}
