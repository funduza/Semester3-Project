using DTOs;
using Microsoft.AspNetCore.WebUtilities;

namespace Client.Service;

public class HttpJobService(HttpClient httpClient) : IJobService
{
    public async Task<ApiResponse<IEnumerable<JobDto>>> GetJobsAsync(string pageToken = "", int pageSize = 12, string filter = "")
    {
        var requestUri = QueryHelpers.AddQueryString("jobs", new Dictionary<string, string?>
        {
            { "pageToken", pageToken },
            { "pageSize", pageSize.ToString() },
            { "filter", filter }
        });

        var httpResponse = await httpClient.GetAsync(requestUri);
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");

        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<ApiResponse<IEnumerable<JobDto>>>();

        return apiResponse ?? throw httpRequestException;
    }
    
    public async Task<ApiResponse<JobDto>> GetJobAsync(int id)
    {
        var requestUri = $"jobs/{id}";

        var httpResponse = await httpClient.GetAsync(requestUri);
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");

        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<ApiResponse<JobDto>>();

        return apiResponse ?? throw httpRequestException;
    }
}
