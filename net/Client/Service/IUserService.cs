using DTOs;

namespace Client.Service;

public interface IUserService
{
    Task<ApiResponse<UserDto>> RegisterAsync(UserDto userDto);
}
