using Microsoft.EntityFrameworkCore;
using ChocolateApp.Backend.Models;

namespace ChocolateApp.Backend.Data
{
    public class ChocolateDbContext : DbContext
    {
        public DbSet<User> Users { get; set; }
        public DbSet<Formulacao> Formulacoes { get; set; }
        public DbSet<Ingrediente> Ingredientes { get; set; }

        public ChocolateDbContext(DbContextOptions<ChocolateDbContext> options)
            : base(options) { }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Formulacao>(entity =>
            {
                entity.ToTable("formulacoes");
                entity.HasMany(f => f.Ingredientes)
                      .WithOne()
                      .HasForeignKey(i => i.FormulacaoId)
                      .OnDelete(DeleteBehavior.Cascade); // ✅ Isso é ESSENCIAL
            });

            modelBuilder.Entity<Ingrediente>(entity =>
            {
                entity.ToTable("ingredientes");
            });
        }
    }
}