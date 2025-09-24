using System.ComponentModel.DataAnnotations.Schema;

namespace ChocolateApp.Backend.Models
{
    [Table("ingredientes")]
    public class Ingrediente
    {
        [Column("id")]
        public int Id { get; set; }

        [Column("nome")]
        public string Nome { get; set; } = string.Empty;

        [Column("porcentagem")]
        public double Porcentagem { get; set; }

        [Column("gramas")]
        public double Gramas { get; set; }

        [Column("formulacao_id")]
        public int FormulacaoId { get; set; }

       // [ForeignKey("FormulacaoId")]  // ✅ CORRIGIDO: Nome da propriedade
       // public Formulacao Formulacao { get; set; } = null!;  // ✅ CORRIGIDO: Sem conflito nullable
    }
}