import React from 'react';
import "./ListaFormulacoes.css";
import { useState } from 'react';

function ListaFormulacoes({ formulacoes, onRemover, loading }) {
   const [removendo, setRemovendo] = useState(null);
   const handleRemover = async (id, nome) => {
    if (!window.confirm(`Tem certeza que deseja remover "${nome}"?`)) {
      return;
    }
    setRemovendo(id);
    const sucesso = await onRemover(id);
    setRemovendo(null);

    if (sucesso) {
      // Feedback visual opcional
      console.log(`Formulação ${id} removida`);
    }
  };
   
  if (loading) {
    return (
      <div className="lista-container">
        <h2>📂 Formulações Salvas</h2>
        <div className="loading-message">⏳ Carregando formulações...</div>
      </div>
    );
  }

  if (!formulacoes || formulacoes.length === 0) {
    return (
      <div className="lista-container">
        <h2>📂 Formulações Salvas</h2>
        <div className="empty-message">
          📝 Nenhuma formulação salva ainda.<br/>
          <small>Crie uma nova formulação ou clique em "Criar Dados de Teste"</small>
        </div>
      </div>
    );
  }

  return (
    <div className="lista-container">
      <h2>📂 Formulações Salvas ({formulacoes.length})</h2>
      <div className="formulacoes-grid">
        {formulacoes.map((form) => (
          <div key={form.id} className="formulacao-card">
            <div className="formulacao-header">
              <h3>🍫 {form.nome}</h3>
              <span className="quantidade-total">{form.quantidadeTotalKg} kg</span>
            </div>
            
            <div className="ingredientes-list">
              <h4>📋 Ingredientes:</h4>
              <ul>
                {form.ingredientes && form.ingredientes.map((ing, index) => (
                  <li key={index} className="ingrediente-item">
                    <span className="ingrediente-nome">{ing.nome}</span>
                    <span className="ingrediente-detalhes">
                      {ing.porcentagem}% • {ing.gramas}g
                    </span>
                  </li>
                ))}
              </ul>
              
              {/* ✅ Cálculo de totais */}
              {form.ingredientes && (
                <div className="totais">
                  <strong>Total: {form.ingredientes.reduce((sum, ing) => sum + (ing.gramas || 0), 0)}g</strong>
                </div>
              )}
            </div>
            
            <button
            className={`remover-button ${removendo === form.id ? 'removendo' : ''}`}
            onClick={() => handleRemover(form.id, form.nome)}
            disabled={removendo === form.id}
            title="Remover formulação"
          >
            {removendo === form.id ? '⏳ Removendo...' : '🗑️ Remover'}
          </button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ListaFormulacoes;