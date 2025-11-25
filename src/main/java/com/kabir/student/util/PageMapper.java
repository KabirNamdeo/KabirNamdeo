package com.kabir.student.util;

import org.springframework.data.domain.Page;

import com.kabir.student.dto.PagedResponse;

public final class PageMapper {

	private PageMapper() {
	}

	public static <T> PagedResponse<T> from(Page<T> page) {
        String sort = page.getPageable().getSort().toString();
		return PagedResponse.<T>builder()
				.content(page.getContent())
				.page(page.getNumber())
				.size(page.getSize())
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.hasNext(page.hasNext())
				.hasPrevious(page.hasPrevious())
				.sort(sort.isBlank() ? "UNSORTED" : sort)
				.build();
	}
}

