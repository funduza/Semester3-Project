using DTOs;
using Microsoft.AspNetCore.Mvc;
using Server.Grpc;
using Server.Mapper;

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

        var jobsDto = listJobsResponse.Jobs.Select(jobProto => new JobDto()
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
            JobProvider = UserMapper.ToDto(jobProto.JobProvider)
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
        var jobProto = await _jobServiceClient.GetJobAsync(new GetJobRequest()
        {
            Id = id
        });

        var jobDto = new JobDto()
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
            JobProvider = UserMapper.ToDto(jobProto.JobProvider)
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
                JobProvider = UserMapper.ToProto(jobDto.JobProvider)
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
            JobProvider = UserMapper.ToDto(jobProto.JobProvider)
        };

        return Ok(new ApiResponse<JobDto>
        {
            Data = createdJobDto
        });
    }
}
