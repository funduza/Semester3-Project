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
            Status = job.Status
        });

        return Ok(new ApiResponse<IEnumerable<JobDto>>()
        {
            Data = jobsDto,
            NextPageToken = listJobsResponse.NextPageToken,
            TotalSize = listJobsResponse.TotalSize
        });
    }
}
