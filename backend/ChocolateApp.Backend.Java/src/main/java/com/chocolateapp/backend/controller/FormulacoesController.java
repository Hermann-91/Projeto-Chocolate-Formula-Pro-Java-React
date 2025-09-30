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
import org.springframework.web.bind.annotation.PutMapping; // Importação adicionada
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chocolateapp.backend.model.Formulacao;
import com.chocolateapp.backend.model.Ingrediente;
import com.chocolateapp.backend.repository.FormulacaoRepository;

@RestController
@RequestMapping("/api/Formulacoes")
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
    @PostMapping
    public ResponseEntity<?> createFormulacao(@RequestBody Formulacao formulacao) {
        if (formulacao == null || formulacao.getNome() == null || formulacao.getNome().isEmpty()) {
            return ResponseEntity.badRequest().body("Nome da formulação é obrigatório.");
        }
        if (formulacao.getIngredientes() == null || formulacao.getIngredientes().isEmpty()) {
            return ResponseEntity.badRequest().body("Adicione pelo menos um ingrediente.");
        }
        
        // Garante a ligação bidirecional para o JPA
        for (Ingrediente ingrediente : formulacao.getIngredientes()) {
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

    // ========================================================================
    // ✅ NOVO MÉTODO ADICIONADO: UPDATE (PUT)
    // ========================================================================
    // PUT: api/Formulacoes/5
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFormulacao(@PathVariable Integer id, @RequestBody Formulacao formulacaoDetails) {
        Optional<Formulacao> optionalFormulacao = formulacaoRepository.findById(id);

        if (optionalFormulacao.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Formulacao existingFormulacao = optionalFormulacao.get();

        // Atualiza os campos da formulação
        existingFormulacao.setNome(formulacaoDetails.getNome());
        existingFormulacao.setQuantidadeTotalKg(formulacaoDetails.getQuantidadeTotalKg());

        // Remove os ingredientes antigos
        existingFormulacao.getIngredientes().clear();

        // Adiciona os novos ingredientes e mantém a relação bidirecional
        if (formulacaoDetails.getIngredientes() != null) {
            for (Ingrediente ingrediente : formulacaoDetails.getIngredientes()) {
                ingrediente.setFormulacao(existingFormulacao);
                existingFormulacao.getIngredientes().add(ingrediente);
            }
        }

        try {
            Formulacao updatedFormulacao = formulacaoRepository.save(existingFormulacao);
            return ResponseEntity.ok(updatedFormulacao);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                  .body("Erro ao atualizar formulação: " + ex.getMessage());
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
    
    // ========================================================================
    // ✅ NOVO MÉTODO ADICIONADO: HEALTH CHECK
    // ========================================================================
    // GET: api/Formulacoes/health
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        try {
            long count = formulacaoRepository.count(); // Consulta simples e rápida
            return ResponseEntity.ok("Status: OK. Formulacoes count: " + count);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Status: ERRO. Falha ao conectar ao banco: " + e.getMessage());
        }
    }

    // POST: api/Formulacoes/seed (Para criar dados de teste)
    @PostMapping("/seed")
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

    // Método auxiliar para criar ingredientes
    private Ingrediente createIngrediente(String nome, double porcentagem, double gramas, Formulacao formulacao) {
        Ingrediente i = new Ingrediente();
        i.setNome(nome);
        i.setPorcentagem(porcentagem);
        i.setGramas(gramas);
        i.setFormulacao(formulacao);
        return i;
    }
}