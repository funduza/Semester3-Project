package sep3.project.jobservice.grpc;

import io.github.perplexhub.rsql.RSQLJPASupport;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import sep3.project.jobservice.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import sep3.project.jobservice.repositories.JobApplicationRepository;
import sep3.project.jobservice.repositories.JobRepository;
import sep3.project.jobservice.repositories.JobSeekerRepository;

import java.util.List;

@Slf4j
@GrpcService
public class JobApplicationServiceImpl extends JobApplicationServiceGrpc.JobApplicationServiceImplBase {
    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final JobSeekerRepository jobSeekerRepository;

    public JobApplicationServiceImpl(
            JobApplicationRepository jobApplicationRepository,
            JobRepository jobRepository,
            JobSeekerRepository jobSeekerRepository
    ) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobRepository = jobRepository;
        this.jobSeekerRepository = jobSeekerRepository;
    }

    @Override
    public void createJobApplication(CreateJobApplicationRequest request, StreamObserver<JobApplicationProto> responseObserver) {
        log.info("CreateJobApplicationRequest: {}", request);

        JobApplicationProto jobApplicationProto = request.getJobApplication();

        Job job = jobRepository.findById(jobApplicationProto.getJobId()).orElseThrow();
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobApplicationProto.getJobSeekerId()).orElseThrow();

        JobApplication jobApplication = jobApplicationRepository.save(JobApplication.newBuilder()
            .setJob(job)
            .setJobSeeker(jobSeeker)
            .build()
        );

        JobApplicationProto response = JobApplicationProto.newBuilder()
            .setId(jobApplication.getId())
            .setStatus(jobApplication.getStatus().toString())
            .setApplicationDate(jobApplication.getApplicationDate().toString())
            .setJobId(jobApplication.getJob().getId())
            .setJobSeekerId(jobApplication.getJobSeeker().getId())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listJobApplications(ListJobApplicationsRequest request, StreamObserver<ListJobApplicationsResponse> responseObserver) {
        log.info("ListJobApplicationsRequest: {}", request);

        int pageSize = request.getPageSize() == 0 ? 12 : request.getPageSize() > 64 ? 64 : request.getPageSize();
        int pageToken;

        try {
            pageToken = Integer.parseInt(request.getPageToken());
        } catch (Exception e) {
            pageToken = 0;
        }

        Specification<JobApplication> specification = RSQLJPASupport.toSpecification(request.getFilter());
        Page<JobApplication> jobApplications = jobApplicationRepository.findAll(specification, PageRequest.of(pageToken, pageSize));

        List<JobApplicationProto> jobApplicationMessages = jobApplications.stream()
                .map(jobApplication -> JobApplicationProto.newBuilder()
                        .setId(jobApplication.getId())
                        .setStatus(jobApplication.getStatus().toString())
                        .setApplicationDate(jobApplication.getApplicationDate().toString())
                        .setJobId(jobApplication.getJob().getId())
                        .setJobSeekerId(jobApplication.getJobSeeker().getId())
                        .build())
                .toList();

        String nextPageToken = pageToken < jobApplications.getTotalPages() - 1 ? String.valueOf(pageToken + 1) : "";

        ListJobApplicationsResponse response = ListJobApplicationsResponse
                .newBuilder()
                .addAllJobApplications(jobApplicationMessages)
                .setNextPageToken(nextPageToken)
                .setTotalSize((int) jobApplications.getTotalElements())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getJobApplication(GetJobApplicationRequest request, StreamObserver<JobApplicationProto> responseObserver) {
        log.info("GetJobApplicationRequest: {}", request);

        JobApplication jobApplication = jobApplicationRepository.findById(request.getId()).orElseThrow();


        JobApplicationProto response = JobApplicationProto.newBuilder()
                .setId(jobApplication.getId())
                .setStatus(jobApplication.getStatus().toString())
                .setApplicationDate(jobApplication.getApplicationDate().toString())
                .setJobId(jobApplication.getJob().getId())
                .setJobSeekerId(jobApplication.getJobSeeker().getId())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateJobApplication(UpdateJobApplicationRequest request, StreamObserver<JobApplicationProto> responseObserver) {
        log.info("UpdateJobApplicationRequest: {}", request);

        JobApplicationProto jobApplicationProto = request.getJobApplication();

        JobApplication existingJobApplication = jobApplicationRepository.findById(jobApplicationProto.getId()).orElseThrow();

        // Only the status can be updated for now
        existingJobApplication.setStatus(JobApplication.Status.valueOf(jobApplicationProto.getStatus()));

        JobApplication jobApplication = jobApplicationRepository.save(existingJobApplication);

        JobApplicationProto response = JobApplicationProto.newBuilder()
            .setId(jobApplication.getId())
            .setStatus(jobApplication.getStatus().toString())
            .setApplicationDate(jobApplication.getApplicationDate().toString())
            .setJobId(jobApplication.getJob().getId())
            .setJobSeekerId(jobApplication.getJobSeeker().getId())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
