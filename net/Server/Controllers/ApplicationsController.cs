using DTOs;
using Microsoft.AspNetCore.Mvc;
using Server.Grpc;

namespace Server.Controllers;

[ApiController]
[Route("applications")]
public class ApplicationsController : ControllerBase
{
    private readonly JobApplicationService.JobApplicationServiceClient _jobApplicationServiceClient;

    public ApplicationsController(JobApplicationService.JobApplicationServiceClient jobApplicationServiceClient)
    {
        _jobApplicationServiceClient = jobApplicationServiceClient;
    }
    
    [HttpPost]
    public async Task<ActionResult<PagedResult<JobApplicationDto>>> CreateApplicationAsync(
        [FromBody] CreateJobApplicationDto createJobApplicationDto
    )
    {
        
        var createJobApplicationRequest = new CreateJobApplicationRequest
        {
            JobApplication = new JobApplicationProto
            {
                JobId = createJobApplicationDto.JobId,
                JobSeekerId = createJobApplicationDto.JobSeekerId
            }
        };
        
        var createJobApplicationResponse = await _jobApplicationServiceClient.CreateJobApplicationAsync(createJobApplicationRequest);
        
        var createdApplicationDto = new JobApplicationDto
        {
            Id = createJobApplicationResponse.Id,
            Status = createJobApplicationResponse.Status,
            ApplicationDate = DateTimeOffset.Parse(createJobApplicationResponse.ApplicationDate),
            JobId = createJobApplicationResponse.JobId,
            JobSeekerId = createJobApplicationResponse.JobSeekerId
        };
        
        return Ok(new PagedResult<JobApplicationDto>
        {
            Data = createdApplicationDto
        });
    }
    
    [HttpGet]
    public async Task<ActionResult<PagedResult<IEnumerable<JobApplicationDto>>>> GetJobApplicationsAsync(
        [FromQuery] string pageToken = "",
        [FromQuery] int pageSize = 12,
        [FromQuery] string filter = ""
    )
    {
        var listJobApplicationsResponse = await _jobApplicationServiceClient.ListJobApplicationsAsync(new ListJobApplicationsRequest()
        {
            Filter = filter,
            PageSize = pageSize,
            PageToken = pageToken
        });

        var jobApplicationDtos = listJobApplicationsResponse.JobApplications.Select(jobApplicationProto => new JobApplicationDto()
        {
            Id = jobApplicationProto.Id,
            Status = jobApplicationProto.Status,
            ApplicationDate = DateTimeOffset.Parse(jobApplicationProto.ApplicationDate),
            JobId = jobApplicationProto.JobId,
            JobSeekerId = jobApplicationProto.JobSeekerId
        });

        return Ok(new PagedResult<IEnumerable<JobApplicationDto>>()
        {
            Data = jobApplicationDtos,
            NextPageToken = listJobApplicationsResponse.NextPageToken,
            TotalSize = listJobApplicationsResponse.TotalSize
        });
    }
    
    [HttpGet("{id:int}")]
    public async Task<ActionResult<PagedResult<JobApplicationDto>>> GetApplicationAsync([FromRoute] int id)
    {
        var jobResponse = await _jobApplicationServiceClient.GetJobApplicationAsync(new GetJobApplicationRequest()
        {
            Id = id
        });

        var jobApplicationDto = new JobApplicationDto()
        {
            Id = jobResponse.Id,
            Status = jobResponse.Status,
            ApplicationDate = DateTimeOffset.Parse(jobResponse.ApplicationDate),
            JobSeekerId = jobResponse.JobSeekerId,
            JobId = jobResponse.JobId
        };

        return Ok(new PagedResult<JobApplicationDto>()
        {
            Data = jobApplicationDto,
            TotalSize = 1
        });
    }
    
    [HttpPut("{id:int}")]
    public async Task<ActionResult<PagedResult<JobApplicationDto>>> UpdateApplicationAsync(
        [FromRoute] int id,
        [FromBody] JobApplicationDto jobApplicationDto
    )
    {
        var jobApplicationProto = await _jobApplicationServiceClient.UpdateJobApplicationAsync(new UpdateJobApplicationRequest
        {
            JobApplication = new JobApplicationProto
            {
                Id = id,
                Status = jobApplicationDto.Status,
                ApplicationDate = jobApplicationDto.ApplicationDate.ToString(),
                JobId = jobApplicationDto.JobId,
                JobSeekerId = jobApplicationDto.JobSeekerId
            }
        });
        
        var response = new JobApplicationDto
        {
            Id = jobApplicationProto.Id,
            Status = jobApplicationProto.Status,
            ApplicationDate = DateTimeOffset.Parse(jobApplicationProto.ApplicationDate),
            JobId = jobApplicationProto.JobId,
            JobSeekerId = jobApplicationProto.JobSeekerId
        };
        
        return Ok(new PagedResult<JobApplicationDto>
        {
            Data = response
        });
    }
}
