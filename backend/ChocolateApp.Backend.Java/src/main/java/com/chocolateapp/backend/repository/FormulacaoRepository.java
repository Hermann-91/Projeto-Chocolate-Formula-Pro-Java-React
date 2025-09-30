package com.chocolateapp.backend.repository;

import com.chocolateapp.backend.model.Formulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormulacaoRepository extends JpaRepository<Formulacao, Integer> {
}