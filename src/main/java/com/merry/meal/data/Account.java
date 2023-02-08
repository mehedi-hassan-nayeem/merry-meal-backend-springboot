package com.merry.meal.data;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "account")
@Getter
@Setter
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private Long account_id;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "provider")
	private String provider;

	@OneToMany(mappedBy = "account", cascade = CascadeType.MERGE)
	private List<UserRole> userRoles;

	@OneToOne(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private User user;
}
