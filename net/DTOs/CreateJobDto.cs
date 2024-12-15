using System.ComponentModel.DataAnnotations;

namespace DTOs;

public class CreateJobDto
{
    [Required]
    public required string Title { get; set; }
    [Required]
    public required string Description { get; set; }
    [Required]
    public required DateTimeOffset Deadline { get; set; }
    [Required]
    public required string Location { get; set; }
    [Required, Range(1, int.MaxValue)]
    public required int Salary { get; set; }
    [Required]
    public required string Type { get; set; }
    [Required]
    public required long JobProviderId { get; set; }
}