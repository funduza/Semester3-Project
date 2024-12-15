using DTOs;
using Microsoft.AspNetCore.WebUtilities;
using System.Net.Http.Json;

namespace Client.Service;

public class HttpJobService(HttpClient httpClient) : IJobService
{
    public async Task<JobDto> CreateJobAsync(CreateJobDto createJobDto)
    {
        var httpResponse = await httpClient.PostAsJsonAsync("jobs", createJobDto);
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");

        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<JobDto>();

        return apiResponse ?? throw httpRequestException;
    }

    public async Task<PagedResult<IEnumerable<JobDto>>> GetJobsAsync(string pageToken = "", int pageSize = 12, string filter = "")
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

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<PagedResult<IEnumerable<JobDto>>>();

        return apiResponse ?? throw httpRequestException;
    }
    
    public async Task<JobDto> GetJobAsync(long id)
    {
        var httpResponse = await httpClient.GetAsync($"jobs/{id}");
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");

        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<JobDto>();

        return apiResponse ?? throw httpRequestException;
    }

    public async Task<JobDto> UpdateJobAsync(long id, UpdateJobDto updateJobDto)
    {
        var httpResponse = await httpClient.PutAsJsonAsync($"jobs/{id}", updateJobDto);
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");

        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<JobDto>();

        return apiResponse ?? throw httpRequestException;
    }
}
