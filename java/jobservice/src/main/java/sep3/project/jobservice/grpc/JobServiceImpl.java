package sep3.project.jobservice.grpc;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import sep3.project.jobservice.entities.Job;
import sep3.project.jobservice.repositories.JobRepository;

import java.util.List;

@Slf4j
@GrpcService
public class JobServiceImpl extends JobServiceGrpc.JobServiceImplBase {
    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void listJobs(ListJobsRequest request, StreamObserver<ListJobsResponse> responseObserver) {
        log.info("GetAllJobsRequest: {}", request);

        int pageSize = request.getPageSize() == 0 ? 12 : request.getPageSize() > 64 ? 64 : request.getPageSize();
        Page<Job> jobs;

        if(request.getFilter().isEmpty()) {
            jobs = jobRepository.findAll(PageRequest.of(request.getPageToken(), pageSize));
        } else {
            jobs = jobRepository.findByTitleContainsIgnoreCaseOrDescriptionContainsIgnoreCase(
                    request.getFilter(),
                    request.getFilter(),
                    PageRequest.of(request.getPageToken(), pageSize));
        }

        List<JobProto> jobMessages = jobs.stream()
                .map(job -> JobProto.newBuilder()
                        .setId(job.getId())
                        .setTitle(job.getTitle())
                        .setDescription(job.getDescription())
                        .setPostingDate(job.getPostingDate().toString())
                        .setDeadline(job.getDeadline().toString())
                        .setLocation(job.getLocation())
                        .setType(job.getType().toString())
                        .setSalary(job.getSalary())
                        .setStatus(job.getStatus().toString())
                        .build())
                .toList();

        ListJobsResponse.Builder responseBuilder = ListJobsResponse
                .newBuilder()
                .addAllJobs(jobMessages);

        if (request.getPageToken() < jobs.getTotalPages() - 1) {
            responseBuilder.setNextPageToken(request.getPageToken() + 1).build();
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
