namespace DTOs;

public class JobDto
{
    public long Id { get; set; }
    public string? Title { get; set; }
    public string? Description { get; set; } 
    public DateTimeOffset PostingDate { get; set; }
    public DateTimeOffset Deadline { get; set; } 
    public string? Location { get; set; }
    public string? Type { get; set; }
    public int Salary { get; set; }
    public string? Status { get; set; }
    public UserDto? JobProvider { get; set; }
}
