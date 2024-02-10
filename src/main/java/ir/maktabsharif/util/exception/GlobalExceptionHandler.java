package ir.maktabsharif.util.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
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

    @ExceptionHandler
    public ResponseEntity<String> accessDeniedExceptionHandler(AccessDeniedException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<String> existingEntityCannotBeFetchedExceptionHandler(ExistingEntityCannotBeFetchedException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> invalidInputExceptionHandler(InvalidInputException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> illegalArgumentExceptionHandler(IllegalArgumentException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> noSuchElementExceptionHandler(NoSuchElementException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> noSuchAlgorithmExceptionHandler(NoSuchAlgorithmException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> inputMismatchExceptionHandler(InputMismatchException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler //todo check to see if the exceptions is loaded from org.hibernate package
    public ResponseEntity<String> constraintViolationExceptionHandler(ConstraintViolationException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> illegalCallerExceptionHandler(IllegalCallerException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }
    @ExceptionHandler
    public ResponseEntity<String> invalidKeySpecExceptionHandler(InvalidKeySpecException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }
    @ExceptionHandler
    public ResponseEntity<String> ioExceptionHandler(IOException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }
    @ExceptionHandler
    public ResponseEntity<String> nullPointerExceptionHandler(NullPointerException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }
    @ExceptionHandler
    public ResponseEntity<String> constraintViolationExceptionHandler(jakarta.validation.ConstraintViolationException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }
    @ExceptionHandler
    public ResponseEntity<String> interruptedExceptionHandler(InterruptedException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.CONFLICT);
    }
    @ExceptionHandler
    public ResponseEntity<String> timeLimitExceededExceptionHandler(TimeLimitExceededException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.GATEWAY_TIMEOUT);
    }
    @ExceptionHandler
    public ResponseEntity<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception){

        return new ResponseEntity<>("invalid input pattern",HttpStatus.CONFLICT);
    }


}
