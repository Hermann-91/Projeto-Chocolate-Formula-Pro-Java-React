import React, { useState, useEffect } from 'react';
import './App.css';
import FormulacaoForm from './components/FormulacaoForm';
import ListaFormulacoes from './components/ListaFormulacoes';

// ✅ URL correta do backend
const API_BASE_URL = 'http://localhost:8080/api/Formulacoes';

function App() {
  const [formulacoes, setFormulacoes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // ✅ Carrega as formulações salvas no backend ao iniciar
  const carregarFormulacoes = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await fetch(API_BASE_URL);
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${response.statusText}`);
      }
      const data = await response.json();
      setFormulacoes(data);
      console.log('✅ Dados carregados:', data);
    } catch (error) {
      setError('Erro ao carregar formulações: ' + error.message);
      console.error('❌ Erro ao carregar:', error);
    }
    setLoading(false);
  };

  useEffect(() => {
    carregarFormulacoes();
  }, []);

  // ✅ Envia nova formulação para o backend
  const adicionarFormulacao = async (novaFormulacao) => {
    try {
      const response = await fetch(API_BASE_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(novaFormulacao),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Erro ao salvar formulação');
      }

      const resultado = await response.json();

      // ✅ Atualiza a lista após salvar
      setFormulacoes([...formulacoes, resultado]);
      console.log('✅ Formulação salva com sucesso:', resultado);

      return { success: true, data: resultado };
    } catch (error) {
      console.error('❌ Erro ao salvar:', error.message);
      return { success: false, error: error.message };
    }
  };

  // ✅ Remove uma formulação do backend
  const removerFormulacao = async (id) => {
    try {
      const response = await fetch(`${API_BASE_URL}/${id}`, {
        method: 'DELETE',
      });

      if (!response.ok) {
        // Se for status 204 (No Content), é sucesso!
        if (response.status === 204) {
          setFormulacoes(formulacoes.filter(f => f.id !== id));
          console.log(`✅ Formulação ${id} removida com sucesso`);
          return true;
        }
        throw new Error(`Erro ${response.status}: ${response.statusText}`);
      }

      // Para status 200 OK ou outros sucessos
      setFormulacoes(formulacoes.filter(f => f.id !== id));
      console.log(`✅ Formulação ${id} removida com sucesso`);
      return true;
    } catch (error) {
      console.error('❌ Erro ao remover:', error.message);
      alert('Erro ao remover formulação: ' + error.message);
      return false;
    }
  };
  // ✅ Criar dados de teste
  const criarDadosTeste = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/seed`, {
        method: 'POST'
      });

      if (!response.ok) {
        throw new Error('Erro ao criar dados de teste');
      }

      const result = await response.text();
      alert(result);

      // Recarrega os dados
      carregarFormulacoes();
    } catch (error) {
      alert('Erro: ' + error.message);
    }
  };

  return (
    <div className="App">
      <header className="app-header">
        <h1>🍫 Calculadora de Formulação de Chocolate</h1>
        <p>Backend .NET + Supabase + React</p>

        {/* ✅ Botões de controle */}
        <div className="control-buttons">
          <button onClick={criarDadosTeste} className="btn btn-primary">
            🧪 Criar Dados de Teste
          </button>
          <button onClick={carregarFormulacoes} className="btn btn-secondary">
            🔄 Recarregar Dados
          </button>
        </div>

        {error && <div className="error-message">❌ {error}</div>}
        {loading && <div className="loading-message">⏳ Carregando...</div>}
      </header>

      <FormulacaoForm onSalvar={adicionarFormulacao} />

      <hr />

      <ListaFormulacoes
        formulacoes={formulacoes}
        onRemover={removerFormulacao}
        loading={loading}
      />
    </div>
  );
}

export default App;