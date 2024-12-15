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
        var testJobDto = GetJobDto();
        var mockDelegatingHandler = MockDelegatingHandler(HttpStatusCode.OK, new PagedResult<IEnumerable<JobDto>>()
        {
            Data = new List<JobDto>()
            {
                testJobDto
            },
            NextPageToken = string.Empty,
            TotalSize = 1
        });
        var httpJobService = CreateMockedService(mockDelegatingHandler);
        
        // Act
        var apiResponse = await httpJobService.GetJobsAsync();

        // Assert
        Assert.NotNull(apiResponse);
        Assert.IsType<PagedResult<IEnumerable<JobDto>>>(apiResponse);
        var jobs = apiResponse.Data.ToList();
        Assert.NotNull(jobs);
        Assert.NotEmpty(jobs);
        Assert.Single(jobs);
        Assert.Equal(testJobDto.Title, jobs.First().Title);
        
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
        var testJobDto = GetJobDto();
        var mockDelegatingHandler = MockDelegatingHandler(HttpStatusCode.OK, testJobDto);
        var httpJobService = CreateMockedService(mockDelegatingHandler);
        
        //Act
        var jobDto = await httpJobService.GetJobAsync(1);
        
        //Assert
        Assert.NotNull(jobDto);
        Assert.IsType<JobDto>(jobDto);
        Assert.Equal(testJobDto.Title, jobDto.Title);
        
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
            Description = "test",
            Location = "test",
            Salary = 1000,
            Status = "Active",
            Title = "test",
            Type = "FullTime",
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