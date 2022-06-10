package com.iftm.client.tests.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;

// para testar camada de serviço utilize essa notação, que carrega o contexto com os recursos do Spring boot
@ExtendWith(SpringExtension.class)
public class ClienteServiceTest {
		
	@InjectMocks
	private ClientService servico;
	
	@Mock
	private ClientRepository repositorio;

	/* Resolução - Atividade 6 */

	/*• delete deveria
		◦ retornar vazio quando o id existir
		◦ lançar uma EmptyResultDataAccessException quando o id não existir */

	@Test
	public void testarSeDeleteNaoRetornaNadaQuandoIdExiste() {
		Long idExistente = 1L;
		Mockito.doNothing().when(repositorio).deleteById(idExistente);

		Assertions.assertDoesNotThrow(()->servico.delete(idExistente));
		Mockito.verify(repositorio, Mockito.times(1)).deleteById(idExistente);
	}

	@Test
	public void testarSeDeleteLancaExceptionQuandoIdNaoExiste() {
		Long idNaoExistente = 5000L;
		Mockito.doThrow(ResourceNotFoundException.class).when(repositorio).deleteById(idNaoExistente);

		Assertions.assertThrows(ResourceNotFoundException.class, ()->servico.delete(idNaoExistente));
		Mockito.verify(repositorio, Mockito.times(1)).deleteById(idNaoExistente);
	}

	/*• findAllPaged deveria retornar uma página com todos os clientes
	(e chamar o método findAll do repository)*/

	@Test
	public void testarRetornoDaPaginaComTodosOsClientes() {
		//configuracao da pagina
		PageRequest pageRequest = PageRequest.of(11,1);
		//lista de clientes esperados
		List<Client> lista = new ArrayList<Client>();
		lista.add(new Client(8L, "'Jorge Amado", "10204374161", 2500.0, Instant.parse("1975-11-10T07:00:00Z"), 0));
		//criacao da paginacao
		Page<Client> pagina = new PageImpl<>(lista, pageRequest, lista.size());
		//mock
		Mockito.when(repositorio.findAll(pageRequest)).thenReturn(pagina);

		//verificar as execuções da classe mock e de seus métodos
		Page<ClientDTO> resultado = servico.findAllPaged(pageRequest);
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertEquals(lista.size(), resultado.getNumberOfElements());
		for(int i = 0; i < lista.size(); i++) {
			Assertions.assertEquals(lista.get(i), resultado.toList().get(i).toEntity());
		}
		Mockito.verify(repositorio, Mockito.times(1)).findAll(pageRequest);
	}

	/* findByIncome deveria retornar uma página com os clientes que tenham o Income informado
	(e chamar o método findByIncome do repository) */

	@Test
	public void testarRetornoDoMetodoFindByIncome() {
		//configuracao da pagina
		PageRequest pageRequest = PageRequest.of(1, 10, Direction.valueOf("ASC"), "name");
		//definindo o vlaor de comparacao
		Double  entrada = 4500.00;
		//lista de clientes esperados
		List<Client> lista = new ArrayList<Client>();
		lista.add(new Client(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1));
		//criacao da paginacao
		Page<Client> pagina = new PageImpl<>(lista, pageRequest, lista.size());
		//mock
		Mockito.when(repositorio.findByIncome(entrada, pageRequest)).thenReturn(pagina);

		//verificar as execuções da classe mock e de seus métodos
		Page<ClientDTO> resultado = servico.findByIncome(pageRequest, entrada);
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertEquals(lista.size(), resultado.getNumberOfElements());
		for (int i = 0; i < lista.size(); i++) {
			Assertions.assertEquals(lista.get(i), resultado.toList().get(i).toEntity());
		}
		Mockito.verify(repositorio, Mockito.times(1)).findByIncome(entrada, pageRequest);
	}

	/*• findById deveria
		◦ retornar um ClientDTO quando o id existir
		◦ lançar ResourceNotFoundException quando o id não existir*/

	@Test
	public void testarSeFindByIdRetornaClientDTOQuandoExistir() {
		Long idExistente = 1L;
		Optional<Client> cliente = Optional.of(new Client());
		Mockito.when(repositorio.findById(idExistente)).thenReturn(cliente);
		ClientDTO resultado = servico.findById(idExistente);

		Assertions.assertEquals(cliente.get(), resultado.toEntity());
		Mockito.verify(repositorio, Mockito.times(1)).findById(idExistente);
	}

