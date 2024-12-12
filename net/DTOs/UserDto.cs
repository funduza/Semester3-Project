using System.ComponentModel.DataAnnotations;

namespace DTOs;

public class UserDto
{
    public long Id { get; set; }
    [EmailAddress, Required]
    public string? Email { get; set; }
    [Required]
    public string? Password { get; set; }
    [Required]
    public string? Role { get; set; }
    public string? FirstName { get; set; }
    public string? LastName { get; set; }
    public string? PhoneNumber { get; set; }
    public string? Resume { get; set; }
    public string? Name { get; set; }
    public string? Description { get; set; }
}