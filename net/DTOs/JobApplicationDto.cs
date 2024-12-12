namespace DTOs;

public class JobApplicationDto
{
    public long Id { get; set; }
    public string Status { get; set; }
    public DateTimeOffset ApplicationDate { get; set; }
    public long JobId { get; set; }
    public long JobSeekerId { get; set; }
}
