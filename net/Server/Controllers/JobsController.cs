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
    public async Task<IResult> GetAllJobs()
    {
        var getAllJobsResponse = await _dataClient.getAllJobsAsync(new GetAllJobsRequest());
        var jobDtos = getAllJobsResponse.Jobs.Select(job => new JobDto()
        {
            Id = job.Id,
            Title = job.Title,
        });
        return Results.Ok(jobDtos);
    }
}