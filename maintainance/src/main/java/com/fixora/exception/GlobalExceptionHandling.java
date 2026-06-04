package com.fixora.exception;

import com.fixora.maintainance.maintainancerequest.domain.exception.InvalidWorkflowStateException;
import com.fixora.maintainance.maintainancerequest.domain.exception.MaintenanceWorkflowException;
import com.fixora.maintainance.maintainancerequest.domain.exception.WorkflowAccessDeniedException;
import com.fixora.maintainance.property.domain.exception.CompanyWorkflowConfigNotFoundException;
import com.fixora.maintainance.user.domain.exception.InvalidCredentialException;
import com.fixora.maintainance.user.domain.exception.UserNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;


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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("Validation failed");

        ErrorResponse errorResponse = new ErrorResponse(ErrorCodes.BAD_REQUEST, message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> authorizationDeniedException(Exception ex){
        ErrorResponse errorResponse=new ErrorResponse(ErrorCodes.FORBIDDEN_ACCESS,ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CompanyWorkflowConfigNotFoundException.class)
    public ResponseEntity<ErrorResponse> workflowConfigNotFound(CompanyWorkflowConfigNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCodes.WORKFLOW_CONFIG_NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WorkflowAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> workflowAccessDenied(WorkflowAccessDeniedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCodes.FORBIDDEN_ACCESS, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidWorkflowStateException.class)
    public ResponseEntity<ErrorResponse> invalidWorkflowState(InvalidWorkflowStateException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCodes.WORKFLOW_CONFLICT, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MaintenanceWorkflowException.class)
    public ResponseEntity<ErrorResponse> maintenanceWorkflow(MaintenanceWorkflowException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCodes.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> genericException(Exception ex)
    {
        ErrorResponse errorResponse=new ErrorResponse(ErrorCodes.ERROR,ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
