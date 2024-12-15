using DTOs;

namespace Client.Service;

public interface IApplicationService
{
    Task<JobApplicationDto> CreateJobApplicationAsync(CreateJobApplicationDto createJobApplicationDto);
    Task<PagedResult<IEnumerable<JobApplicationDto>>> GetJobApplicationsAsync(string pageToken = "", int pageSize = 12, string filter = "");
    Task<PagedResult<JobApplicationDto>> GetApplicationAsync(long id);
    Task UpdateApplicationAsync(JobApplicationDto jobApplicationDto);
}
