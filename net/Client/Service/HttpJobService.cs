using DTOs;
using Microsoft.AspNetCore.Http.Extensions;
using Microsoft.AspNetCore.WebUtilities;
using Microsoft.Extensions.Primitives;

namespace Client.Service;

public class HttpJobService(HttpClient httpClient) : IJobService
{
    public async Task<ApiResponse<IEnumerable<JobDto>>> GetJobsAsync(int pageToken = 0, int pageSize = 12, string filter = "")
    {
        var requestUri = QueryHelpers.AddQueryString("jobs", new Dictionary<string, string?>
        {
            { "pageToken", pageToken.ToString() },
            { "pageSize", pageSize.ToString() },
            { "filter", filter }
        });

        var httpResponse = await httpClient.GetAsync(requestUri);
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");

        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<ApiResponse<IEnumerable<JobDto>>>();

        return apiResponse ?? throw httpRequestException;
    }
}
