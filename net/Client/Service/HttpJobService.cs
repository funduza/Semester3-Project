using DTOs;
using Microsoft.AspNetCore.WebUtilities;
using System.Net.Http.Json;

namespace Client.Service;

public class HttpJobService : IJobService
{
    private readonly HttpClient httpClient;

    public HttpJobService(HttpClient httpClient)
    {
        this.httpClient = httpClient;
    }

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
    
    public async Task<ApiResponse<JobDto>> CreateJobAsync(JobDto job)
    {
        var httpResponse = await httpClient.PostAsJsonAsync("jobs", job);

        if (!httpResponse.IsSuccessStatusCode)
        {
            var errorContent = await httpResponse.Content.ReadAsStringAsync();
            throw new HttpRequestException($"Failed to create job: {errorContent}");
        }

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<ApiResponse<JobDto>>();

        return apiResponse ?? throw new HttpRequestException("Invalid response from the server.");
    }

    public async Task<ApiResponse<JobApplicationDto>> ApplyJobAsync(int id, JobApplicationDto applyJobRequest)
    {
        if (applyJobRequest == null)
        {
            throw new ArgumentNullException(nameof(applyJobRequest), "ApplyJobRequest cannot be null.");
        }

        var httpResponse = await httpClient.PostAsJsonAsync("jobapplications/apply", applyJobRequest);

        if (!httpResponse.IsSuccessStatusCode)
        {
            var errorContent = await httpResponse.Content.ReadAsStringAsync();
            throw new HttpRequestException($"Failed to apply for job: {errorContent}");
        }

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<ApiResponse<JobApplicationDto>>();

        return apiResponse ?? throw new HttpRequestException("Invalid response from the server.");
    }
}
