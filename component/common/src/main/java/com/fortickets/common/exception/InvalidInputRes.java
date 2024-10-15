package com.fortickets.common.exception;

public record InvalidInputRes(
    String field,
    String message
) {

}