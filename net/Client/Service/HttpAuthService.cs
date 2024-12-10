using System.Net;
using DTOs;

namespace Client.Service;

public class HttpAuthService(HttpClient httpClient) : IAuthService
{
    public async Task<UserDto> RegisterAsync(RegisterDto registerDto)
    {
        var httpResponse = await httpClient.PostAsJsonAsync("auth/register", registerDto);
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");

        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;

        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<UserDto>();

        return apiResponse ?? throw httpRequestException;
    }

    public async Task<UserDto> LoginAsync(LoginDto loginDto)
    {
        var httpResponse = await httpClient.PostAsJsonAsync("auth/login", loginDto);
        var httpRequestException = new HttpRequestException("Something went wrong, refresh the page or try again later.");
        
        if (!httpResponse.IsSuccessStatusCode) throw httpRequestException;
        
        var apiResponse = await httpResponse.Content.ReadFromJsonAsync<UserDto>();
        
        return apiResponse ?? throw httpRequestException;
    }
}