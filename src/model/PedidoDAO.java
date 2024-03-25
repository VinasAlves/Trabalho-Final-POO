package model;

import Conexao.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.Pedido;

public class PedidoDAO {

    public void cadastrarPedido(Pedido pedido) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = ConnectionFactory.getConnection();
            String sql = "INSERT INTO pedido (id_prod,valortotal, quantidade) VALUES (?, ?, ?)";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, pedido.getProduto().getId());
            stmt.setDouble(2, pedido.getValortotal());
            stmt.setInt(3, pedido.getQuantidade());
            stmt.executeUpdate();
            System.out.println("Pedido cadastrado com sucesso.");
        } finally {
            closeResources(stmt, null, connection);
        }
    }

    public void atualizarPedido(Pedido pedido) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = ConnectionFactory.getConnection();
            String sql = "UPDATE pedido SET  valortotal = ?, quantidade = ? WHERE id = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setDouble(2, pedido.getValortotal());
            stmt.setInt(3, pedido.getQuantidade());
            stmt.setInt(4, pedido.getId());
            stmt.executeUpdate();
            System.out.println("Pedido atualizado com sucesso.");
        } finally {
            closeResources(stmt, null, connection);
        }
    }

    public void removerPedido(int idPedido) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = ConnectionFactory.getConnection();
            String sql = "DELETE FROM pedido WHERE id = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, idPedido);
            stmt.executeUpdate();
            System.out.println("Pedido removido com sucesso.");
        } finally {
            closeResources(stmt, null, connection);
        }
    }

    public List<Pedido> listarPedidos() throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM pedido";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

            List<Pedido> pedidos = new ArrayList<>();

            while (rs.next()) {
                Pedido pedido = new Pedido();
                int pedidoId = rs.getInt("idpedido");  // Nome da coluna corrigido
                Produto produto = buscarProdutoPorId(pedidoId);

                if (produto != null) {
                    pedido.setProduto(produto);
                    pedido.setValortotal(rs.getDouble("valortotal"));
                    pedido.setQuantidade(rs.getInt("quantidade"));
                    pedidos.add(pedido);
                } else {
                    System.out.println("Produto não encontrado para o pedido com ID: " + rs.getInt("idpedido"));
                }
            }

            return pedidos;
        } finally {
            closeResources(stmt, rs, connection);
        }
    }

    //FUNÇÃO DE TESTE
    public Pedido buscarPedidoPorId(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM pedido WHERE idpedido = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Pedido pedido = new Pedido();
                int produtoId = rs.getInt("idpedido");
                Produto produto = buscarProdutoPorId(produtoId);

                if (produto != null) {
                    pedido.setId(rs.getInt("idpedido"));
                    pedido.setProduto(produto);
                    pedido.setValortotal(rs.getDouble("valortotal"));
                    pedido.setQuantidade(rs.getInt("quantidade"));
                    return pedido;
                } else {
                    System.out.println("Produto não encontrado para o pedido com ID: " + id);
                    return null;
                }
            } else {
                System.out.println("Pedido não encontrado com ID: " + id);
                return null;
            }
        } finally {
            closeResources(stmt, rs, connection);
        }
    }

    // Método auxiliar para buscar um produto por ID
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
                System.out.println("ID não existente");
                return null;
            }
        } finally {
            closeResources(stmt, rs, connection);
        }
    }

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

  

}
