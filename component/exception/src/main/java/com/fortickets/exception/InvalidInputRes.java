package com.fortickets.exception;

public record InvalidInputRes(
    String field,
    String message
) {

}