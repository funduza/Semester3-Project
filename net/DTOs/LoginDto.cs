using System.ComponentModel.DataAnnotations;

namespace DTOs;

public class LoginDto
{
    [EmailAddress, Required]
    public required string Email { get; set; }
    [Required]
    public required string Password { get; set; }
}