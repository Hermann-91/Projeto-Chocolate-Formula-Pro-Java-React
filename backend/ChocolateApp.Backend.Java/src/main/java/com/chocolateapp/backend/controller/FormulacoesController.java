package com.chocolateapp.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chocolateapp.backend.model.Formulacao;
import com.chocolateapp.backend.model.Ingrediente;
import com.chocolateapp.backend.repository.FormulacaoRepository;

@RestController
@RequestMapping("/api/Formulacoes")
// ✅ CORREÇÃO FINAL: Permite pedidos do seu site online e do ambiente local.
@CrossOrigin(origins = {"http://localhost:3000", "https://produtostops.online"})
public class FormulacoesController {

    private final FormulacaoRepository formulacaoRepository;

    @Autowired
    public FormulacoesController(FormulacaoRepository formulacaoRepository) {
        this.formulacaoRepository = formulacaoRepository;
    }

    // GET: api/Formulacoes
    @GetMapping
    public ResponseEntity<List<Formulacao>> getFormulacoes() {
        try {
            List<Formulacao> formulacoes = formulacaoRepository.findAll();
            return ResponseEntity.ok(formulacoes);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(null);
        }
    }

    // GET: api/Formulacoes/5
    @GetMapping("/{id}")
    public ResponseEntity<Formulacao> getFormulacao(@PathVariable Integer id) {
        Optional<Formulacao> formulacao = formulacaoRepository.findById(id);
        return formulacao.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST: api/Formulacoes
    @PostMapping // ✅ Verifica se esta anotação está presente!
    public ResponseEntity<?> createFormulacao(@RequestBody Formulacao formulacao) {
        if (formulacao == null) {
            return ResponseEntity.badRequest().body("Dados da formulação são obrigatórios");
        }
        if (formulacao.getNome() == null || formulacao.getNome().isEmpty()) {
            return ResponseEntity.badRequest().body("Nome é obrigatório");
        }
        if (formulacao.getIngredientes() == null || formulacao.getIngredientes().isEmpty()) {
            return ResponseEntity.badRequest().body("Adicione pelo menos um ingrediente");
        }
        
        // Garante a ligação bidirecional para o JPA
        for (Ingrediente ingrediente : formulacao.getIngredientes()) {
            if (ingrediente.getNome() == null || ingrediente.getNome().isEmpty()) {
                return ResponseEntity.badRequest().body("Nome do ingrediente é obrigatório");
            }
            ingrediente.setFormulacao(formulacao);
        }

        try {
            Formulacao createdFormulacao = formulacaoRepository.save(formulacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFormulacao);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Erro ao salvar formulação: " + ex.getMessage());
        }
    }

    // DELETE: api/Formulacoes/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormulacao(@PathVariable Integer id) {
        if (formulacaoRepository.existsById(id)) {
            formulacaoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST: api/Formulacoes/seed (Para criar dados de teste)
    @PostMapping("/seed") // ✅ Verifica se esta anotação está presente!
    public ResponseEntity<String> seedData() {
        if (formulacaoRepository.count() > 0) {
            return ResponseEntity.ok("Já existem dados no banco");
        }

        Formulacao formulacao = new Formulacao();
        formulacao.setNome("Chocolate ao Leite Premium");
        formulacao.setQuantidadeTotalKg(1.0);
        
        List<Ingrediente> ingredientes = List.of(
            createIngrediente("Cacau", 40.0, 400.0, formulacao),
            createIngrediente("Açúcar", 30.0, 300.0, formulacao),
            createIngrediente("Leite em pó", 25.0, 250.0, formulacao),
            createIngrediente("Manteiga de cacau", 5.0, 50.0, formulacao)
        );
        formulacao.setIngredientes(ingredientes);

        try {
            formulacaoRepository.save(formulacao);
            return ResponseEntity.ok("Dados de teste criados com sucesso!");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar dados: " + ex.getMessage());
        }
    }

    // Método auxiliar para criar ingredientes (Java não permite criar dentro da lista diretamente)
    private Ingrediente createIngrediente(String nome, double porcentagem, double gramas, Formulacao formulacao) {
        Ingrediente i = new Ingrediente();
        i.setNome(nome);
        i.setPorcentagem(porcentagem);
        i.setGramas(gramas);
        i.setFormulacao(formulacao);
        return i;
    }
}
