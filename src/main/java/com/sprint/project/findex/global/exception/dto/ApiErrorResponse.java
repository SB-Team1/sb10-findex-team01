package com.sprint.project.findex.global.exception.dto;

import java.time.Instant;

public record ApiErrorResponse(
    Instant timestamp,
    int status,
    String message,
    String detail,
    String path
) { }
