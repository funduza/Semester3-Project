namespace DTOs;

public class JobProviderDto : UserDto
{
    public required string Name { get; set; }
    public string? Description { get; set; }
    public string? PhoneNumber { get; set; }
}