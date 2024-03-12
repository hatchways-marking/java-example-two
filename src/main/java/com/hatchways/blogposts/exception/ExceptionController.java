package com.hatchways.blogposts.exception;

import com.hatchways.blogposts.schema.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(AuthenticationException exception) {
    return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException exception) {
    return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException exception) {
    return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException exception) {
    return new ResponseEntity<>(
        new ErrorResponse(String.format("%s not found", exception.getResource())),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UserExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserExists(UserExistsException exception) {
    return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
    StringBuilder sb = new StringBuilder();
    exception.getBindingResult().getAllErrors().stream()
        .map(ObjectError::getDefaultMessage)
        .forEach(s -> sb.append(s).append("; "));
    return new ResponseEntity<>(
        new ErrorResponse(sb.toString().stripTrailing()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeError(RuntimeException exception) {
    return new ResponseEntity<>(
        new ErrorResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
