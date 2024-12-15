package sep3.project.jobservice.grpc;

import com.google.protobuf.Timestamp;
import io.github.perplexhub.rsql.RSQLJPASupport;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import sep3.project.jobservice.entities.Job;
import sep3.project.jobservice.entities.JobProvider;
import sep3.project.jobservice.repositories.JobProviderRepository;
import sep3.project.jobservice.repositories.JobRepository;

import java.time.Instant;
import java.util.List;

/**
 * A service for working with jobs.
 * This service can create, list and get jobs from the database.
 */
@Slf4j
@GrpcService
public class JobServiceImpl extends JobServiceGrpc.JobServiceImplBase {
    private final JobRepository jobRepository;
    private final JobProviderRepository jobProviderRepository;

    public JobServiceImpl(JobRepository jobRepository, JobProviderRepository jobProviderRepository) {
        this.jobRepository = jobRepository;
        this.jobProviderRepository = jobProviderRepository;
    }

    /**
     * A method used to create a new job.
     * @param request The request containing details about the job.
     * @param responseObserver The stream observer for sending the response.
     * @throws java.util.NoSuchElementException If the job provider cannot be found by ID.
     */
    @Override
    public void createJob(CreateJobRequest request, StreamObserver<JobProto> responseObserver) {
        log.info("CreateJobRequest: {}", request);

        JobProto jobProto = request.getJob();
        JobProvider jobProvider = jobProviderRepository.findById(jobProto.getJobProvider().getId()).orElseThrow();

        Job job = Job.newBuilder()
                .setTitle(jobProto.getTitle())
                .setDescription(jobProto.getDescription())
                .setDeadline(Instant.ofEpochSecond(jobProto.getDeadline().getSeconds(), jobProto.getDeadline().getNanos()))
                .setLocation(jobProto.getLocation())
                .setSalary(jobProto.getSalary())
                .setType(Job.Type.valueOf(jobProto.getType()))
                .setJobProvider(jobProvider)
                .build();

        Job savedJob = jobRepository.save(job);

        JobProto responseJobProto = JobProto.newBuilder()
                .setId(savedJob.getId())
                .setTitle(savedJob.getTitle())
                .setDescription(savedJob.getDescription())
                .setPostingDate(Timestamp.newBuilder()
                        .setSeconds(savedJob.getPostingDate().getEpochSecond())
                        .setNanos(savedJob.getPostingDate().getNano())
                        .build())
                .setDeadline(Timestamp.newBuilder()
                        .setSeconds(savedJob.getDeadline().getEpochSecond())
                        .setNanos(savedJob.getDeadline().getNano())
                        .build())
                .setLocation(savedJob.getLocation())
                .setSalary(savedJob.getSalary())
                .setType(savedJob.getType().toString())
                .setStatus(savedJob.getStatus().toString())
                .setJobProvider(UserProto.newBuilder()
                        .setId(job.getJobProvider().getId())
                        .setEmail(savedJob.getJobProvider().getEmail())
                        .setRole(savedJob.getJobProvider().getRole().toString())
                        .setJobProvider(JobProviderProto.newBuilder()
                                .setName(savedJob.getJobProvider().getName())
                                .setDescription(savedJob.getJobProvider().getDescription())
                                .setPhoneNumber(savedJob.getJobProvider().getPhoneNumber())
                                .build())
                        .build())
                .build();

        responseObserver.onNext(responseJobProto);
        responseObserver.onCompleted();
    }

