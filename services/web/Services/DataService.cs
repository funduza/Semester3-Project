using Grpc.Core;

namespace web.Services;

public class DataService : Data.DataBase
{
    private readonly ILogger<DataService> _logger;

    public DataService(ILogger<DataService> logger)
    {
        _logger = logger;
    }
    
    public override Task<GetAllJobsResponse> getAllJobs(GetAllJobsRequest request, ServerCallContext context)
    {
        _logger.LogInformation("GetAllJobsRequest: {}", request);
        
        var job = new GetAllJobsResponse.Types.Job() { Id = 1, Title = "Software Engineer" };
        
        var response = new GetAllJobsResponse()
        {
            Jobs = { job }
        };

        return Task.FromResult(response);
    }
}