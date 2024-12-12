using DTOs;
using Microsoft.AspNetCore.Mvc;
using Server.Grpc;
using Server.Mapper;

namespace Server.Controllers;


[ApiController]
[Route("users")]
public class UsersController: ControllerBase
{
    private readonly UserService.UserServiceClient _userServiceClient;
    
    public UsersController(UserService.UserServiceClient userServiceClient)
    {
        _userServiceClient = userServiceClient;
    }

    [HttpGet("{id:int}")]
    public async Task<ActionResult<UserDto>> GetUserAsync([FromRoute] int id)
    {
        var userProto = await _userServiceClient.GetUserAsync(new GetUserRequest
        {
            Id = id
        });

        var response = UserMapper.ToDto(userProto);

        return Ok(response);
    }
    
    [HttpPut]
    public async Task<ActionResult<UserDto>> UpdateUserAsync([FromBody] UserDto userDto)
    {
        var userProto = await _userServiceClient.UpdateUserAsync(new UpdateUserRequest()
        {
            User = UserMapper.ToProto(userDto)
        });
        
        var response = UserMapper.ToDto(userProto);

        return Ok(response);
    }
}