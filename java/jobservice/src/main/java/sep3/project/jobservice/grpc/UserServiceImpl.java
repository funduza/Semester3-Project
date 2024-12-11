package sep3.project.jobservice.grpc;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import sep3.project.jobservice.entities.JobProvider;
import sep3.project.jobservice.entities.JobSeeker;
import sep3.project.jobservice.entities.User;
import sep3.project.jobservice.repositories.UserRepository;

import java.util.List;

// TODO: refactor duplicated code, maybe use an object mapper.

@Slf4j
@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<ListUsersResponse> responseObserver) {
        log.info("ListUsersRequest: {}", request);

        int pageSize = request.getPageSize() == 0 ? 12 : request.getPageSize();
        int pageToken;

        try {
            pageToken = Integer.parseInt(request.getPageToken());
        } catch (Exception e) {
            pageToken = 0;
        }

        Page<User> users = userRepository.findAll(PageRequest.of(pageToken, pageSize, Sort.by(Sort.Direction.ASC, "id")));

        List<UserProto> userProtos = users.stream()
                .map(user -> {
                    UserProto.Builder builder = UserProto.newBuilder()
                            .setId(user.getId())
                            .setEmail(user.getEmail())
                            .setPassword(user.getPassword())
                            .setRole(user.getRole().toString());

                    switch (user.getRole()) {
                        case JobProvider -> {
                            JobProvider jobProvider = (JobProvider) user;

                            builder.setJobProvider(JobProviderProto.newBuilder()
                                    .setName(jobProvider.getName())
                                    .setDescription(jobProvider.getDescription())
                                    .setPhoneNumber(jobProvider.getPhoneNumber())
                                    .build());
                        }
                        case JobSeeker -> {
                            JobSeeker jobSeeker = (JobSeeker) user;

                            builder.setJobSeeker(JobSeekerProto.newBuilder()
                                    .setFirstName(jobSeeker.getFirstName())
                                    .setLastName(jobSeeker.getLastName())
                                    .setPhoneNumber(jobSeeker.getPhoneNumber())
                                    .setResume(jobSeeker.getResume())
                                    .build());
                        }
                    }

                    return builder.build();
                })
                .toList();

        String nextPageToken = pageToken < users.getTotalPages() - 1 ? String.valueOf(pageToken + 1) : "";

        ListUsersResponse response = ListUsersResponse.newBuilder()
                .addAllUsers(userProtos)
                .setNextPageToken(nextPageToken)
                .setTotalSize((int) users.getTotalElements())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<UserProto> responseObserver) {
        log.info("GetUserRequest: {}", request);

        Specification<User> userByIdOrEmail = (root, query, criteriaBuilder) -> {
            if (!request.getEmail().isEmpty()) {
                return criteriaBuilder.equal(root.get("email"), request.getEmail());
            } else {
                return criteriaBuilder.equal(root.get("id"), request.getId());
            }
        };

        User user = userRepository.findOne(userByIdOrEmail).orElseThrow();

        UserProto.Builder builder = UserProto.newBuilder()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setRole(user.getRole().toString());

        switch (user.getRole()) {
            case JobProvider -> {
                JobProvider jobProvider = (JobProvider) user;
                JobProviderProto jobProviderProto = JobProviderProto.newBuilder()
                        .setName(jobProvider.getName())
                        .setDescription(jobProvider.getDescription())
                        .setPhoneNumber(jobProvider.getPhoneNumber())
                        .build();
                builder.setJobProvider(jobProviderProto);
            }
            case JobSeeker -> {
                JobSeeker jobSeeker = (JobSeeker) user;
                JobSeekerProto jobSeekerProto = JobSeekerProto.newBuilder()
                        .setFirstName(jobSeeker.getFirstName())
                        .setLastName(jobSeeker.getLastName())
                        .setPhoneNumber(jobSeeker.getPhoneNumber())
                        .setResume(jobSeeker.getResume())
                        .build();
                builder.setJobSeeker(jobSeekerProto);
            }
        }

        UserProto response = builder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<UserProto> responseObserver) {
        log.info("CreateUserRequest: {}", request);

        UserProto userProto = request.getUser();
        UserProto response = null;

        switch (User.Role.valueOf(userProto.getRole())) {
            case JobProvider -> {
                JobProvider jobProvider = JobProvider.newBuilder()
                        .setEmail(userProto.getEmail())
                        .setPassword(userProto.getPassword())
                        .setName(userProto.getJobProvider().getName())
                        .setDescription(userProto.getJobProvider().getDescription())
                        .setPhoneNumber(userProto.getJobProvider().getPhoneNumber())
                        .build();

                JobProvider user = userRepository.save(jobProvider);

                JobProviderProto jobProviderProto = JobProviderProto.newBuilder()
                        .setName(user.getName())
                        .setDescription(user.getDescription())
                        .setPhoneNumber(user.getPhoneNumber())
                        .build();

                response = UserProto.newBuilder()
                        .setId(user.getId())
                        .setEmail(user.getEmail())
                        .setPassword(user.getPassword())
                        .setRole(user.getRole().toString())
                        .setJobProvider(jobProviderProto)
                        .build();
            }
            case JobSeeker -> {
                JobSeeker jobSeeker = JobSeeker.newBuilder()
                        .setEmail(userProto.getEmail())
                        .setPassword(userProto.getPassword())
                        .setFirstName(userProto.getJobSeeker().getFirstName())
                        .setLastName(userProto.getJobSeeker().getLastName())
                        .setPhoneNumber(userProto.getJobSeeker().getPhoneNumber())
                        .setResume(userProto.getJobSeeker().getResume())
                        .build();

                JobSeeker user = userRepository.save(jobSeeker);

                JobSeekerProto jobSeekerProto = JobSeekerProto.newBuilder()
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setPhoneNumber(user.getPhoneNumber())
                        .setResume(user.getResume())
                        .build();

                response = UserProto.newBuilder()
                        .setId(user.getId())
                        .setEmail(user.getEmail())
                        .setPassword(user.getPassword())
                        .setRole(user.getRole().toString())
                        .setJobSeeker(jobSeekerProto)
                        .build();
            }
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserProto> responseObserver) {
        log.info("UpdateUserRequest: {}", request);

        UserProto userProto = request.getUser();
        UserProto response = null;
        User existingUser = userRepository.findById(userProto.getId()).orElseThrow();

        switch (User.Role.valueOf(userProto.getRole())) {
            case JobProvider -> {
                JobProvider jobProvider = (JobProvider) existingUser;
                jobProvider.setEmail(userProto.getEmail());
                jobProvider.setPassword(userProto.getPassword());
                jobProvider.setName(userProto.getJobProvider().getName());
                jobProvider.setDescription(userProto.getJobProvider().getDescription());
                jobProvider.setPhoneNumber(userProto.getJobProvider().getPhoneNumber());

                JobProvider user = userRepository.save(jobProvider);

                JobProviderProto jobProviderProto = JobProviderProto.newBuilder()
                        .setName(user.getName())
                        .setDescription(user.getDescription())
                        .setPhoneNumber(user.getPhoneNumber())
                        .build();

                response = UserProto.newBuilder()
                        .setId(user.getId())
                        .setEmail(user.getEmail())
                        .setPassword(user.getPassword())
                        .setRole(user.getRole().toString())
                        .setJobProvider(jobProviderProto)
                        .build();
            }
            case JobSeeker -> {
                JobSeeker jobSeeker = (JobSeeker) existingUser;
                jobSeeker.setEmail(userProto.getEmail());
                jobSeeker.setPassword(userProto.getPassword());
                jobSeeker.setFirstName(userProto.getJobSeeker().getFirstName());
                jobSeeker.setLastName(userProto.getJobSeeker().getLastName());
                jobSeeker.setPhoneNumber(userProto.getJobSeeker().getPhoneNumber());
                jobSeeker.setResume(userProto.getJobSeeker().getResume());

                JobSeeker user = userRepository.save(jobSeeker);

                JobSeekerProto jobSeekerProto = JobSeekerProto.newBuilder()
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setPhoneNumber(user.getPhoneNumber())
                        .setResume(user.getResume())
                        .build();

                response = UserProto.newBuilder()
                        .setId(user.getId())
                        .setEmail(user.getEmail())
                        .setPassword(user.getPassword())
                        .setRole(user.getRole().toString())
                        .setJobSeeker(jobSeekerProto)
                        .build();
            }
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<Empty> responseObserver) {
        log.info("DeleteUserRequest: {}", request);

        userRepository.findById(request.getId()).orElseThrow();
        userRepository.deleteById(request.getId());

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
