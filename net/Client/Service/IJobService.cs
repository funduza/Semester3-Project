using DTOs;

namespace Client.Components.Service;

public interface IJobService
{
    Task<List<JobDto>> GetAllJobsAsync();
}