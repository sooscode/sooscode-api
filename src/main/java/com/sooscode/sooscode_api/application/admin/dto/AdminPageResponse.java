package com.sooscode.sooscode_api.application.admin.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class AdminPageResponse<T> {
    private List<T> contents;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;

    /**
     * Spring Data Page 객체로부터 AdminPageResponse 생성
     */
    public static <T> AdminPageResponse<T> from(Page<T> page) {
        return AdminPageResponse.<T>builder()
                .contents(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .build();
    }
}