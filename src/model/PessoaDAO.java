package model;

import Conexao.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Pessoa;

/**
 *
 * @author vinas
 */
public class PessoaDAO {

    Pessoa[] pessoas = new Pessoa[5];

    public Pessoa buscaPessoaLogin(String login, String senha) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = ConnectionFactory.getConnection();

            String sql = "SELECT * FROM pessoa WHERE login = ? AND senha = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, senha);

            rs = stmt.executeQuery();

            if (rs.next()) {
                Pessoa pessoa = new Pessoa();
                pessoa.setId(rs.getInt("idpessoa")); 
                pessoa.setNome(rs.getString("nome"));
                pessoa.setLogin(rs.getString("login"));
                pessoa.setSenha(rs.getString("senha"));

                return pessoa;
            } else {
                // Não encontrou a pessoa com o login e senha fornecidos
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pessoa por login: " + e.getMessage());
            return null;
        } finally {
            // Certifique-se de fechar os recursos no bloco finally para evitar vazamentos
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

    public void adiciona(Pessoa pessoa) throws SQLException {

        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;

        String sql = "INSERT INTO pessoa (nome,login,senha) VALUES (?, ?, ?)";

        if (!pessoaExiste(pessoa.getLogin(), con)) {
            try {
                stmt = con.prepareStatement(sql);
                stmt.setString(1, pessoa.getNome());
                stmt.setString(2, pessoa.getLogin());
                stmt.setString(3, pessoa.getSenha());

                stmt.execute();
            } catch (SQLException e) {
                System.out.println("Nao foi possivel adicionar: " + e.getMessage());
                throw e;  // lançar novamente a exceção após o tratamento local
            }
        }
    }

    private boolean pessoaExiste(String login, Connection con) throws SQLException {
        String sql = "SELECT 1 FROM pessoa WHERE login = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retorna true se o usuário existe
            }
        }
    }

    public void remover(Pessoa pessoa) {
        String sql = "delete from pessoa where idpessoa = ?";

        try (Connection connection = new ConnectionFactory().getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, pessoa.getId());

            stmt.execute();

            System.out.println("Pessoa excluída com sucesso.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
