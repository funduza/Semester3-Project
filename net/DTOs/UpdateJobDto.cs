using System.ComponentModel.DataAnnotations;

namespace DTOs;

public class UpdateJobDto
{
    [Required]
    public required string Status { get; set; }
}