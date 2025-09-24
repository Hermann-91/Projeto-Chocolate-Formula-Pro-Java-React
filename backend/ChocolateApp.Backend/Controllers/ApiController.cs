using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ChocolateApp.Backend.Data;
using ChocolateApp.Backend.Models;

namespace ChocolateApp.Backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class FormulacoesController : ControllerBase
    {
        private readonly ChocolateDbContext _context;

        public FormulacoesController(ChocolateDbContext context)
        {
            _context = context;
        }

        // GET: api/Formulacoes
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Formulacao>>> GetFormulacoes()
        {
            try
            {
                // ✅ CORREÇÃO: Adicionar Include para carregar ingredientes
                var formulacoes = await _context.Formulacoes
                    .Include(f => f.Ingredientes)  // ✅ AGORA COM INCLUDE
                    .ToListAsync();

                return Ok(formulacoes);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erro interno: {ex.Message}");
            }
        }

        // POST: api/Formulacoes
        [HttpPost]
        public async Task<ActionResult<Formulacao>> CreateFormulacao([FromBody] Formulacao formulacao)
        {
            try
            {
                if (formulacao == null)
                    return BadRequest("Dados da formulação são obrigatórios");

                if (string.IsNullOrEmpty(formulacao.Nome))
                    return BadRequest("Nome é obrigatório");

                // Garante que a lista de ingredientes não seja nula
                formulacao.Ingredientes ??= new List<Ingrediente>();

                // Valida se há pelo menos um ingrediente
                if (!formulacao.Ingredientes.Any())
                    return BadRequest("Adicione pelo menos um ingrediente");

                // Valida os ingredientes
                foreach (var ingrediente in formulacao.Ingredientes)
                {
                    if (string.IsNullOrEmpty(ingrediente.Nome))
                        return BadRequest("Nome do ingrediente é obrigatório");
                }

                _context.Formulacoes.Add(formulacao);
                await _context.SaveChangesAsync();

                // Retorna a formulação criada com os ingredientes
                return CreatedAtAction(nameof(GetFormulacoes), new { id = formulacao.Id }, formulacao);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erro ao salvar formulação: {ex.Message}");
            }
        }

        // GET: api/Formulacoes/5
        [HttpGet("{id}")]
        public async Task<ActionResult<Formulacao>> GetFormulacao(int id)
        {
            try
            {
                var formulacao = await _context.Formulacoes
                    .Include(f => f.Ingredientes)
                    .FirstOrDefaultAsync(f => f.Id == id);

                if (formulacao == null)
                    return NotFound();

                return Ok(formulacao);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erro interno: {ex.Message}");
            }
        }

        // DELETE: Remover formulacao api/Formulacao/remover

        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteFormulacao(int id)
        {
    try
    {
        // ✅ CORREÇÃO: Carrega a formulação COM os ingredientes
        var formulacao = await _context.Formulacoes
            .Include(f => f.Ingredientes)  // ✅ IMPORTANTE: Inclui os ingredientes
            .FirstOrDefaultAsync(f => f.Id == id);

        if (formulacao == null)
            return NotFound($"Formulação com ID {id} não encontrada");

        // ✅ Remove a formulação (os ingredientes serão removidos em cascade)
        _context.Formulacoes.Remove(formulacao);
        await _context.SaveChangesAsync();

        return NoContent(); // ✅ Retorna 204 No Content
    }
    catch (Exception ex)
    {
        // ✅ Log do erro completo para debug
        Console.WriteLine($"❌ ERRO AO REMOVER: {ex}");
        return StatusCode(500, $"Erro interno ao remover formulação: {ex.Message}");
    }
}


// POST: api/Formulacoes/seed (Para criar dados de teste)
[HttpPost("seed")]
public async Task<ActionResult> SeedData()
{
    try
    {
        // Verifica se já existem dados
        if (await _context.Formulacoes.AnyAsync())
            return Ok("Já existem dados no banco");

        var formulacao = new Formulacao
        {
            Nome = "Chocolate ao Leite Premium",
            QuantidadeTotalKg = 1.0,
            Ingredientes = new List<Ingrediente>
                    {
                        new Ingrediente { Nome = "Cacau", Porcentagem = 40, Gramas = 400 },
                        new Ingrediente { Nome = "Açúcar", Porcentagem = 30, Gramas = 300 },
                        new Ingrediente { Nome = "Leite em pó", Porcentagem = 25, Gramas = 250 },
                        new Ingrediente { Nome = "Manteiga de cacau", Porcentagem = 5, Gramas = 50 }
                    }
        };

        _context.Formulacoes.Add(formulacao);
        await _context.SaveChangesAsync();

        return Ok("Dados de teste criados com sucesso!");
    }
    catch (Exception ex)
    {
        return StatusCode(500, $"Erro ao criar dados: {ex.Message}");
    }
}
    }
}