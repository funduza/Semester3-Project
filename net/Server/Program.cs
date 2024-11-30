using Grpc.Core;
using Grpc.Net.Client.Configuration;
using Server.Grpc;
using Server.Middlewares;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddGrpcClient<JobService.JobServiceClient>(options =>
{
    options.Address = new Uri("http://localhost:9090");
    options.ChannelOptionsActions.Add(channelOptions =>
    {
        channelOptions.ServiceConfig = new ServiceConfig()
        {
            MethodConfigs =
            {
                new MethodConfig()
                {
                    Names = { MethodName.Default },
                    RetryPolicy = new RetryPolicy()
                    {
                        MaxAttempts = 5,
                        InitialBackoff = TimeSpan.FromSeconds(1),
                        MaxBackoff = TimeSpan.FromSeconds(5),
                        BackoffMultiplier = 1.5,
                        RetryableStatusCodes = { StatusCode.Unavailable }
                    }
                }
            }
        };
    });
});
builder.Services.AddControllers();
builder.Services.AddTransient<GlobalExceptionHandlerMiddleware>();

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseMiddleware<GlobalExceptionHandlerMiddleware>();

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
