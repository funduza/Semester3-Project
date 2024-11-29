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
    public async Task<ActionResult<IEnumerable<JobDto>>> GetJobs()
    {
        var response = await _jobServiceClient.ListJobsAsync(new ListJobsRequest());
        var jobsDto = response.Jobs.Select(job => new JobDto()
        {
            Id = job.Id,
            Title = job.Title,
            Description = job.Description, 
            PostingDate = job.PostingDate,
            Deadline = job.Deadline,
            Location = job.Location,
            Type = job.Type,
            Salary = job.Salary,
            Status = job.Status
        });
        
        return Ok(jobsDto);
    }
    
    [HttpGet("search")] //added search path dont know if we want it
    public async Task<ActionResult<IEnumerable<JobDto>>> SearchJobs([FromQuery] string query)
    {
        if (string.IsNullOrWhiteSpace(query))
        {
            return BadRequest("search query empty");
        }

        var response = await _dataClient.getSearchedJobsAsync(new GetSearchedJobsRequest { Query = query });
        var jobsDto = response.Jobs.Select(job => new JobDto
        {
            Id = job.Id,
            Title = job.Title,
            Description = job.Description,
            Salary = job.Salary,
            Type = job.Type,
            Deadline = job.Deadline,
            Location = job.Location
        });

        return Ok(jobsDto);
    }
}