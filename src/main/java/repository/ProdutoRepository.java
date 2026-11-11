package repository;

import model.Produto;

public interface ProdutoRepository {
    Produto salvar(Produto produto);
    Produto buscarPorId(Long id);
}