package com.chocolateapp.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "formulacoes")
public class Formulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "quantidade_total_kg")
    private Double quantidadeTotalKg;

    @OneToMany(mappedBy = "formulacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingrediente> ingredientes;

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

    public Double getQuantidadeTotalKg() {
        return quantidadeTotalKg;
    }

    public void setQuantidadeTotalKg(Double quantidadeTotalKg) {
        this.quantidadeTotalKg = quantidadeTotalKg;
    }

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }
}