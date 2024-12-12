using DTOs;

namespace Client.Service;

public class HttpUserService(HttpClient httpClient) : IUserService
{
    public async Task<UserDto> GetUserByIdAsync(long id)
    {
        var httpResponse = await httpClient.GetAsync($"users/{id}");
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");

        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<UserDto>();

        return apiResponse ?? throw httpRequestException;
    }

    public async Task UpdateUserAsync(UserDto user)
    {
        var httpResponse = await httpClient.PutAsJsonAsync("users", user); 
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");
        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;
    }
}