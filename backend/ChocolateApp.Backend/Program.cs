using Microsoft.EntityFrameworkCore;
using ChocolateApp.Backend.Data;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers();

// Configure CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowReactApp",
        policy => policy.WithOrigins("http://localhost:3000")
                        .AllowAnyHeader()
                        .AllowAnyMethod());
});

// Configure DbContext com melhor tratamento de erro
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");

builder.Services.AddDbContext<ChocolateDbContext>(options =>
{
    options.UseNpgsql(connectionString, options =>
    {
        options.EnableRetryOnFailure(
            maxRetryCount: 3,
            maxRetryDelay: TimeSpan.FromSeconds(5),
            errorCodesToAdd: null);
    });
});

var app = builder.Build();

// Configure the HTTP request pipeline.
app.UseCors("AllowReactApp");
app.UseRouting();
app.MapControllers();

// Rota simples para testar se a API está funcionando
app.MapGet("/", () => "API Chocolate Formulações está funcionando!");

app.Run();