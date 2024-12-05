package sep3.project.jobservice.grpc;

import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

import java.util.NoSuchElementException;

@Slf4j
@GrpcAdvice
public class GrpcExceptionAdvice {
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
