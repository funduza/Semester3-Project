using DTOs;

namespace Client.Service;

public interface IJobService
{
    Task<JobDto> CreateJobAsync(CreateJobDto createJobDto);
    Task<PagedResult<IEnumerable<JobDto>>> GetJobsAsync(string pageToken = "", int pageSize = 12, string filter = "");
    Task<JobDto> GetJobAsync(long id);
    Task<JobDto> UpdateJobAsync(long id, UpdateJobDto updateJobDto);
}
