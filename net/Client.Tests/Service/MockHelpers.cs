using System.Net;
using System.Net.Http.Json;
using Client.Service;
using Moq;
using Moq.Protected;

namespace Client.Tests.Service;

public class MockHelpers
{
    public static Mock<DelegatingHandler> CreateDelegatingHandlerMock<T>(HttpStatusCode statusCode, T content)
    {
        var mockDelegatingHandler = new Mock<DelegatingHandler>();
        mockDelegatingHandler.Protected()
            .Setup<Task<HttpResponseMessage>>("SendAsync", ItExpr.IsAny<HttpRequestMessage>(),
                ItExpr.IsAny<CancellationToken>())
            .ReturnsAsync(new HttpResponseMessage
            {
                StatusCode = statusCode,
                Content = JsonContent.Create(content)
            });
        return mockDelegatingHandler;
    }
    
    public static HttpClient CreateHttpClientMock(Mock<DelegatingHandler> mockDelegatingHandler)
    {
        return new HttpClient(mockDelegatingHandler.Object)
        {
            BaseAddress = new Uri("http://localhost")
        };
    }
}