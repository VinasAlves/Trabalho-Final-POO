package model;

import Conexao.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProdutoDAO {

    private Scanner scanner = new Scanner(System.in);

    public void adiciona(Produto produto) throws SQLException {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;

        String sql = "INSERT INTO produto (nome_produto, quantidade_estoque, preco) VALUES (?, ?, ?)";

        try {
            if (!produtoExiste(produto.getNome_produto(), con)) {
                stmt = con.prepareStatement(sql);
                stmt.setString(1, produto.getNome_produto());
                stmt.setInt(2, produto.getQuantidade_estoque());
                stmt.setDouble(3, produto.getPreco());

                stmt.execute();
                System.out.println("Produto adicionado ao banco: " + produto.getNome_produto());
            } else {
                System.out.println("Produto já existe no banco: " + produto.getNome_produto());
            }
        } catch (SQLException e) {
            System.out.println("Não foi possível adicionar: " + e.getMessage());
            throw e; // lançar novamente a exceção após o tratamento local
        } finally {
            // Certifique-se de fechar os recursos no bloco finally para evitar vazamentos
            closeResources(stmt, null, con);
        }
    }

    public void removeProdutoPorNome(String nomeProduto) throws SQLException {
        Connection connection = null;
        PreparedStatement stmtEstoque = null;
        PreparedStatement stmtProduto = null;

        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);  // Inicia uma transação

            // Remove registros relacionados na tabela "estoque"
            String sqlEstoque = "DELETE FROM estoque WHERE id_produto IN (SELECT idproduto FROM produto WHERE nome_produto = ?)";
            stmtEstoque = connection.prepareStatement(sqlEstoque);
            stmtEstoque.setString(1, nomeProduto);
            stmtEstoque.executeUpdate();

            // Remove o produto na tabela "produto"
            String sqlProduto = "DELETE FROM produto WHERE nome_produto = ?";
            stmtProduto = connection.prepareStatement(sqlProduto);
            stmtProduto.setString(1, nomeProduto);
            stmtProduto.executeUpdate();

            // Comita a transação
            connection.commit();
        } catch (SQLException e) {
            // Em caso de erro, realiza o rollback da transação
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            // Certifique-se de fechar os recursos e retornar ao modo de commit automático
            if (connection != null) {
                connection.setAutoCommit(true);
            }
            closeResources(stmtEstoque, null, connection);
            closeResources(stmtProduto, null, null);  // A conexão é fechada no bloco try ou rollback
        }
    }

    public void removeProdutoPorId(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = ConnectionFactory.getConnection();

            String sql = "DELETE FROM produto WHERE idproduto = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Produto removido com sucesso.");
            } else {
                System.out.println("Produto não encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao remover produto por id: " + e.getMessage());
            throw e;  // lançar novamente a exceção após o tratamento local
        } finally {
            // Certifique-se de fechar os recursos no bloco finally para evitar vazamentos
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void atualizarProduto(Produto produtoAtualizado) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = ConnectionFactory.getConnection();
            String sql = "UPDATE produto SET nome_produto = ?, quantidade_estoque = ?, preco = ? WHERE idproduto = ?";
            stmt = connection.prepareStatement(sql);

            stmt.setString(1, produtoAtualizado.getNome_produto());
            stmt.setInt(2, produtoAtualizado.getQuantidade_estoque());
            stmt.setDouble(3, produtoAtualizado.getPreco());
            stmt.setInt(4, produtoAtualizado.getId()); // Supondo que você tenha um método getId() na classe Produto

            stmt.executeUpdate();
            System.out.println("Produto atualizado com sucesso.");

        } finally {
            closeResources(stmt, null, connection);
        }
    }

    public List<Produto> listarProdutos() throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM produto";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

            List<Produto> produtos = new ArrayList<>();

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("idproduto"));
                produto.setNome_produto(rs.getString("nome_produto"));
                produto.setQuantidade_estoque(rs.getInt("quantidade_estoque"));
                produto.setPreco(rs.getDouble("preco"));
                produtos.add(produto);
            }

            return produtos;
        } finally {
            closeResources(stmt, rs, connection);
        }
    }

    public void entradaNoEstoque(int idProduto, int quantidade) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = ConnectionFactory.getConnection();
            String sql = "UPDATE produto SET quantidade_estoque = quantidade_estoque + ? WHERE idproduto = ?";
            stmt = connection.prepareStatement(sql);

            stmt.setInt(1, quantidade);
            stmt.setInt(2, idProduto);

            stmt.executeUpdate();
            System.out.println("Entrada de produto no estoque realizada com sucesso.");
        } finally {
            closeResources(stmt, null, connection);
        }
    }

    public void saidaDoEstoque(int idProduto, int quantidade) throws SQLException, EstoqueInsuficienteException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = ConnectionFactory.getConnection();
            String sql = "SELECT quantidade_estoque FROM produto WHERE idproduto = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, idProduto);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int estoqueAtual = rs.getInt("quantidade_estoque");

                if (estoqueAtual >= quantidade) {
                    // Se houver estoque suficiente, realiza a saída
                    sql = "UPDATE produto SET quantidade_estoque = quantidade_estoque - ? WHERE idproduto = ?";
                    stmt = connection.prepareStatement(sql);
                    stmt.setInt(1, quantidade);
                    stmt.setInt(2, idProduto);
                    stmt.executeUpdate();
                    System.out.println("Saída de produto do estoque realizada com sucesso.");
                } else {
                    // Se o estoque for insuficiente, lança uma exceção
                    throw new EstoqueInsuficienteException("Estoque insuficiente para a saída desejada.");
                }
            } else {
                System.out.println("ID do produto não encontrado.");
            }
        } finally {
            closeResources(stmt, rs, connection);
        }
    }

    private boolean produtoExiste(String nomeProduto, Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM produto WHERE nome_produto = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, nomeProduto);
            rs = stmt.executeQuery();

            return rs.next();
        } finally {
            // Não fechar recursos aqui para que a conexão seja fechada apenas no final do método chamador
        }
    }

    private int quantidadeDisponivel(String nomeProduto, Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT quantidade FROM produto WHERE nome_produto = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, nomeProduto);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("quantidade");
            } else {
                return 0;
            }
        } finally {
            // Certifique-se de fechar os recursos no bloco finally para evitar vazamentos
            closeResources(stmt, rs, connection);
        }
    }

    // Método auxiliar para fechar os recursos (PreparedStatement, ResultSet e Connection)
    private void closeResources(PreparedStatement stmt, ResultSet rs, Connection connection) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Produto buscarProdutoPorId(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM produto WHERE idproduto = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("idproduto"));
                produto.setNome_produto(rs.getString("nome_produto"));
                produto.setQuantidade_estoque(rs.getInt("quantidade_estoque"));
                produto.setPreco(rs.getDouble("preco"));
                return produto;
            } else {
                System.out.println("ID nao existente");
                return null;
            }
        } finally {
            closeResources(stmt, rs, connection);
        }
    }

    public class EstoqueInsuficienteException extends Exception {

        public EstoqueInsuficienteException(String mensagem) {
            super(mensagem);
        }
    }

    public void atualizarEstoqueAposPedido(Pedido pedido) throws SQLException, EstoqueInsuficienteException {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = ConnectionFactory.getConnection();
            String sql = "UPDATE produto SET quantidade_estoque = ? WHERE idproduto = ?";
            stmt = connection.prepareStatement(sql);
            String updateDataModificacao = "UPDATE produto SET dataModificacao = CURRENT_TIMESTAMP WHERE idproduto = ?";
            PreparedStatement stmtDataModificacao = connection.prepareStatement(updateDataModificacao);
            
            // Calcula o novo estoque subtraindo a quantidade do pedido do estoque atual
            int estoqueAtual = pedido.getProduto().getQuantidade_estoque();
            int quantidadePedido = pedido.getQuantidade();

            if (estoqueAtual >= quantidadePedido) {
                int novoEstoque = estoqueAtual - quantidadePedido;

                stmt.setInt(1, novoEstoque);
                stmt.setInt(2, pedido.getProduto().getId());

                stmt.executeUpdate();
                System.out.println("Estoque atualizado com sucesso apos o pedido.");
            } else {
                throw new EstoqueInsuficienteException("Estoque insuficiente para realizar o pedido.");
            }
        } finally {
            closeResources(stmt, null, connection);
        }

    }

}
