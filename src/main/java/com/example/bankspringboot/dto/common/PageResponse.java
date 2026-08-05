package com.example.bankspringboot.dto.common;

import java.util.List;

public record PageResponse<T> (
        List<T> items,
        int pageNumber,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
}
