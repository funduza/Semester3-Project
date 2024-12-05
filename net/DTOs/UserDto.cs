namespace DTOs;

public class UserDto
{
    public long? Id { get; set; }
    public required string Email { get; set; }
    public string? Password { get; set; }
    public string? Role { get; set; }
}