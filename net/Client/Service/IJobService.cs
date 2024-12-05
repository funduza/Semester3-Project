using DTOs;

namespace Client.Service;

public interface IJobService
{
    Task<ApiResponse<IEnumerable<JobDto>>> GetJobsAsync(string pageToken = "", int pageSize = 12, string filter = "");
    Task<ApiResponse<JobDto>> GetJobAsync(int id);
}
