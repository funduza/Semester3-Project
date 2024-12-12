using DTOs;

namespace Client.Service;

public interface IUserService
{
    Task<UserDto> GetUserByIdAsync(long id);
    Task UpdateUserAsync(UserDto user);
}