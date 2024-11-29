using Client.Components.Service;
using DTOs;
namespace Client.Service;

public class HttpJobService(HttpClient httpClient) : IJobService
{
    public async Task<List<JobDto>> GetAllJobsAsync()
    {
        HttpResponseMessage response = await httpClient.GetAsync("jobs");

        if (!response.IsSuccessStatusCode)
        {
            throw new Exception("Failed to retrieve jobs.");
        }

        List<JobDto>? jobs = await response.Content.ReadFromJsonAsync<List<JobDto>>();
        return jobs ?? new List<JobDto>();
    }

    public async Task<List<JobDto>> SearchJobAsync(string searchTerm)
    {
        HttpResponseMessage response = await httpClient.GetAsync($"jobs/search?query={Uri.EscapeDataString(searchTerm)}");

        if (!response.IsSuccessStatusCode)
        {
            throw new Exception("Failed to search jobs.");
        }

        List<JobDto>? jobs = await response.Content.ReadFromJsonAsync<List<JobDto>>();
        return jobs ?? new List<JobDto>();
    }
}