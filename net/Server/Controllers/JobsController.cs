using DTOs;
using Microsoft.AspNetCore.Mvc;
using Server.Grpc;

namespace Server.Controllers;

[ApiController]
[Route("jobs")]
public class JobsController : ControllerBase
{
    private readonly Data.DataClient _dataClient;

    public JobsController(Data.DataClient dataClient)
    {
        _dataClient = dataClient;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<JobDto>>> GetJobs()
    {
        var response = await _dataClient.getAllJobsAsync(new GetAllJobsRequest());
        var jobsDto = response.Jobs.Select(job => new JobDto()
        {
            Id = job.Id,
            Title = job.Title,
            Description = job.Description, 
            Salary = job.Salary,
            Type = job.Type,
            Deadline = job.Deadline, 
            Location = job.Location,
            
        });
        
        return Ok(jobsDto);
    }
}