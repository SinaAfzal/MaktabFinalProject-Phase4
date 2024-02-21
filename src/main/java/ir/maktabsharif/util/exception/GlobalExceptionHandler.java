package ir.maktabsharif.util.exception;

import jakarta.mail.MessagingException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.TimeLimitExceededException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> accessDeniedExceptionHandler(AccessDeniedException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ExistingEntityCannotBeFetchedException.class)
    public ResponseEntity<String> existingEntityCannotBeFetchedExceptionHandler(ExistingEntityCannotBeFetchedException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> invalidInputExceptionHandler(InvalidInputException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentExceptionHandler(IllegalArgumentException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noSuchElementExceptionHandler(NoSuchElementException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<String> noSuchAlgorithmExceptionHandler(NoSuchAlgorithmException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InputMismatchException.class)
    public ResponseEntity<String> inputMismatchExceptionHandler(InputMismatchException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class) //todo check to see if the exceptions is loaded from org.hibernate package
    public ResponseEntity<String> constraintViolationExceptionHandler(ConstraintViolationException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalCallerException.class)
    public ResponseEntity<String> illegalCallerExceptionHandler(IllegalCallerException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }
    @ExceptionHandler(InvalidKeySpecException.class)
    public ResponseEntity<String> invalidKeySpecExceptionHandler(InvalidKeySpecException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> ioExceptionHandler(IOException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> nullPointerExceptionHandler(NullPointerException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<String> constraintViolationExceptionHandler(jakarta.validation.ConstraintViolationException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }
    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<String> interruptedExceptionHandler(InterruptedException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }
    @ExceptionHandler(TimeLimitExceededException.class)
    public ResponseEntity<String> timeLimitExceededExceptionHandler(TimeLimitExceededException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.GATEWAY_TIMEOUT);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception){
        return new ResponseEntity<>("invalid input pattern",HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<String> messagingExceptionHandler(MessagingException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

}
