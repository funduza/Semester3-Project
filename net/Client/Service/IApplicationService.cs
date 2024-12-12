using DTOs;

namespace Client.Service;

public interface IApplicationService
{
    Task<ApiResponse<JobApplicationDto>> GetApplicationAsync(long id);
    Task UpdateApplicationAsync(JobApplicationDto jobApplicationDto);
}
