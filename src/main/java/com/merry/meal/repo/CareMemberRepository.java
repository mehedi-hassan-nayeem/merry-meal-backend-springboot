package com.merry.meal.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.merry.meal.data.CareMember;

@Repository
public interface CareMemberRepository extends JpaRepository<CareMember, Long> {

}
