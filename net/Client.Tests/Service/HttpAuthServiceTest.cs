using System.Net;
using Client.Service;
using DTOs;
using JetBrains.Annotations;
using Moq;
using Moq.Protected;

namespace Client.Tests.Service;

[TestSubject(typeof(HttpAuthService))]
public class HttpAuthServiceTest
{
    [Fact]
    public async Task LoginAsync_Success()
    {
        // Arrange
        var testLoginDto = GetLoginDto();
        var mockDelegatingHandler = MockHelpers.CreateDelegatingHandlerMock(HttpStatusCode.OK, GetUserDto("JobProvider"));
        var mockHttpClient = MockHelpers.CreateHttpClientMock(mockDelegatingHandler);
        var httpAuthService = new HttpAuthService(mockHttpClient);
        
        // Act
        var userDto = await httpAuthService.LoginAsync(testLoginDto);

        // Assert
        Assert.NotNull(userDto);
        Assert.IsType<UserDto>(userDto);
        Assert.Equal(testLoginDto.Email, userDto.Email);
        Assert.Equal(testLoginDto.Password, userDto.Password);
        
        mockDelegatingHandler.Protected()
            .Verify<Task<HttpResponseMessage>>(
                "SendAsync",
                Times.Once(),
                ItExpr.IsAny<HttpRequestMessage>(),
                ItExpr.IsAny<CancellationToken>()
            );
    }

    [Fact]
    public async Task LoginAsync_Failure()
    {
        // Arrange
        var testLoginDto = GetLoginDto();
        var mockDelegatingHandler = MockHelpers.CreateDelegatingHandlerMock(HttpStatusCode.BadRequest, new {});
        var mockHttpClient = MockHelpers.CreateHttpClientMock(mockDelegatingHandler);
        var httpAuthService = new HttpAuthService(mockHttpClient);

        
        // Act & Assert
        await Assert.ThrowsAsync<HttpRequestException>(async () => await httpAuthService.LoginAsync(testLoginDto));

        mockDelegatingHandler.Protected()
            .Verify<Task<HttpResponseMessage>>(
                "SendAsync",
                Times.Once(),
                ItExpr.IsAny<HttpRequestMessage>(),
                ItExpr.IsAny<CancellationToken>()
            );
    }

    private static LoginDto GetLoginDto()
    {
        return new LoginDto()
        {
            Email = "test@test.com",
            Password = "test"
        };
    }

    private static UserDto GetUserDto(string role)
    {
        return new UserDto()
        {
            Id = 1,
            Email = "test@test.com",
            Password = "test",
            Role = role,
            FirstName = "test",
            LastName = "test",
            PhoneNumber = "test",
            Resume = "test",
            Name = "test",
            Description = "test"
        };
    }
}
