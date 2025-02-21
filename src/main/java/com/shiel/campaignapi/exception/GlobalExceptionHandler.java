package com.shiel.campaignapi.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("type", "about:blank");
		body.put("title", ex.getTitle());
		body.put("detail", ex.getMessage());
		body.put("status", ex.getStatus());
		body.put("instance", "/api/auth/signin");

		return ResponseEntity.status(ex.getStatus()).body(body);
	}

	@ExceptionHandler(UserIllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(UserIllegalArgumentException ex,
			HttpServletRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("type", "about:blank");
		body.put("title", ex.getTitle());
		body.put("status", ex.getStatus());
		body.put("detail", ex.getMessage());
		String instance = request.getRequestURI();
		body.put("instance", instance);

		return ResponseEntity.status(ex.getStatus()).body(body);
	}

	@ExceptionHandler(UserBadCredential.class)
	public ResponseEntity<Map<String, Object>> handleBadCredentiaException(UserBadCredential ex,
			HttpServletRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("type", "about:blank");
		body.put("title", ex.getTitle());
		body.put("status", ex.getStatus());
		body.put("detail", ex.getMessage());

		String instance = request.getRequestURI();
		body.put("instance", instance);

		return ResponseEntity.status(ex.getStatus()).body(body);
	}

	@ExceptionHandler(UserBadRequest.class)
	public ResponseEntity<Map<String, Object>> handleBadRequestException(UserBadRequest ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("type", "about:blank");
		body.put("title", ex.getTitle());
		body.put("status", ex.getStatus());
		body.put("detail", ex.getMessage());
		body.put("instance", "/api/auth/signin");
		return ResponseEntity.status(ex.getStatus()).body(body);
	}

}
