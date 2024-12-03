using Client.Service;
using JetBrains.Annotations;

namespace Client.Tests.Service;

[TestSubject(typeof(HttpJobService))]
public class HttpJobServiceTest
{
    [Fact]
    public void ClientTests_ShouldRun()
    {
        Assert.Fail("The Client tests are running, implement all tests.");
    }
}
