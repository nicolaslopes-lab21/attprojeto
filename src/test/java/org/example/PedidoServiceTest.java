package service;

import exception.ErroAoSalvarPedidoException;
import exception.EstoqueInsuficienteException;
import exception.ProdutoNaoEncontradoException;
import model.Pedido;
import model.Produto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.PedidoRepository;
import repository.ProdutoRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService service;

    @Test
    void deveCalcularValorTotalDoPedido() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        Pedido pedido = service.criarPedido(1L, 2);

        assertEquals(99.80, pedido.getValorTotal(), 0.001);
        verify(produtoRepository, times(1)).buscarPorId(1L);
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    @Test
    void deveVerificarInteracaoComAmbosRepositorios() {
        Produto produto = new Produto("Fone", 100.0, 5);
        when(produtoRepository.buscarPorId(2L)).thenReturn(produto);

        service.criarPedido(2L, 1);

        verify(produtoRepository, times(1)).buscarPorId(2L);
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    @Test
    void naoDeveCriarPedidoComQuantidadeZero() {
        assertThrows(IllegalArgumentException.class, () -> service.criarPedido(1L, 0));
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    void naoDeveCriarPedidoComQuantidadeNegativa() {
        assertThrows(IllegalArgumentException.class, () -> service.criarPedido(1L, -3));
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    void naoDeveCriarPedidoQuandoProdutoNaoExistir() {
        when(produtoRepository.buscarPorId(99L)).thenReturn(null);

        assertThrows(ProdutoNaoEncontradoException.class, () -> service.criarPedido(99L, 1));
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    void naoDeveCriarPedidoComEstoqueInsuficiente() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        assertThrows(EstoqueInsuficienteException.class, () -> service.criarPedido(1L, 11));
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    void deveCriarPedidoComQuantidadeIgualAoEstoque() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        Pedido pedido = service.criarPedido(1L, 10);

        assertEquals(499.00, pedido.getValorTotal(), 0.001);
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    @Test
    void naoDeveSalvarPedidoEmQualquerCenarioDeErro() {
        when(produtoRepository.buscarPorId(1L)).thenReturn(null);

        assertThrows(ProdutoNaoEncontradoException.class, () -> service.criarPedido(1L, 5));
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    void deveLancarErroAoSalvarPedidoQuandoBancoFalhar() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);
        doThrow(new RuntimeException("Falha na conexão"))
                .when(pedidoRepository).salvar(any(Pedido.class));

        assertThrows(ErroAoSalvarPedidoException.class, () -> service.criarPedido(1L, 2));
        verify(pedidoRepository).salvar(any(Pedido.class));
    }

    @Test
    void naoDeveChamarPedidoRepositorySeProdutoRepositoryFalhar() {
        when(produtoRepository.buscarPorId(1L)).thenThrow(new RuntimeException("Banco fora do ar"));

        assertThrows(RuntimeException.class, () -> service.criarPedido(1L, 2));
        verify(pedidoRepository, never()).salvar(any());
    }
}