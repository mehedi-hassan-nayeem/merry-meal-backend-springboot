package com.merry.meal.payload;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MealResponse {

	private List<MealDto> content;
	private int pageNumber;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private boolean lastPage;

}
