package com.merry.meal.config;

import java.util.List;

import com.merry.meal.payload.FunDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class FundsResponse {
	private List<FunDto>content;
	private int pageNumber;
	private int pageSize;
	private long totalElement;
	private int totalPages;
	
	private boolean lastPage;
}
