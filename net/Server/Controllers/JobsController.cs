using DTOs;
using Microsoft.AspNetCore.Mvc;
using Server.Grpc;

namespace Server.Controllers;

[ApiController]
[Route("jobs")]
public class JobsController : ControllerBase
{
    private readonly JobService.JobServiceClient _jobServiceClient;

    public JobsController(JobService.JobServiceClient jobServiceClient)
    {
        _jobServiceClient = jobServiceClient;
    }

    [HttpGet]
    public async Task<ActionResult<ApiResponse<IEnumerable<JobDto>>>> GetJobsAsync(
        [FromQuery] string pageToken = "",
        [FromQuery] int pageSize = 12,
        [FromQuery] string filter = ""
    )
    {
        var listJobsResponse = await _jobServiceClient.ListJobsAsync(new ListJobsRequest()
        {
            Filter = filter,
            PageSize = pageSize,
            PageToken = pageToken
        });

        var jobsDto = listJobsResponse.Jobs.Select(job => new JobDto()
        {
            Id = job.Id,
            Title = job.Title,
            Description = job.Description,
            PostingDate = DateTime.Parse(job.PostingDate),
            Deadline = DateTime.Parse(job.Deadline),
            Location = job.Location,
            Type = job.Type,
            Salary = job.Salary,
            Status = job.Status,
            JobProvider = new UserDto()
            {
                Id = job.JobProvider.Id,
                Email = job.JobProvider.Email,
                Name = job.JobProvider.JobProvider.Name,
                Description = job.JobProvider.JobProvider.Description,
                PhoneNumber = job.JobProvider.JobProvider.PhoneNumber,
            }
        });

        return Ok(new ApiResponse<IEnumerable<JobDto>>()
        {
            Data = jobsDto,
            NextPageToken = listJobsResponse.NextPageToken,
            TotalSize = listJobsResponse.TotalSize
        });
    }

    [HttpGet("{id:int}")]
    public async Task<ActionResult<ApiResponse<JobDto>>> GetJobAsync([FromRoute] int id)
    {
        var jobResponse = await _jobServiceClient.GetJobAsync(new GetJobRequest()
        {
            Id = id
        });

        var jobDto = new JobDto()
        {
            Id = jobResponse.Id,
            Title = jobResponse.Title,
            Description = jobResponse.Description,
            PostingDate = DateTime.Parse(jobResponse.PostingDate),
            Deadline = DateTime.Parse(jobResponse.Deadline),
            Location = jobResponse.Location,
            Type = jobResponse.Type,
            Salary = jobResponse.Salary,
            Status = jobResponse.Status,
            JobProvider = new UserDto()
            {
                Id = jobResponse.JobProvider.Id,
                Email = jobResponse.JobProvider.Email,
                Name = jobResponse.JobProvider.JobProvider.Name,
                Description = jobResponse.JobProvider.JobProvider.Description,
                PhoneNumber = jobResponse.JobProvider.JobProvider.PhoneNumber,
            }
        };

        return Ok(new ApiResponse<JobDto>()
        {
            Data = jobDto,
            TotalSize = 1
        });
    }
    
    [HttpPost]
    public async Task<ActionResult<ApiResponse<JobDto>>> CreateJobAsync(
        [FromBody] JobDto jobDto
    )
    {
        var createJobRequest = new CreateJobRequest
        {
            Job = new JobProto
            {
                Title = jobDto.Title,
                Description = jobDto.Description,
                PostingDate = jobDto.PostingDate.ToString("o"),
                Deadline = jobDto.Deadline.ToString("o"),
                Location = jobDto.Location,
                Type = jobDto.Type,
                Salary = jobDto.Salary,
                JobProvider = new UserProto()
                {
                    Id = 11, // TODO: check null reference
                    JobProvider = new JobProviderProto()
                    {
                        Name = jobDto.JobProvider.Name
                    }
                }
            }
        };

        var jobProto = await _jobServiceClient.CreateJobAsync(createJobRequest);
    
        var createdJobDto = new JobDto
        {
            Id = jobProto.Id,
            Title = jobProto.Title,
            Description = jobProto.Description,
            PostingDate = DateTime.Parse(jobProto.PostingDate),
            Deadline = DateTime.Parse(jobProto.Deadline),
            Location = jobProto.Location,
            Type = jobProto.Type,
            Salary = jobProto.Salary,
            Status = jobProto.Status,
            JobProvider = new JobProviderDto() {
                Email = jobProto.JobProvider.Email,
                Name = jobProto.JobProvider.JobProvider.Name,
                }
        };

        return Ok(new ApiResponse<JobDto>
        {
            Data = createdJobDto
        });
    }
}
