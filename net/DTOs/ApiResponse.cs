namespace DTOs;

public class ApiResponse<T>
{
    public required T Data { get; set; }
    public int NextPageToken { get; set; }
}