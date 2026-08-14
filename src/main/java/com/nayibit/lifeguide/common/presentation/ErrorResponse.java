package com.nayibit.lifeguide.common.presentation;

import java.time.Instant;

/**
 * Single JSON shape returned for every error, regardless of source, so the
 * frontend always parses the same envelope: a stable {@code code} to branch
 * on and a human-readable {@code message}.
 */
public record ErrorResponse(
        String code,
        String message,
        int status,
        String path,
        Instant timestamp
) {
    public static ErrorResponse of(String code, String message, int status, String path) {
        return new ErrorResponse(code, message, status, path, Instant.now());
    }
}
