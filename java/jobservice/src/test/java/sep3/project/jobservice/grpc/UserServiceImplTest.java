package sep3.project.jobservice.grpc;

import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import sep3.project.jobservice.entities.JobProvider;
import sep3.project.jobservice.entities.JobSeeker;
import sep3.project.jobservice.entities.User;
import sep3.project.jobservice.repositories.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userServiceImpl;

  @Test
  public void testUserServiceImplInitialization() {
    assertNotNull(userServiceImpl);
    assertNotNull(userRepository);
  }

  @ParameterizedTest
  @EnumSource(User.Role.class)
  public void TestGetUser(User.Role role) throws Exception
  {
    // Arrange
    User testUser = getUser(role);
    when(userRepository.findOne(any(Specification.class))).thenReturn(Optional.of(testUser));

    StreamRecorder<UserProto> response = StreamRecorder.create();

    // Act
    userServiceImpl.getUser(GetUserRequest.newBuilder()
        .setId(testUser.getId())
        .setEmail(testUser.getEmail()).build(),
        response
    );

    // Assert
    assertNull(response.getError());
    assertEquals(1, response.getValues().size());
    assertEquals(testUser.getId(), response.firstValue().get().getId());
    assertEquals(testUser.getEmail(), response.firstValue().get().getEmail());

    when(userRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> userServiceImpl.getUser(GetUserRequest.newBuilder().setId(2L).build(), response));

    verify(userRepository, times(2)).findOne(any(Specification.class));
  }

  private User getUser(User.Role role)
  {
    return switch (role) {
      case JobSeeker -> JobSeeker.newBuilder()
          .setId(1L)
          .setPhoneNumber("1111")
          .setPassword("1111")
          .setFirstName("test")
          .setLastName("test")
          .setEmail("test@gmail.com")
          .setResume("test")
          .build();
      case JobProvider -> JobProvider.newBuilder()
          .setId(1L)
          .setPhoneNumber("1111")
          .setPassword("1111")
          .setEmail("test@gmail.com")
          .setName("test")
          .setDescription("test")
          .build();
    };
  }
}