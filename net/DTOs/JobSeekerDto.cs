namespace DTOs;

public class JobSeekerDto : UserDto
{
    public required string FirstName { get; set; }
    public required string LastName { get; set; }
    public string? PhoneNumber { get; set; }
    public string? Resume { get; set; }
}