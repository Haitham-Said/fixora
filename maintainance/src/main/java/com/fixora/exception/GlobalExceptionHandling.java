package com.fixora.exception;

import com.fixora.maintainance.user.domain.exception.InvalidCredentialException;
import com.fixora.maintainance.user.domain.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException ex)
    {
        ErrorResponse errorResponse=new ErrorResponse(ErrorCodes.USER_NOT_FOUND,ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ErrorResponse> invalidCredentialsException(InvalidCredentialException ex)
    {
        ErrorResponse errorResponse=new ErrorResponse(ErrorCodes.INVALID_CREDENTIALS,ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> authorizationDeniedException(Exception ex){
        ErrorResponse errorResponse=new ErrorResponse(ErrorCodes.FORBIDDEN_ACCESS,ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> genericException(Exception ex)
    {
        ErrorResponse errorResponse=new ErrorResponse(ErrorCodes.ERROR,ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
