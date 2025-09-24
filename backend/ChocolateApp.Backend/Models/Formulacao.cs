using System.ComponentModel.DataAnnotations.Schema;

namespace ChocolateApp.Backend.Models
{
    [Table("formulacoes")]  // ✅ Correto - nome da tabela
    public class Formulacao
    {
        [Column("id")]  // ✅ Correto
        public int Id { get; set; }

        [Column("nome")]  // ✅ Correto
        public string Nome { get; set; } = string.Empty;

        [Column("quantidade_total_kg")]  // ✅ Correto
        public double QuantidadeTotalKg { get; set; }

        // ⚠️ POSSÍVEL PROBLEMA: 
        // O 'virtual' pode causar problemas com proxies se não estiver configurado
        // Vamos simplificar removendo o 'virtual'
       public List<Ingrediente> Ingredientes { get; set; } = new List<Ingrediente>();
    }
}