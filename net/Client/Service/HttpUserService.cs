using DTOs;

namespace Client.Service;

public class HttpUserService(HttpClient httpClient) : IUserService
{
    public async Task<ApiResponse<UserDto>> RegisterAsync(UserDto userDto)
    {
        var httpResponse = await httpClient.PostAsJsonAsync("users", userDto);
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");

        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<ApiResponse<UserDto>>();

        return apiResponse ?? throw httpRequestException;
    }
}