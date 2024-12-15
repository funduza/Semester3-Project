using DTOs;
using Google.Protobuf.WellKnownTypes;
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

    [HttpPost]
    public async Task<ActionResult<JobDto>> CreateJobAsync(
        [FromBody] CreateJobDto createJobDto
    )
    {
        var jobProto = await _jobServiceClient.CreateJobAsync(new CreateJobRequest
        {
            Job = new JobProto
            {
                Title = createJobDto.Title,
                Description = createJobDto.Description,
                Deadline = createJobDto.Deadline.ToTimestamp(),
                Location = createJobDto.Location,
                Type = createJobDto.Type,
                Salary = createJobDto.Salary,
                JobProvider = new UserProto()
                {
                    Id = createJobDto.JobProviderId
                }
            }
        });
    
        var jobDto = new JobDto
        {
            Id = jobProto.Id,
            Title = jobProto.Title,
            Description = jobProto.Description,
            PostingDate = jobProto.PostingDate.ToDateTimeOffset(),
            Deadline = jobProto.Deadline.ToDateTimeOffset(),
            Location = jobProto.Location,
            Type = jobProto.Type,
            Salary = jobProto.Salary,
            Status = jobProto.Status,
            JobProvider = UserMapper.ToDto(jobProto.JobProvider)
        };

        return CreatedAtAction("GetJob", new { id = jobDto.Id }, jobDto);
    }

    [HttpGet]
    public async Task<ActionResult<PagedResult<IEnumerable<JobDto>>>> GetJobsAsync(
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

        var jobDtos = listJobsResponse.Jobs.Select(jobProto => new JobDto()
        {
            Id = jobProto.Id,
            Title = jobProto.Title,
            Description = jobProto.Description,
            PostingDate = jobProto.PostingDate.ToDateTimeOffset(),
            Deadline = jobProto.Deadline.ToDateTimeOffset(),
            Location = jobProto.Location,
            Type = jobProto.Type,
            Salary = jobProto.Salary,
            Status = jobProto.Status,
            JobProvider = UserMapper.ToDto(jobProto.JobProvider)
        });

        return Ok(new PagedResult<IEnumerable<JobDto>>()
        {
            Data = jobDtos,
            NextPageToken = listJobsResponse.NextPageToken,
            TotalSize = listJobsResponse.TotalSize
        });
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<JobDto>> GetJobAsync([FromRoute] long id)
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
            PostingDate = jobProto.PostingDate.ToDateTimeOffset(),
            Deadline = jobProto.Deadline.ToDateTimeOffset(),
            Location = jobProto.Location,
            Type = jobProto.Type,
            Salary = jobProto.Salary,
            Status = jobProto.Status,
            JobProvider = UserMapper.ToDto(jobProto.JobProvider)
        };

        return Ok(jobDto);
    }
}
