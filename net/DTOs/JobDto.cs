namespace DTOs;

public class JobDto
{
    public required long Id { get; set; }
    public required string Title { get; set; }
    public required string Description { get; set; } 
    public required string PostingDate { get; set; }
    public required string Deadline { get; set; } 
    public required string Location { get; set; }
    public required string Type { get; set; }
    public required Double Salary { get; set; }
    public required string Status { get; set; }

    


}