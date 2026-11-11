package service;

import exception.ErroAoSalvarProdutoException;
import exception.ProdutoNaoEncontradoException;
import model.Produto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.ProdutoRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    @Test
    void deveSalvarProdutoComSucesso() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(repository.salvar(produto)).thenReturn(produto);

        Produto resultado = service.salvar(produto);

        assertNotNull(resultado);
        assertEquals("Mouse", resultado.getNome());
        verify(repository).salvar(produto);
    }

    @Test
    void naoDeveSalvarProdutoComPrecoNegativo() {
        Produto produto = new Produto("Mouse", -10, 5);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(produto));
        verify(repository, never()).salvar(any());
    }

    @Test
    void naoDeveSalvarProdutoComPrecoZero() {
        Produto produto = new Produto("Teclado", 0.0, 5);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(produto));
        verify(repository, never()).salvar(any());
    }

    @Test
    void naoDeveSalvarProdutoComNomeVazio() {
        Produto produto = new Produto("", 100.0, 5);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(produto));
        verify(repository, never()).salvar(any());
    }

    @Test
    void naoDeveSalvarProdutoComNomeNulo() {
        Produto produto = new Produto(null, 100.0, 5);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(produto));
        verify(repository, never()).salvar(any());
    }

    @Test
    void naoDeveSalvarProdutoComQuantidadeNegativa() {
        Produto produto = new Produto("Monitor", 500.0, -1);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(produto));
        verify(repository, never()).salvar(any());
    }

    @Test
    void deveSalvarProdutoComEstoqueZerado() {
        Produto produto = new Produto("Impressora", 350.0, 0);
        when(repository.salvar(produto)).thenReturn(produto);

        Produto resultado = service.salvar(produto);

        assertEquals(0, resultado.getQuantidade());
        verify(repository).salvar(produto);
    }

    @Test
    void deveRetornarProdutoQuandoEncontrado() {
        Produto produto = new Produto("Webcam", 150.0, 8);
        when(repository.buscarPorId(1L)).thenReturn(produto);

        Produto resultado = service.buscarPorId(1L);

        assertEquals("Webcam", resultado.getNome());
        verify(repository).buscarPorId(1L);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        when(repository.buscarPorId(99L)).thenReturn(null);

        assertThrows(ProdutoNaoEncontradoException.class, () -> service.buscarPorId(99L));
        verify(repository).buscarPorId(99L);
    }

    @Test
    void deveLancarErroQuandoRepositoryFalharAoSalvar() {
        Produto produto = new Produto("Teclado", 129.90, 5);
        doThrow(new RuntimeException("Falha na conexão"))
                .when(repository).salvar(any(Produto.class));

        assertThrows(ErroAoSalvarProdutoException.class, () -> service.salvar(produto));
        verify(repository).salvar(any(Produto.class));
    }

    @Test
    void deveSalvarProdutoComPrecoLimiteMinimoValido() {
        Produto produto = new Produto("Bala", 0.01, 100);
        when(repository.salvar(produto)).thenReturn(produto);

        Produto resultado = service.salvar(produto);

        assertEquals(0.01, resultado.getPreco());
        verify(repository).salvar(produto);
    }

    @Test
    void naoDeveSalvarProdutoComNomeEmBranco() {
        Produto produto = new Produto("   ", 50.0, 5);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(produto));
        verify(repository, never()).salvar(any());
    }
}