package com.merry.meal.config;

import java.util.List;

import com.merry.meal.payload.SessionDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SessionResponse {
	private List<SessionDto>content;
	private int pageNumber;
	private int pageSize;
	private long totalElement;
	private int totalPages;
	
	private boolean lastPage;
}
