package com.merry.meal;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.merry.meal.config.AppConstants;
import com.merry.meal.data.Role;
import com.merry.meal.repo.RoleRepository;

@SpringBootApplication
public class MerryMealApplication implements CommandLineRunner {

	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(MerryMealApplication.class, args);
	}

	// model mapper
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	// setting the roles on run
	@Override
	public void run(String... args) throws Exception {

		try {

			// admin role
			Role role_admin = new Role();
			role_admin.setRole_id(AppConstants.ROLE_ADMIN.longValue());
			role_admin.setRole_name("ROLE_ADMIN");

			// user role
			Role role_member = new Role();
			role_member.setRole_id(AppConstants.ROLE_MEMBER.longValue());
			role_member.setRole_name("ROLE_MEMBER");

			// partner role
			Role role_partner = new Role();
			role_partner.setRole_id(AppConstants.ROLE_PARTNER.longValue());
			role_partner.setRole_name("ROLE_PARTNER");

			// rider role
			Role role_rider = new Role();
			role_rider.setRole_id(AppConstants.ROLE_RIDER.longValue());
			role_rider.setRole_name("ROLE_RIDER");

			// volunteer role
			Role role_volunteer = new Role();
			role_volunteer.setRole_id(AppConstants.ROLE_VOLUNTEER.longValue());
			role_volunteer.setRole_name("ROLE_VOLUNTEER");

			// career giver role
			Role role_careergiver = new Role();
			role_careergiver.setRole_id(AppConstants.ROLE_CARERGIVER.longValue());
			role_careergiver.setRole_name("ROLE_CAREGIVER");

			List<Role> roles = List.of(role_admin, role_member, role_partner, role_rider, role_volunteer,
					role_careergiver);

			this.roleRepository.saveAll(roles);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
