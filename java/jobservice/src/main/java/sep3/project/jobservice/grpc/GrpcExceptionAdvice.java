package sep3.project.jobservice.grpc;

import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@Slf4j
@GrpcAdvice
public class GrpcExceptionAdvice {
    @GrpcExceptionHandler
    public Status handleException(Exception exception) {
        log.error(exception.getMessage(), exception);

        return Status.INTERNAL.withDescription("An error occurred, try again later.").withCause(exception);
    }
}
