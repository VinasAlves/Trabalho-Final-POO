package view;

import java.util.Scanner;
import model.Pessoa;
import model.PessoaDAO;
import javax.swing.JOptionPane;

/**
 *
 * @author vinas
 */
public class Menus {

    Scanner s = new Scanner(System.in);

// ...
    public int LoginPage() {

        StringBuilder menu = new StringBuilder("");

        menu.append("\n===== Login =====");
        menu.append("\n1 - Fazer Pedido");
        menu.append("\n2 - Login");
        menu.append("\n3 - Cadastro");
        menu.append("\n0 - Sair");

        String userInput = JOptionPane.showInputDialog(null, menu.toString() + "\n-> ");
        return Integer.parseInt(userInput);
    }

    public Pessoa Logar(PessoaDAO p) {
        String login = JOptionPane.showInputDialog(null, "Login: ");
        String senha = JOptionPane.showInputDialog(null, "Senha: ");
        return p.buscaPessoaLogin(login, senha);

    }

    public Pessoa cadastrar() {
        Pessoa p = new Pessoa();

        p.setNome(JOptionPane.showInputDialog(null, "Nome completo:"));
        p.setLogin(JOptionPane.showInputDialog(null, "Insira seu login:"));
        p.setSenha(JOptionPane.showInputDialog(null, "Insira sua senha:"));

        return p;
    }

    public int menuPrincipal() {
        StringBuilder menu = new StringBuilder("");

        menu.append("\n\n");
        menu.append("\n====== MENU PRINCIPAL ======");
        menu.append("\n1 - PEDIDOS");
        menu.append("\n2 - ENTRADA DE PRODUTO");
        menu.append("\n3 - SAIDA DE PRODUTO");
        menu.append("\n0 - Deslogar");

        String userInput = JOptionPane.showInputDialog(null, menu.toString() + "\n-> ");

        // Certifique-se de validar a entrada do usuário para evitar exceções
        try {
            return Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            // Trate o caso em que o usuário não fornece um número válido
            return -1; // Valor de retorno indicando erro ou escolha inválida
        }
    }

}
