using DTOs;

namespace Client.Service;

public class HttpApplicationService(HttpClient httpClient) : IApplicationService
{
    public async Task<ApiResponse<JobApplicationDto>> GetApplicationAsync(long id)
    {
        var httpResponse = await httpClient.GetAsync($"applications/{id}");
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");

        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<ApiResponse<JobApplicationDto>>();

        return apiResponse ?? throw httpRequestException;
    }

    public async Task UpdateApplicationAsync(JobApplicationDto jobApplicationDto)
    {
        var httpResponse = await httpClient.PutAsJsonAsync($"applications/{jobApplicationDto.Id}", jobApplicationDto);
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");
        
        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;
    }
}
