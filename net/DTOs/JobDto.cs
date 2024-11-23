namespace DTOs;

public class JobDto
{
    public required long Id { get; set; }
    public required string Title { get; set; }
    public string Description { get; set; } = string.Empty;
    public string Salary { get; set; } = string.Empty;
    public string Deadline { get; set; } = string.Empty;
    public string Status { get; set; } = string.Empty;
}