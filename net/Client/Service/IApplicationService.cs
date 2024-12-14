using DTOs;

namespace Client.Service;

public interface IApplicationService
{
    Task<ApiResponse<IEnumerable<JobApplicationDto>>> GetJobApplicationsAsync(string pageToken = "", int pageSize = 12, string filter = "");
    Task<ApiResponse<JobApplicationDto>> GetApplicationAsync(long id);
    Task UpdateApplicationAsync(JobApplicationDto jobApplicationDto);
}
