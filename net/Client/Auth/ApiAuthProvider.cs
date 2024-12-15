using System.Security.Claims;
using System.Text.Json;
using Client.Service;
using DTOs;
using Microsoft.AspNetCore.Components.Authorization;
using Microsoft.JSInterop;

namespace Client.Auth;

public class ApiAuthProvider : AuthenticationStateProvider
{
    private readonly JsonSerializerOptions jsonSerializerOptions = new() { PropertyNameCaseInsensitive = true };
    private readonly IAuthService httpAuthService;
    private IJSRuntime jsRuntime;
    private UserDto? userDto;

    public ApiAuthProvider(IAuthService httpAuthService, IJSRuntime jsRuntime)
    {
        this.httpAuthService = httpAuthService;
        this.jsRuntime = jsRuntime;
    }
    
    public async Task LoginAsync(LoginDto loginDto)
    {
        // Try to login through the WEB API
        userDto = await httpAuthService.LoginAsync(loginDto);
        // Add the current user to the browser session storage
        await jsRuntime.InvokeVoidAsync("sessionStorage.setItem", "currentUser", JsonSerializer.Serialize(userDto, jsonSerializerOptions));
        // Notify the application about the change
        NotifyAuthenticationStateChanged(GetAuthenticationStateAsync());
    }

    public async Task LogoutAsync()
    {
        // Remove the current user from memory
        userDto = null;
        // Remove the current user from the browser session storage
        await jsRuntime.InvokeVoidAsync("sessionStorage.setItem", "currentUser", "");
        // Notify the application about the change
        NotifyAuthenticationStateChanged(Task.FromResult(new AuthenticationState(new ClaimsPrincipal())));
    }

    public override async Task<AuthenticationState> GetAuthenticationStateAsync()
    {
        if (userDto is null)
        {
            try
            {
                var userAsJson = await jsRuntime.InvokeAsync<string>("sessionStorage.getItem", "currentUser");
                if (string.IsNullOrEmpty(userAsJson)) throw new Exception("User JSON is malformed");
                userDto = JsonSerializer.Deserialize<UserDto>(userAsJson, jsonSerializerOptions)!;
            }
            catch (Exception)
            {
                return new AuthenticationState(new ClaimsPrincipal());
            }
        }

        List<Claim> claims =
        [
            new(ClaimTypes.NameIdentifier, $"{userDto.Id}"),
            new(ClaimTypes.Email, $"{userDto.Email}"),
            new(ClaimTypes.Role, $"{userDto.Role}")
        ];

        var identity = new ClaimsIdentity(claims, "API authentication");
        var user = new ClaimsPrincipal(identity);

        return new AuthenticationState(user);
    }
}