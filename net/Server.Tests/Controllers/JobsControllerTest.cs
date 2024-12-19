using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using DTOs;
using Google.Protobuf.WellKnownTypes;
using JetBrains.Annotations;
using Microsoft.AspNetCore.Mvc;
using Moq;
using Server.Controllers;
using Server.Grpc;
using Xunit;

namespace Server.Tests.Controllers;

[TestSubject(typeof(JobsController))]
public class JobsControllerTest
{
    [Fact]
    public async Task CreateJobAsync_ShouldCreateJob()
    {
        // Arrange
        var testJobDto = GetJobDto();
        var mockCall = MockHelpers.CreateAsyncUnaryCall(GetJobProto());
        var mockJobServiceClient = new Mock<JobService.JobServiceClient>();
        mockJobServiceClient
            .Setup(mock => mock.CreateJobAsync(It.IsAny<CreateJobRequest>(), null, null, CancellationToken.None))
            .Returns(mockCall);
        var jobsController = new JobsController(mockJobServiceClient.Object);
        
        // Act
        var response = await jobsController.CreateJobAsync(new CreateJobDto()
        {
            Title = testJobDto.Title!,
            Description = testJobDto.Description!,
            Deadline = testJobDto.Deadline,
            Location = testJobDto.Location!,
            Salary = testJobDto.Salary,
            Type = testJobDto.Type!,
            JobProviderId = testJobDto.JobProvider!.Id
        });
        
        // Assert
        Assert.NotNull(response);
        Assert.NotNull(response.Result);
        var createdAtActionResult = Assert.IsType<CreatedAtActionResult>(response.Result);
        Assert.Equal(201, createdAtActionResult.StatusCode);
        var createdJobDto = Assert.IsType<JobDto>(createdAtActionResult.Value);
        Assert.Equal(testJobDto.Title, createdJobDto.Title);
        
        mockJobServiceClient
            .Verify(mock => mock.CreateJobAsync(It.IsAny<CreateJobRequest>(), null, null, CancellationToken.None), Times.Once);
    }

    [Fact]
    public async Task GetJobsAsync_ShouldReturnAllJobs()
    {
        // Arrange
        var testJobDto = GetJobDto();
        var mockCall = MockHelpers.CreateAsyncUnaryCall(new ListJobsResponse()
        {
            Jobs = { GetJobProto() },
            NextPageToken = "",
            TotalSize = 1
        });
        var mockJobServiceClient = new Mock<JobService.JobServiceClient>();
        mockJobServiceClient
            .Setup(mock => mock.ListJobsAsync(It.IsAny<ListJobsRequest>(), null, null, CancellationToken.None))
            .Returns(mockCall);
        var jobsController = new JobsController(mockJobServiceClient.Object);
        
        // Act
        var response = await jobsController.GetJobsAsync();
        
        // Assert
        Assert.NotNull(response);
        Assert.NotNull(response.Result);
        var okObjectResult = Assert.IsType<OkObjectResult>(response.Result);
        Assert.Equal(200, okObjectResult.StatusCode);
        var jobDtosPages = Assert.IsType<PagedResult<IEnumerable<JobDto>>>(okObjectResult.Value);
        Assert.NotNull(jobDtosPages);
        var jobDtos = Assert.IsAssignableFrom<IEnumerable<JobDto>>(jobDtosPages.Data);
        Assert.Single(jobDtosPages.Data);
        var jobDto = Assert.IsType<JobDto>(jobDtos.First());
        Assert.Equal(testJobDto.Title, jobDto.Title);
        
        mockJobServiceClient
            .Verify(mock => mock.ListJobsAsync(It.IsAny<ListJobsRequest>(), null, null, CancellationToken.None), Times.Once);
    }

