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
        var mockDelegatingHandler = MockHelpers.CreateDelegatingHandlerMock(HttpStatusCode.OK, new PagedResult<IEnumerable<JobDto>>()
        {
            Data = new List<JobDto>()
            {
                testJobDto
            },
            NextPageToken = string.Empty,
            TotalSize = 1
        });
        var mockHttpClient = MockHelpers.CreateHttpClientMock(mockDelegatingHandler);
        var httpJobService = new HttpJobService(mockHttpClient);
        
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
        var mockDelegatingHandler = MockHelpers.CreateDelegatingHandlerMock(HttpStatusCode.BadRequest, new {});
        var mockHttpClient = MockHelpers.CreateHttpClientMock(mockDelegatingHandler);
        var httpJobService = new HttpJobService(mockHttpClient);
        
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
        var mockDelegatingHandler = MockHelpers.CreateDelegatingHandlerMock(HttpStatusCode.OK, testJobDto);
        var mockHttpClient = MockHelpers.CreateHttpClientMock(mockDelegatingHandler);
        var httpJobService = new HttpJobService(mockHttpClient);
        
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
        var mockDelegatingHandler = MockHelpers.CreateDelegatingHandlerMock(HttpStatusCode.BadRequest, new {});
        var mockHttpClient = MockHelpers.CreateHttpClientMock(mockDelegatingHandler);
        var httpJobService = new HttpJobService(mockHttpClient);
        
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
}