namespace DTOs;

public class ApiResponse<T>
{
    public required T Data { get; set; }
    public string? NextPageToken { get; set; }
    public int TotalSize { get; set; }
}