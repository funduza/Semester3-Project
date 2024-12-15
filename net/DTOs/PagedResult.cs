namespace DTOs;

public class PagedResult<T>
{
    public required T Data { get; set; }
    public string? NextPageToken { get; set; }
    public long TotalSize { get; set; }
}