	@Test
	public void testarSeFindByIdLancaExceptionQuandoClienteNaoExistir() {
		Long idInexistente = 50000L;
		Mockito.doThrow(ResourceNotFoundException.class).when(repositorio).findById(idInexistente);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.findById(idInexistente));
		Mockito.verify(repositorio, Mockito.times(1)).findById(idInexistente);
	}


	/*• update deveria
		◦ retornar um ClientDTO quando o id existir
		◦ lançar uma ResourceNotFoundException quando o id não existir*/

	@Test
	public void testarRetornoUpdateQuandoClienteDTOExistir() {
		Long idExistente = 1L;
		Client cliente = new Client(1L, "Conceição Evaristo", "10619244881", 1500.0, Instant.parse("2020-07-13T20:50:00Z"), 2);
		Mockito.when(repositorio.getOne(idExistente)).thenReturn(cliente);
		ClientDTO clienteModificado = new ClientDTO(1L, "Conceição Evaristo", "10619244881", 10000.0, Instant.parse("2020-07-13T20:50:00Z"), 2);
		Mockito.when(repositorio.save(clienteModificado.toEntity())).thenReturn(cliente);
		ClientDTO resultado = servico.update(idExistente, clienteModificado);

		Assertions.assertEquals(cliente, resultado.toEntity());
		Mockito.verify(repositorio, Mockito.times(1)).getOne(idExistente);
		Mockito.verify(repositorio, Mockito.times(1)).save(clienteModificado.toEntity());
	}

	@Test
	public void testarSeUpdateLancaExceptionQuandoClienteDTONaoExistir() {
		Long idInexistente = 50000L;
		Client cliente = new Client(1L, "Conceição Evaristo", "10619244881", 1500.0, Instant.parse("2020-07-13T20:50:00Z"), 2);
		Mockito.doThrow(ResourceNotFoundException.class).when(repositorio).getOne(idInexistente);
		ClientDTO clienteModificado = new ClientDTO(1L, "Conceição Evaristo", "10619244881", 10000.0, Instant.parse("2020-07-13T20:50:00Z"), 2);
		Mockito.when(repositorio.save(clienteModificado.toEntity())).thenReturn(cliente);

		Assertions.assertThrows(ResourceNotFoundException.class, ()-> servico.update(idInexistente, clienteModificado));
		Mockito.verify(repositorio, Mockito.times(1)).getOne(idInexistente);
		Mockito.verify(repositorio, Mockito.times(0)).save(clienteModificado.toEntity());
	}

	/*• insert deveria retornar um ClientDTO ao inserir um novo cliente*/

	@Test
	public void testarSeInsertRetornaClientDTOAoInserirNovoCliente() {
		Client cliente =  new Client(13L, "Maria", "12345698700", 20000.0, Instant.parse("2010-08-14T20:50:00Z"), 4);
		Mockito.when(repositorio.save(cliente)).thenReturn((cliente));
		ClientDTO resultado = servico.insert(new ClientDTO(cliente));

		Assertions.assertEquals(cliente, resultado.toEntity());
		Mockito.verify(repositorio, Mockito.times(1)).save(cliente);
	}


	/* Explicações e Exemplos feitos em aula: */

	/**
	 * Atividade A6
	 * Cenário de Teste
	 * Entrada:
	 * 		- idExistente: 2
	 * Resultado:
	 * 		- void
	 */

	@Test
	public void testarApagarRetornaNadaQuandoIDExiste() {
		//construir cenário
		//entrada
		Long idExistente = 2L;
		//configurar Mock
		Mockito.doNothing().when(repositorio).deleteById(idExistente);
		//executar o teste
		Assertions.assertDoesNotThrow(()->{servico.delete(idExistente);});
		//verificar as execuções da classe mock e de seus métodos
		Mockito.verify(repositorio, Mockito.times(1)).deleteById(idExistente);
	}

	/**
	 * Atividade A6
	 * Cenário de Teste : id não existe e retorna exception
	 * Entrada:
	 * 		- idExistente: 1000
	 * Resultado:
	 * 		- ResourceNotFoundException
	 */

	@Test
	public void testarApagarRetornaExceptionQuandoIDNaoExiste() {
		//construir cenário
		//entrada
		Long idNaoExistente = 1000L;
		//configurar Mock
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repositorio).deleteById(idNaoExistente);
		//executar o teste
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{servico.delete(idNaoExistente);});
		//verificar as execuções da classe mock e de seus métodos
		Mockito.verify(repositorio, Mockito.times(1)).deleteById(idNaoExistente);
	}

	/**
	 * Exemplo Extra
	 * Cenário de Teste : método findByIncomeGreaterThan retorna a página com clientes corretos
	 * Entrada:
	 * 		- Paginação:
	 * 			- Pagina = 1;
	 * 			- 2
	 * 			- Asc
	 * 			- Income
	 * 		- Income: 4800.00
	 * 		- Clientes:
	 Pagina: 0
	 {
	 "id": 7,
	 "name": "Jose Saramago",
	 "cpf": "10239254871",
	 "income": 5000.0,
	 "birthDate": "1996-12-23T07:00:00Z",
	 "children": 0
	 },

	 {
	 "id": 4,
	 "name": "Carolina Maria de Jesus",
	 "cpf": "10419244771",
	 "income": 7500.0,
	 "birthDate": "1996-12-23T07:00:00Z",
	 "children": 0
	 },
	 Pagina: 1
	 {
	 "id": 8,
	 "name": "Toni Morrison",
	 "cpf": "10219344681",
	 "income": 10000.0,
	 "birthDate": "1940-02-23T07:00:00Z",
	 "children": 0
	 }
	 * Resultado:
	 * 		Página não vazia
	 * 		Página contendo um cliente
	 * 		Página contendo o cliente da página 1
	 */
	@Test
	public void testarApagarRetornaExceptionQuandoIDNaoExiste2() {

		//construir cenário
		//entrada do método que sera testado
		//PageRequest.of(
		// qual Página deverá ser retornada (1 = a segunda página)
		// quantidade de objetos(client) que serão apresentados por página
		// ordem de apresentação crescente(ASC) decrescente(DESC)
		// campo que irá ordenar a página

		PageRequest pageRequest = PageRequest.of(1, 2, Direction.valueOf("ASC"), "income");
		Double entrada = 4800.00;

		//retorno que o método da classe mock deverá retornar
		List <Client> lista = new ArrayList<Client>();
		lista.add(new Client(8L, "Toni Morrsion", "10219344681", 10000.0, Instant.parse("1940-02-23T07:00:00Z"), 0));

		Page<Client> pag = new PageImpl<>(lista, pageRequest, 1);

		//configurar Mock
		Mockito.when(repositorio.findByIncomeGreaterThan(entrada, pageRequest)).thenReturn(pag);
		//executar o teste
		Page<ClientDTO> resultado = servico.findByIncomeGreaterThan(pageRequest, entrada);
		//verificar as execuções da classe mock e de seus métodos
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertEquals(1, resultado.getNumberOfElements());
		for (int i = 0; i < lista.size(); i++) {
			Assertions.assertEquals(lista.get(i), resultado.toList().get(i).toEntity());
		}
		Mockito.verify(repositorio, Mockito.times(1)).findByIncomeGreaterThan(entrada, pageRequest);
	}

	/** Exemplo Extra
	 * findByCpfLike deveria retornar uma página (e chamar o método findByCpfLike do repository)
	 * Cenário de teste
	 * Entradas necessárias:
	 *  - cpf : "%447%"
	 * 	- Uma PageRequest com os valores
	 * 		- page = 0
	 * 		- size = 3
	 * 		- direction = Direction.valueOf("ASC")
	 * 		- order = "name"
	 * 	- Lista de clientes esperada
	 {
	 "id": 4,
	 "name": "Carolina Maria de Jesus",
	 "cpf": "10419244771",
	 "income": 7500.0,
	 "birthDate": "1996-12-23T07:00:00Z",
	 "children": 0
	 },

	 Resultado Esperado:
	 - Página não vazia
	 - Página contendo um cliente
	 - Página contendo exatamente o cliente esperado.
	 */

	@Test
	public void testarSeBuscarClientesPorCPFLikeRetornaUmaPaginaComClientesComCPFQueContemTextoInformado(){
		String cpf = "%447%";
		PageRequest pageRequest = PageRequest.of(0, 3, Direction.valueOf("ASC"), "name");

		List <Client> listaClientes = new ArrayList<Client>();
		listaClientes.add(new Client(4L, "Carolina Maria de Jesus", "10419244771", 7500.0, Instant.parse("1996-12-23T07:00:00Z"), 0));

		Page<Client> clientes = new PageImpl<Client>(listaClientes);

		Mockito.when(repositorio.findByCpfLike(cpf, pageRequest)).thenReturn(clientes);
		Page<ClientDTO> resultado = servico.findByCpfLike(pageRequest, cpf);
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertEquals(listaClientes.size(), resultado.getNumberOfElements());
		for (int i = 0; i < listaClientes.size(); i++) {
			Assertions.assertEquals(listaClientes.get(i), resultado.toList().get(i).toEntity());
		}
		Mockito.verify(repositorio, Mockito.times(1)).findByCpfLike(cpf, pageRequest);
	}

}
