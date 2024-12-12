using System.Net;
using System.Net.Http.Json;
using Client.Service;
using DTOs;
using JetBrains.Annotations;
using Moq;
using Moq.Protected;

namespace Client.Tests.Service;

[TestSubject(typeof(HttpJobService))]
public class HttpJobServiceTest
{
    [Fact]
    public async Task GetJobsAsync_Success()
    {
        // Arrange
        var mockDelegatingHandler = MockDelegatingHandler(HttpStatusCode.OK, new ApiResponse<IEnumerable<JobDto>>()
        {
            Data = new List<JobDto>()
            {
                GetJobDto()
            },
            NextPageToken = string.Empty,
            TotalSize = 1
        });
        var httpJobService = CreateMockedService(mockDelegatingHandler);
        
        // Act
        var apiResponse = await httpJobService.GetJobsAsync();

        // Assert
        Assert.NotNull(apiResponse);
        Assert.IsType<ApiResponse<IEnumerable<JobDto>>>(apiResponse);
        var jobs = apiResponse.Data.ToList();
        Assert.NotNull(jobs);
        Assert.NotEmpty(jobs);
        Assert.Single(jobs);
        Assert.Equal("Job 1", jobs.First().Title);
        
        mockDelegatingHandler.Protected()
            .Verify<Task<HttpResponseMessage>>(
                "SendAsync",
                Times.Once(),
                ItExpr.IsAny<HttpRequestMessage>(),
                ItExpr.IsAny<CancellationToken>()
            );
    }

    [Fact]
    public async Task GetJobsAsync_Exception()
    {
        // Arrange
        var mockDelegatingHandler = MockDelegatingHandler(HttpStatusCode.BadRequest, new {});
        var httpJobService = CreateMockedService(mockDelegatingHandler);

        // Assert
        await Assert.ThrowsAsync<HttpRequestException>(async () => await httpJobService.GetJobsAsync());

        mockDelegatingHandler.Protected()
            .Verify<Task<HttpResponseMessage>>(
                "SendAsync",
                Times.Once(),
                ItExpr.IsAny<HttpRequestMessage>(),
                ItExpr.IsAny<CancellationToken>()
            );
    }

    [Fact]
    public async Task GetJobAsync_Success()
    {
        // Arrange
        var mockDelegatingHandler = MockDelegatingHandler(HttpStatusCode.OK, new ApiResponse<JobDto>()
        {
            Data = GetJobDto(),
            NextPageToken = string.Empty,
            TotalSize = 1
        });
        var httpJobService = CreateMockedService(mockDelegatingHandler);
        
        //Act
        var apiResponse = await httpJobService.GetJobAsync(1);
        
        //Assert
        Assert.NotNull(apiResponse);
        Assert.IsType<ApiResponse<JobDto>>(apiResponse);
        var job = apiResponse.Data;
        Assert.Equal("Job 1", job.Title);
        
        mockDelegatingHandler.Protected()
            .Verify<Task<HttpResponseMessage>>(
                "SendAsync",
                Times.Once(),
                ItExpr.IsAny<HttpRequestMessage>(),
                ItExpr.IsAny<CancellationToken>()
            );
    }

    [Fact]

    public async Task GetJobAsync_Exception()
    {
        // Arrange
        var mockDelegatingHandler = MockDelegatingHandler(HttpStatusCode.BadRequest, new {});
        var httpJobService = CreateMockedService(mockDelegatingHandler);
        
        // Assert
        await Assert.ThrowsAsync<HttpRequestException>(async () => await httpJobService.GetJobAsync(1));
        
        mockDelegatingHandler.Protected()
            .Verify<Task<HttpResponseMessage>>(
                "SendAsync",
                Times.Once(),
                ItExpr.IsAny<HttpRequestMessage>(),
                ItExpr.IsAny<CancellationToken>()
            );
    }
    
    private static JobDto GetJobDto()
    {
        return new JobDto()
        {
            Id = 1,
            Deadline = DateTime.Today,
            Description = "Job 1",
            Location = "Test Location",
            Salary = 1000,
            Status = "Active",
            Title = "Job 1",
            Type = "s",
            PostingDate = DateTime.Today,
            JobProvider = new UserDto()
            {
                Email = "test@test.com",
                Name = "Test"
            }
        };
    }

    private static Mock<DelegatingHandler> MockDelegatingHandler<T>(HttpStatusCode statusCode, T content)
    {
        var mockDelegatingHandler = new Mock<DelegatingHandler>();
        mockDelegatingHandler.Protected()
            .Setup<Task<HttpResponseMessage>>("SendAsync", ItExpr.IsAny<HttpRequestMessage>(),
                ItExpr.IsAny<CancellationToken>())
            .ReturnsAsync(new HttpResponseMessage
            {
                StatusCode = statusCode,
                Content = JsonContent.Create(content)
            });
        return mockDelegatingHandler;
    }
    
    private static HttpJobService CreateMockedService(Mock<DelegatingHandler> mockDelegatingHandler)
    {
        var mockHttpClient = new HttpClient(mockDelegatingHandler.Object)
        {
            BaseAddress = new Uri("http://localhost")
        };
        return new HttpJobService(mockHttpClient);
    }
}