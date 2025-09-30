package com.chocolateapp.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "ingredientes")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "porcentagem")
    private Double porcentagem;

    @Column(name = "gramas")
    private Double gramas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formulacao_id")
    @JsonBackReference // ✅ ADICIONE ESTA LINHA
    private Formulacao formulacao;

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(Double porcentagem) {
        this.porcentagem = porcentagem;
    }

    public Double getGramas() {
        return gramas;
    }

    public void setGramas(Double gramas) {
        this.gramas = gramas;
    }

    public Formulacao getFormulacao() {
        return formulacao;
    }

    public void setFormulacao(Formulacao formulacao) {
        this.formulacao = formulacao;
    }
}