    [Fact]
    public async Task GetJobAsync_ShouldReturnJob()
    {
        // Arrange
        var testJobDto = GetJobDto();
        var mockCall = MockHelpers.CreateAsyncUnaryCall(GetJobProto());
        var mockJobServiceClient = new Mock<JobService.JobServiceClient>();
        mockJobServiceClient
            .Setup(mock => mock.GetJobAsync(It.IsAny<GetJobRequest>(), null, null, CancellationToken.None))
            .Returns(mockCall);
        var jobsController = new JobsController(mockJobServiceClient.Object);
        
        // Act
        var response = await jobsController.GetJobAsync(testJobDto.Id);
        
        // Assert
        Assert.NotNull(response);
        Assert.NotNull(response.Result);
        var okObjectResult = Assert.IsType<OkObjectResult>(response.Result);
        Assert.Equal(200, okObjectResult.StatusCode);
        var jobDto = Assert.IsType<JobDto>(okObjectResult.Value);
        Assert.Equal(testJobDto.Title, jobDto.Title);
        
        mockJobServiceClient
            .Verify(mock => mock.GetJobAsync(It.IsAny<GetJobRequest>(), null, null, CancellationToken.None), Times.Once);
    }

    [Fact]
    public async Task UpdateJobAsync_ShouldUpdateJob()
    {
        // Arrange
        var testJobDto = GetJobDto("Closed");
        var mockCall = MockHelpers.CreateAsyncUnaryCall(GetJobProto("Closed"));
        var mockJobServiceClient = new Mock<JobService.JobServiceClient>();
        mockJobServiceClient
            .Setup(mock => mock.UpdateJobAsync(It.IsAny<UpdateJobRequest>(), null, null, CancellationToken.None))
            .Returns(mockCall);
        var jobsController = new JobsController(mockJobServiceClient.Object);
        
        // Act
        var response = await jobsController.UpdateJobAsync(testJobDto.Id, new UpdateJobDto()
        {
            Status = testJobDto.Status!,
        });
        
        // Assert
        Assert.NotNull(response);
        Assert.NotNull(response.Result);
        var okObjectResult = Assert.IsType<OkObjectResult>(response.Result);
        Assert.Equal(200, okObjectResult.StatusCode);
        var jobDto = Assert.IsType<JobDto>(okObjectResult.Value);
        Assert.Equal(testJobDto.Title, jobDto.Title);
        Assert.Equal(testJobDto.Status, jobDto.Status);
        
        mockJobServiceClient
            .Verify(mock => mock.UpdateJobAsync(It.IsAny<UpdateJobRequest>(), null, null, CancellationToken.None), Times.Once);
    }

    private static JobDto GetJobDto(string status = "Active")
    {
        return new JobDto()
        {
            Id = 1,
            Title = "test",
            Description = "test",
            PostingDate = DateTimeOffset.Now,
            Deadline = DateTimeOffset.Now.AddDays(7),
            Location = "test",
            Type = "FullTime",
            Salary = 1000,
            Status = status,
            JobProvider = new UserDto()
            {
                Id = 1,
                Email = "test@test.com",
                Password = "test",
                Role = "JobProvider",
                Name = "test",
                PhoneNumber = "test",
                Description = "test"
            }
        };
    }

    private static JobProto GetJobProto(string status = "Active")
    {
        var jobDto = GetJobDto(status);
        return new JobProto()
        {
            Id = jobDto.Id,
            Title = jobDto.Title,
            Description = jobDto.Description,
            PostingDate = jobDto.PostingDate.ToTimestamp(),
            Deadline = jobDto.Deadline.ToTimestamp(),
            Location = jobDto.Location,
            Salary = jobDto.Salary,
            Type = jobDto.Type,
            Status = jobDto.Status,
            JobProvider = new UserProto()
            {
                Id = jobDto.JobProvider!.Id,
                Email = jobDto.JobProvider!.Email,
                Password = jobDto.JobProvider!.Password,
                Role = jobDto.JobProvider!.Role,
                JobProvider = new JobProviderProto()
                {
                    Name = jobDto.JobProvider!.Name,
                    Description = jobDto.JobProvider!.Description,
                    PhoneNumber = jobDto.JobProvider!.PhoneNumber,
                }
            }
        };
    }
}
