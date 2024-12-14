using DTOs;
using Microsoft.AspNetCore.WebUtilities;

namespace Client.Service;

public class HttpApplicationService(HttpClient httpClient) : IApplicationService
{
    public async Task<ApiResponse<IEnumerable<JobApplicationDto>>> GetJobApplicationsAsync(string pageToken = "", int pageSize = 12, string filter = "")
    {
        var requestUri = QueryHelpers.AddQueryString("applications", new Dictionary<string, string?>
        {
            { "pageToken", pageToken },
            { "pageSize", pageSize.ToString() },
            { "filter", filter }
        });

        var httpResponse = await httpClient.GetAsync(requestUri);
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");

        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<ApiResponse<IEnumerable<JobApplicationDto>>>();

        return apiResponse ?? throw httpRequestException;
    }
    
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
