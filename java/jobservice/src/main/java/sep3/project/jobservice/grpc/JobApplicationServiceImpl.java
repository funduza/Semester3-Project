package sep3.project.jobservice.grpc;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import sep3.project.jobservice.entities.*;
import sep3.project.jobservice.repositories.JobApplicationRepository;
import sep3.project.jobservice.repositories.JobRepository;
import sep3.project.jobservice.repositories.JobSeekerRepository;

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
