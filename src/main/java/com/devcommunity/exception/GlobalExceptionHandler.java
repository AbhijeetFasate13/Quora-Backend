package com.devcommunity.exception;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getDefaultMessage()).toList();
		Map<String, List<String>> errorResponse = new HashMap<>();
		errorResponse.put("errors", errors);
		return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DeveloperCommunityException.class)
	public ResponseEntity<ErrorResponse> handleCustomErrors(DeveloperCommunityException ex) {
		ErrorResponse er=new ErrorResponse();
		er.setDate(LocalDate.now());
		er.setMessage(ex.getMessage());
		return new ResponseEntity<>(er,HttpStatus.BAD_REQUEST);
	}
	
	
}