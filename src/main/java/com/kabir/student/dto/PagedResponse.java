package com.kabir.student.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PagedResponse<T> {
	private final List<T> content;
	private final long totalElements;
	private final int totalPages;
	private final int page;
	private final int size;
	private final boolean hasNext;
	private final boolean hasPrevious;
	private final String sort;
}

