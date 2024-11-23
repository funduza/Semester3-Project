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
            throw new Exception("Failed to retrieve posts.");
        }

        List<JobDto>? posts = await response.Content.ReadFromJsonAsync<List<JobDto>>();
        return posts ?? new List<JobDto>();
    }
}