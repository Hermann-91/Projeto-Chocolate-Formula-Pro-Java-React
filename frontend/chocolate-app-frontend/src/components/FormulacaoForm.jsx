import React, { useState } from 'react';
import './FormulacaoForm.css';

export default function FormulacaoForm({ onSalvar }) {
  const [nome, setNome] = useState('');
  const [quantidadeTotal, setQuantidadeTotal] = useState('');
  const [ingredientes, setIngredientes] = useState([]);

  const adicionarIngrediente = () => {
    setIngredientes([...ingredientes, { nome: '', porcentagem: '' }]);
  };

  const removerIngrediente = (index) => {
    if (ingredientes.length > 1) {
      const novosIngredientes = ingredientes.filter((_, i) => i !== index);
      setIngredientes(novosIngredientes);
    }
  };

  const atualizarIngredientes = (index, campo, valor) => {
    const novosIngredientes = [...ingredientes];
    novosIngredientes[index][campo] = valor;
    setIngredientes(novosIngredientes);
  };

  const calcularGramas = () => {
    return ingredientes.map((ing) => {
      const gramas = (quantidadeTotal * 1000 * ing.porcentagem) / 100;
      return { 
        ...ing, 
        gramas: parseFloat(gramas.toFixed(2)), // ✅ Garantir número, não string
        porcentagem: parseFloat(ing.porcentagem) || 0 // ✅ Garantir número
      };
    });
  };

  const ingredientesCalculados = calcularGramas();

  const somaPorcentagens = ingredientes.reduce((total, ing) => {
    return total + parseFloat(ing.porcentagem || 0);
  }, 0);

  const porcentagemValida = somaPorcentagens === 100;
  const formulacaoValida = nome.trim() && quantidadeTotal > 0 && porcentagemValida;

  const salvarFormula = async () => {
    if (!formulacaoValida) return;

    const novaFormulacao = {
      nome: nome.trim(),
      quantidadeTotalKg: parseFloat(quantidadeTotal), // ✅ Nome do campo correto
      ingredientes: ingredientesCalculados.filter(ing => ing.nome.trim() !== ''), // ✅ Filtrar vazios
    };

    // ✅ Chamar a função de salvamento e aguardar resultado
    const resultado = await onSalvar(novaFormulacao);
    
    if (resultado && resultado.success) {
      // ✅ Limpar formulário apenas se salvou com sucesso
      setNome('');
      setQuantidadeTotal('');
      setIngredientes([{ nome: '', porcentagem: '' }]); // ✅ Deixar um ingrediente vazio
    }
  };

  return (
    <div className="form-container">
      <h2 className="form-title">Nova Formulação</h2>

      <div className="form-group">
        <label>Nome da Formulação:</label>
        <input
          className="form-input"
          type="text"
          placeholder="Ex: Chocolate Amargo 70%"
          value={nome}
          onChange={(e) => setNome(e.target.value)}
        />
      </div>

      <div className="form-group">
        <label>Quantidade Total (kg):</label>
        <input
          className="form-input"
          type="number"
          step="0.1"
          min="0.1"
          placeholder="Ex: 1.0"
          value={quantidadeTotal}
          onChange={(e) => setQuantidadeTotal(e.target.value)}
        />
      </div>

      <h3 className="section-title">Ingredientes</h3>
      
      {ingredientes.map((ing, index) => (
        <div className="ingrediente-bloco" key={index}>
          <input
            className="form-input ingrediente-nome"
            type="text"
            placeholder="Nome do ingrediente"
            value={ing.nome}
            onChange={(e) => atualizarIngredientes(index, 'nome', e.target.value)}
          />
          <input
            className="form-input ingrediente-porcentagem"
            type="number"
            step="0.1"
            placeholder="%"
            value={ing.porcentagem}
            onChange={(e) => atualizarIngredientes(index, 'porcentagem', e.target.value)}
          />
          {ingredientes.length > 1 && (
            <button 
              type="button" 
              className="btn-remover"
              onClick={() => removerIngrediente(index)}
            >
              ❌
            </button>
          )}
        </div>
      ))}

      <button className="form-button secondary" onClick={adicionarIngrediente}>
        ➕ Adicionar Ingrediente
      </button>

      <div className="porcentagem-info">
        <h4>Total de porcentagens: {somaPorcentagens.toFixed(1)}%</h4>
        {!porcentagemValida && (
          <p className="erro-porcentagem">
            ⚠️ A soma das porcentagens deve ser exatamente 100%
          </p>
        )}
        {porcentagemValida && (
          <p className="sucesso-porcentagem">
            ✅ Porcentagem válida!
          </p>
        )}
      </div>

      <button
        className="form-button primary"
        disabled={!formulacaoValida}
        onClick={salvarFormula}
      >
        💾 Salvar Formulação
      </button>

      {ingredientesCalculados.length > 0 && (
        <>
          <h3 className="section-title">Resultado da Formulação</h3>
          <ul className="resultado-lista">
            {ingredientesCalculados
              .filter(ing => ing.nome.trim() !== '')
              .map((ing, index) => (
              <li key={index}>
                <strong>{ing.nome}</strong>: {ing.porcentagem}% → {ing.gramas}g
              </li>
            ))}
          </ul>
        </>
      )}
    </div>
  );
}