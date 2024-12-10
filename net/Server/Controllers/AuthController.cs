using DTOs;
using Microsoft.AspNetCore.Mvc;
using Server.Grpc;
using ArgumentException = System.ArgumentException;

namespace Server.Controllers;

[ApiController]
[Route("auth")]
public class AuthController : ControllerBase
{
    private readonly UserService.UserServiceClient _userServiceClient;

    public AuthController(UserService.UserServiceClient userServiceClient)
    {
        _userServiceClient = userServiceClient;
    }

    [HttpPost("register")]
    public async Task<ActionResult> RegisterAsync([FromBody] RegisterDto registerDto)
    {
        try
        {
            var newUser = new UserProto()
            {
                Email = registerDto.Email,
                Password = registerDto.Password,
                Role = registerDto.Role
            };
            
            switch (registerDto.Role)
            {
                case "JobProvider":
                    newUser.JobProvider = new JobProviderProto()
                    {
                        Name = registerDto.Name
                    };
                    break;
                case "JobSeeker":
                    newUser.JobSeeker = new JobSeekerProto()
                    {
                        FirstName = registerDto.FirstName,
                        LastName = registerDto.LastName
                    };
                    break;
                default:
                    throw new ArgumentException("Role is required. Use JobProvider or JobSeeker.");
            }

            var user = await _userServiceClient.CreateUserAsync(new CreateUserRequest()
            {
                User = newUser
            });
            
            return Ok(new UserDto() {
                Id = user.Id,
                Email = user.Email,
                Role = user.Role
            });
        }
        catch (Exception exception)
        {
            return Unauthorized(exception.Message);
        }
    }
    
    [HttpPost("login")]
    public async Task<ActionResult> LoginAsync([FromBody] LoginDto loginDto)
    {
        try
        {
            var user = await _userServiceClient.GetUserAsync(new GetUserRequest()
            {
                Email = loginDto.Email,
            });

            if (user.Password != loginDto.Password) throw new Exception("Wrong password");

            return Ok(new UserDto() {
                Id = user.Id,
                Email = user.Email,
                Role = user.Role
            });
        }
        catch (Exception exception)
        {
            return Unauthorized(exception.Message);
        }
    }
}