package sep3.project.jobservice.grpc;

import io.grpc.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@GrpcAdvice
public class GrpcExceptionAdvice {
    @GrpcExceptionHandler({ConstraintViolationException.class, IllegalArgumentException.class})
    public Status handleValidationException(Exception exception) {
        log.error(exception.getMessage(), exception);

        if (exception instanceof ConstraintViolationException constraintViolationException) {
            Map<String, String> errors = new HashMap<>();

            for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
                errors.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
            }

            return Status.INVALID_ARGUMENT.withDescription(errors.toString());
        }

        IllegalArgumentException illegalArgumentException = (IllegalArgumentException) exception;

        return Status.INVALID_ARGUMENT.withDescription(illegalArgumentException.getMessage());
    }

    @GrpcExceptionHandler
    public Status handleNoSuchElementException(NoSuchElementException exception) {
        log.error(exception.getMessage(), exception);

        return Status.NOT_FOUND.withDescription(exception.getMessage());
    }

    @GrpcExceptionHandler
    public Status handleException(Exception exception) {
        log.error(exception.getMessage(), exception);

        return Status.INTERNAL.withDescription(exception.getMessage());
    }
}
