namespace DTOs;

public class JobDto
{
    public required long Id { get; set; }
    public required string Title { get; set; }
    public required string Description { get; set; } 
    public required DateTime PostingDate { get; set; }
    public required DateTime Deadline { get; set; } 
    public required string Location { get; set; }
    public required string Type { get; set; }
    public required double Salary { get; set; }
    public required string Status { get; set; }
    public required UserDto JobProvider { get; set; }
}
