using DTOs;

namespace Client.Service;

public interface IJobService
{
    Task<ApiResponse<IEnumerable<JobDto>>> GetJobsAsync(int pageToken = 0, int pageSize = 12, string filter = "");
}
