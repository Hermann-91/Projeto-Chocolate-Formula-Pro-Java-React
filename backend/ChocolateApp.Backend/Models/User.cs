using System.ComponentModel.DataAnnotations.Schema;

namespace ChocolateApp.Backend.Models
{
    [Table("users")]
    public class User
    {
        [Column("id")]
        public int Id { get; set; }  // ✅ Chave primária obrigatória

        [Column("email")]
        public string Email { get; set; } = string.Empty;

        [Column("senha")]
        public string Senha { get; set; } = string.Empty;

        [Column("role")]
        public string Role { get; set; } = string.Empty;
    }
}