    /**
     * A method used to get a list of jobs and optionally filter it.
     * @param request The request containing details about filtering, page size and the page token.
     * @param responseObserver The stream observer for sending the response.
     */
    @Override
    public void listJobs(ListJobsRequest request, StreamObserver<ListJobsResponse> responseObserver) {
        log.info("ListJobsRequest: {}", request);

        int pageSize = request.getPageSize() == 0 ? 12 : request.getPageSize() > 64 ? 64 : request.getPageSize();
        int pageToken;

        try {
            pageToken = Integer.parseInt(request.getPageToken());
        } catch (Exception e) {
            pageToken = 0;
        }

        Specification<Job> specification = RSQLJPASupport.toSpecification(request.getFilter());
        Page<Job> jobs = jobRepository.findAll(specification, PageRequest.of(pageToken, pageSize));

        List<JobProto> jobProtos = jobs.stream()
                .map(job -> JobProto.newBuilder()
                        .setId(job.getId())
                        .setTitle(job.getTitle())
                        .setDescription(job.getDescription())
                        .setPostingDate(Timestamp.newBuilder()
                                .setSeconds(job.getPostingDate().getEpochSecond())
                                .setNanos(job.getPostingDate().getNano())
                                .build())
                        .setDeadline(Timestamp.newBuilder()
                                .setSeconds(job.getDeadline().getEpochSecond())
                                .setNanos(job.getDeadline().getNano())
                                .build())
                        .setLocation(job.getLocation())
                        .setSalary(job.getSalary())
                        .setType(job.getType().toString())
                        .setStatus(job.getStatus().toString())
                        .setJobProvider(UserProto.newBuilder()
                                .setId(job.getJobProvider().getId())
                                .setEmail(job.getJobProvider().getEmail())
                                .setRole(job.getJobProvider().getRole().toString())
                                .setJobProvider(JobProviderProto.newBuilder()
                                        .setName(job.getJobProvider().getName())
                                        .setDescription(job.getJobProvider().getDescription())
                                        .setPhoneNumber(job.getJobProvider().getPhoneNumber())
                                        .build())
                                .build())
                        .build()
                )
                .toList();

        String nextPageToken = pageToken < jobs.getTotalPages() - 1 ? String.valueOf(pageToken + 1) : "";

        ListJobsResponse response = ListJobsResponse
                .newBuilder()
                .addAllJobs(jobProtos)
                .setNextPageToken(nextPageToken)
                .setTotalSize(jobs.getTotalElements())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * A method used to get a job by its ID.
     * @param request The request containing the job ID to get.
     * @param responseObserver The stream observer for sending the response.
     */
    @Override
    public void getJob(GetJobRequest request, StreamObserver<JobProto> responseObserver) {
        log.info("GetJobRequest: {}", request);

        Job job = jobRepository.findById(request.getId()).orElseThrow();

        JobProto response = JobProto.newBuilder()
                .setId(job.getId())
                .setTitle(job.getTitle())
                .setDescription(job.getDescription())
                .setPostingDate(Timestamp.newBuilder()
                        .setSeconds(job.getPostingDate().getEpochSecond())
                        .setNanos(job.getPostingDate().getNano())
                        .build())
                .setDeadline(Timestamp.newBuilder()
                        .setSeconds(job.getDeadline().getEpochSecond())
                        .setNanos(job.getDeadline().getNano())
                        .build())
                .setLocation(job.getLocation())
                .setSalary(job.getSalary())
                .setType(job.getType().toString())
                .setStatus(job.getStatus().toString())
                .setJobProvider(UserProto.newBuilder()
                        .setId(job.getJobProvider().getId())
                        .setEmail(job.getJobProvider().getEmail())
                        .setRole(job.getJobProvider().getRole().toString())
                        .setJobProvider(JobProviderProto.newBuilder()
                                .setName(job.getJobProvider().getName())
                                .setDescription(job.getJobProvider().getDescription())
                                .setPhoneNumber(job.getJobProvider().getPhoneNumber())
                                .build())
                        .build())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * A method used to update a job.
     * @param request The request containing details about the job.
     * @param responseObserver The stream observer for sending the response.
     * @throws java.util.NoSuchElementException If the job cannot be found by ID.
     */
    @Override
    public void updateJob(UpdateJobRequest request, StreamObserver<JobProto> responseObserver) {
        log.info("UpdateJobRequest: {}", request);

        Job existingJob = jobRepository.findById(request.getId()).orElseThrow();

        // Update the job status
        existingJob.setStatus(Job.Status.valueOf(request.getStatus()));

        Job job = jobRepository.save(existingJob);

        JobProto response = JobProto.newBuilder()
                .setId(job.getId())
                .setTitle(job.getTitle())
                .setDescription(job.getDescription())
                .setPostingDate(Timestamp.newBuilder()
                        .setSeconds(job.getPostingDate().getEpochSecond())
                        .setNanos(job.getPostingDate().getNano())
                        .build())
                .setDeadline(Timestamp.newBuilder()
                        .setSeconds(job.getDeadline().getEpochSecond())
                        .setNanos(job.getDeadline().getNano())
                        .build())
                .setLocation(job.getLocation())
                .setSalary(job.getSalary())
                .setType(job.getType().toString())
                .setStatus(job.getStatus().toString())
                .setJobProvider(UserProto.newBuilder()
                        .setId(job.getJobProvider().getId())
                        .setEmail(job.getJobProvider().getEmail())
                        .setRole(job.getJobProvider().getRole().toString())
                        .setJobProvider(JobProviderProto.newBuilder()
                                .setName(job.getJobProvider().getName())
                                .setDescription(job.getJobProvider().getDescription())
                                .setPhoneNumber(job.getJobProvider().getPhoneNumber())
                                .build())
                        .build())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
