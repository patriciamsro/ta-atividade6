package com.iftm.client.tests.repositories;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;

@DataJpaTest
public class ClientRepositoryTest {
		//private List<Client> clientesCadastrados;
		
		@Autowired
		private ClientRepository repositorio;

		/*
		@BeforeEach
		private void setupAll() {
			clientesCadastrados = repositorio.findAll();
		}
		*/
		
		/**
		 * Cenário de teste 1
		 * Objetivo: Verificar se a exclusão realmente apaga um registro existente.
			monta o cenário, 
				- arquivo import.sql carrega o cenário (clientes cadastrados)
				- definir o id de um cliente que exista em import.sql
			executa a ação
				- executar o método de exclusão por id
				- executar o método de buscar por id
			e valida a saída.
				- verificar se o resultado do método de busca é falso
		 */
		@Test
		public void testarSeDeleteApagaClienteComIdExistente() {
			//cenário
			long id = 1;
			repositorio.deleteById(id);
			Optional<Client> resposta = repositorio.findById(id);
			Assertions.assertFalse(resposta.isPresent());
			
		}
		
		/**
		 * Cenário de teste 2
		 * Objetivo: Verificar se a exclusão retorna um erro quando um id não existente é informado.
			monta o cenário, 
				- arquivo import.sql carrega o cenário (clientes cadastrados)
				- definir o id de um cliente que não exista em import.sql
			executa a ação
				- executar o método de exclusão por id
			e valida a saída.
				- verificar se ocorre o erro: EmptyResultDataAccessException
		 */	
		@Test
		public void testarSeDeleteRetornaExceptionCasoIdNaoExiste() {
			long id = 10000;
			Assertions.assertThrows(EmptyResultDataAccessException.class, ()->{repositorio.deleteById(id);});
		}

		/**
		  Cenário de teste 3.
		  Objetivo: Verificar se a exclusão de todos elementos realmente apaga todos os registros do Banco de dados.
			monta o cenário,
				- arquivo import.sql carrega o cenário (clientes cadastrados)
			executa a ação
				- executar o método de exclusão de todos registros
			e valida a saída.
				- consultar todos os registros do banco e verificar se retorna vazio.
		 */
		@Test 
		public void testarSeDeleteApagaTodosRegistros() {
			repositorio.deleteAll();
			List<Client> resultado = repositorio.findAll();			
			Assertions.assertTrue(resultado.isEmpty());
		}
		
		/**
		 Cenário de teste 4
		 Objetivo: Verificar se a exclusão de uma entidade existente no banco de dados realmente ocorre.
			monta o cenário,
				- arquivo import.sql carrega o cenário (clientes cadastrados)
				- id de um cliente que existe em import.sql
			executa a ação
				- executa o método encontrar por id para retornar a entidade do	cliente com id informado.
				- executa o método apagar a entidade
				- executar novamente o método encontrar por id e verificar se o retorno dele é vazio
			e valida a saída.
				- verifica se o retorno do método encontrar por id é vazio.
		 */
		@Test
		public void testarSeDeleteApagaUmRegistroComIDExistente() {
			long id = 4;
			Optional<Client> cliente = repositorio.findById(id);
			repositorio.delete(cliente.get());
			Optional<Client> resultado = repositorio.findById(id);
			Assertions.assertTrue(resultado.isEmpty());
		}
		
		/*
		 Cenário de Teste 5
		 Objetivo: Verificar se um cliente pode ser excluído pelo cpf.
			monta o cenário,
				- arquivo import.sql carrega o cenário (clientes cadastrados)
				- cpf de um cliente cadastrado (10919444522)
			executa a ação
				- executar um método para excluir um cliente pelo cpf (não existe ainda).
				- buscar um cliente pelo cpf (não existe)
			e valida a saída.
				- a busca deve retornar vazia.	
		 */
		@Test
		public void testarSeDeleteApagaUmClientePorCpfExiste() {
			String cpfExistente = "10619244881";
			repositorio.deleteByCpf(cpfExistente);
			System.out.println(repositorio.findAll().size());
			Optional<Client> resultado = repositorio.findByCpf(cpfExistente);
			Assertions.assertTrue(resultado.isEmpty());
		}
		
		@Test
		public void TestarApagarSalariosMaioresQueValorExistente() {
			double salarioI=2000;
			repositorio.deleteByIncomeGreaterThan(salarioI);
			List<Client> resultado = repositorio.findByIncomeGreaterThan(salarioI);
			Assertions.assertTrue(resultado.isEmpty());
		}
		
		/**
		 * Exemplo de sala 01
		 * Cenário de Teste - buscar clientes que tenham CPF iniciados com determinado número.
		 * 
		 */
		@Test
		void testaBuscaClientesInicioCPFQueExiste() {
			// definir cenário
			String parteCpf = "104";			
			int tamanhoEsperado = 2;
			String cpfEsperados[] = {"10419244771", "10419344882"};

			//execução do método que está sendo testado
			List<Client> listaClientes = repositorio.findByCpfStartingWith(parteCpf);
			
			//comparação
			// existe elementos na lista
			Assertions.assertFalse(listaClientes.isEmpty());
			Assertions.assertEquals(tamanhoEsperado, listaClientes.size());
			for (int i = 0; i < cpfEsperados.length; i++) {
				Assertions.assertEquals(cpfEsperados[i], listaClientes.get(i).getCpf());	
			}			
			
		}
		
		/**
		 * Exemplo de sala 02
		 * Cenário de Teste - buscar clientes que tenham no mínimo X filhos
		 * 
		 */
		@Test
		void testaBuscaClientesQuantidadeMinimaFilhos() {
			//definir uma variável com a quantidade de filhos// propor algum método de repositorio que recebe a String e retorna uma lista de clientes
			// comparar se a lista retornada é diferente de vazio
			// comparar se os elementos retornados estão corretos.
		}
}
