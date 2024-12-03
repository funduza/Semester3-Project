using System.Threading.Tasks;
using JetBrains.Annotations;
using Server.Controllers;
using Xunit;

namespace Server.Tests.Controllers;

[TestSubject(typeof(JobsController))]
public class JobsControllerTest
{
    [Fact]
    public void ServerTests_ShouldRun()
    {
        Assert.Fail("The Server tests are running, implement all tests.");
    }
}
