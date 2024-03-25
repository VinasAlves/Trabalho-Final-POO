package provarec;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;
import model.Pedido;
import model.PedidoDAO;
import model.Pessoa;
import model.PessoaDAO;
import model.Produto;
import model.ProdutoDAO;
import model.RelatorioPedido;
import model.Utils;
import view.Menus;

public class ProvaREC {

    Menus menu = new Menus();
    PessoaDAO pessoaDAO = new PessoaDAO();
    static ProdutoDAO produtoDAO = new ProdutoDAO();
    PedidoDAO pedidoDAO = new PedidoDAO();

    public ProvaREC() throws SQLException, ProdutoDAO.EstoqueInsuficienteException {

        //INICIALIZAR A LISTA DE PRODUTOS DISPONIVEIS
        List<Produto> produtosParaAdicionar = inicializarListaDeProdutos();

        for (Produto produto : produtosParaAdicionar) {
            try {
                produtoDAO.adiciona(produto);
                System.out.println("Produto adicionado ao banco: " + produto.getNome_produto());
            } catch (SQLException e) {
                System.out.println("Erro ao adicionar o produto: " + e.getMessage());
            }
        }

        //COMEÇAR PROGRAMA
        int opc;

        do {
            opc = menu.LoginPage();
            switch (opc) {
                case 1:
                    List<Produto> produtos = produtoDAO.listarProdutos();

                    StringBuilder mensagem = new StringBuilder();

                    for (Produto produto : produtos) {
                        mensagem.append("ID: ").append(produto.getId()).append("\n");
                        mensagem.append("Nome: ").append(produto.getNome_produto()).append("\n");
                        mensagem.append("Preco: ").append(produto.getPreco()).append("\n");
                        mensagem.append("-----").append("\n");
                    }
                    JOptionPane.showMessageDialog(null, mensagem.toString(), "Informações dos Produtos", JOptionPane.INFORMATION_MESSAGE);

                    String idStr = JOptionPane.showInputDialog(null, "\nFazer Pedido - Digite o ID do produto desejado: ");
                    int id = Integer.parseInt(idStr);

                    String qtdStr = JOptionPane.showInputDialog(null, "Digite a quantidade desejada: ");
                    int qtd = Integer.parseInt(qtdStr);

                    // Verificar se o produto existe
                    Produto produtoSelecionado = produtoDAO.buscarProdutoPorId(id);

                    if (produtoSelecionado != null) {
                        // Verificar se há estoque suficiente
                        if (qtd <= produtoSelecionado.getQuantidade_estoque()) {
                            // Criar o pedido
                            Pedido novoPedido = new Pedido();
                            novoPedido.setProduto(produtoSelecionado);
                            novoPedido.setQuantidade(qtd);
                            novoPedido.getValortotal(); // Certifique-se de calcular o valor total

                            // Adicionar o pedido ao banco de dados
                            try {
                                pedidoDAO.cadastrarPedido(novoPedido);
                                produtoDAO.atualizarEstoqueAposPedido(novoPedido);
                                JOptionPane.showMessageDialog(null, "Pedido realizado com sucesso!");
                            } catch (SQLException e) {
                                JOptionPane.showMessageDialog(null, "Erro ao registrar o pedido: " + e.getMessage());
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Estoque insuficiente. Pedido não realizado.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Produto não encontrado. Pedido não realizado.");
                    }

                    break;

                case 2:
                    Pessoa Plogada = menu.Logar(pessoaDAO);
                    if (Plogada != null) {
                        JOptionPane.showMessageDialog(null, "Logado com sucesso.");
                        Utils.setPessoaLogada(Plogada);
                        menuPrincipal();

                    } else {
                        JOptionPane.showMessageDialog(null, "Usuario nao encontrado");
                    }
                    break;
                case 3:
                    pessoaDAO.adiciona(menu.cadastrar());
                    JOptionPane.showMessageDialog(null, "Usuario cadastrado com sucesso");

                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "Saindo...");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opcao invalida");
            }
        } while (opc != 0);

    }

    public static void main(String[] args) throws SQLException, ProdutoDAO.EstoqueInsuficienteException {
        // TODO code application logic here
        new ProvaREC();

    }

    void menuPrincipal() throws SQLException, ProdutoDAO.EstoqueInsuficienteException {
        int opc = 0;
        do {

            String input0 = JOptionPane.showInputDialog(null, "\n====== MENU ADMINISTRADOR ======\n1 - CRUD PEDIDOS\n2 - CRUD ENTRADA DE PRODUTOS\n3 - CRUD SAIDA DE PEDIDOS\n4 - CRUD PRODUTOS\n5 - RELATORIO\n0 - Sair\n-> ");
            opc = Integer.parseInt(input0);
////////////////////PEDIDOS///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            switch (opc) {
                case 1:
                    String input1 = JOptionPane.showInputDialog(null, "\n====== CRUD Entrada de Produtos ======\n1 - Mostrar Pedidos\n0 - Sair\n-> ");
                    int opc1 = Integer.parseInt(input1);

                    switch (opc1) {
                        case 1:
                            //MEXER AQUI
                            List<Pedido> pedidos = pedidoDAO.listarPedidos();

                            StringBuilder mensagem = new StringBuilder();

                            for (Pedido pedido : pedidos) {
                                mensagem.append("ID: ").append(pedido.getId()).append("\n");
                                mensagem.append("Valor Total ").append(pedido.getValortotal()).append("\n");
                                mensagem.append("Quantidade: ").append(pedido.getQuantidade()).append("\n");
                                mensagem.append("-----").append("\n");
                            }
                            JOptionPane.showMessageDialog(null, mensagem.toString(), "Informações dos Pedidos", JOptionPane.INFORMATION_MESSAGE);

                            break;
                        case 0:
                            System.out.println("Saindo...");
                            break;
                    }
                    break;
////////////////////ENTRADA DE PRODUTOS/ESTOQUE///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////                    
                case 2:

                    String input2 = JOptionPane.showInputDialog(null, "\n====== CRUD Entrada de Produtos ======\n1 - Adicionar ao Estoque\n2 - Listar\n0 - Sair\n-> ");
                    int opc2 = Integer.parseInt(input2);
                    // Entrada de Produto

                    switch (opc2) {

                        case 1:
                            //MOSTRAR ITENS E ESTOQUE
                            List<Produto> mp = produtoDAO.listarProdutos();
                            StringBuilder mensagem0 = new StringBuilder();

                            for (Produto produto : mp) {
                                mensagem0.append("ID: ").append(produto.getId()).append("\n");
                                mensagem0.append("Nome: ").append(produto.getNome_produto()).append("\n");
                                mensagem0.append("Quantidade: ").append(produto.getQuantidade_estoque()).append("\n");
                                mensagem0.append("-----").append("\n");
                            }

                            JOptionPane.showMessageDialog(null, mensagem0.toString(), "Produtos Em estoque", JOptionPane.INFORMATION_MESSAGE);
                            String idEntradaStr = JOptionPane.showInputDialog(null, "Digite o ID do produto: ");
                            int idEntrada = Integer.parseInt(idEntradaStr);

                            String qtdentradaStr = JOptionPane.showInputDialog(null, "Digite a quantidade de entrada: ");
                            int quantidadeEntrada = Integer.parseInt(qtdentradaStr);

                            produtoDAO.entradaNoEstoque(idEntrada, quantidadeEntrada);
                            break;

                        case 2:
                            List<Produto> produtos = produtoDAO.listarProdutos();

                            StringBuilder mensagem1 = new StringBuilder();

                            for (Produto produto : produtos) {
                                mensagem1.append("ID: ").append(produto.getId()).append("\n");
                                mensagem1.append("Nome: ").append(produto.getNome_produto()).append("\n");
                                mensagem1.append("Quantidade: ").append(produto.getQuantidade_estoque()).append("\n");
                                mensagem1.append("-----").append("\n");
                            }
                            JOptionPane.showMessageDialog(null, mensagem1.toString(), "Produtos em Estoque", JOptionPane.INFORMATION_MESSAGE);

                            break;

                        case 0:
                            System.out.println("Saindo...");
                            break;
                    }

                    break;
////////////////////SAIDA DE PRODUTOS/ESTOQUE///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                case 3:

                    String input3 = JOptionPane.showInputDialog(null, "\n====== CRUD Saida de Produtos ======\n1 - Remover do Estoque\n2 - Listar Estoque\n0 - Sair\n-> ");
                    int opc3 = Integer.parseInt(input3);
                    // Entrada de Produto

                    switch (opc3) {
                        case 1:
                            List<Produto> mp = produtoDAO.listarProdutos();

                            StringBuilder mensagem1 = new StringBuilder();

                            for (Produto produto : mp) {
                                mensagem1.append("ID: ").append(produto.getId()).append("\n");
                                mensagem1.append("Nome: ").append(produto.getNome_produto()).append("\n");
                                mensagem1.append("Quantidade: ").append(produto.getQuantidade_estoque()).append("\n");
                                mensagem1.append("-----").append("\n");
                            }
                            JOptionPane.showMessageDialog(null, mensagem1.toString(), "Produtos em Estoque", JOptionPane.INFORMATION_MESSAGE);
                            String idSaidaStr = JOptionPane.showInputDialog(null, "Digite o ID do produto: ");
                            int idSaida = Integer.parseInt(idSaidaStr);

                            String qtdsaidaStr = JOptionPane.showInputDialog(null, "Digite a quantidade de Saida: ");
                            int quantidadeSaida = Integer.parseInt(qtdsaidaStr);

                            produtoDAO.saidaDoEstoque(idSaida, quantidadeSaida);

                            break;
                        case 2:
                            List<Produto> produtos = produtoDAO.listarProdutos();

                            StringBuilder mensagem = new StringBuilder();

                            for (Produto produto : produtos) {
                                mensagem.append("ID: ").append(produto.getId()).append("\n");
                                mensagem.append("Nome: ").append(produto.getNome_produto()).append("\n");
                                mensagem.append("Quantidade: ").append(produto.getQuantidade_estoque()).append("\n");
                                mensagem.append("-----").append("\n");
                            }
                            JOptionPane.showMessageDialog(null, mensagem.toString(), "Informações dos Produtos", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        case 0:
                            System.out.println("Saindo...");
                            break;
                    }

                    break;
////////////////////PRODUTOS///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                case 4:
                    String input4 = JOptionPane.showInputDialog(null, "\n====== CRUD Produtos ======\n1 - Adicionar Produto\n2 - Listar Produtos\n3 - Excluir Produto\n4 - Alterar Produto\n0 - Sair\n-> ");
                    int opc4 = Integer.parseInt(input4);

                    switch (opc4) {
                        //ADICIONA PRODUTOS
                        case 1:
                            String nomeProduto = JOptionPane.showInputDialog(null, "Digite o nome do produto que deseja adicionar:");

                            String qtdStr = JOptionPane.showInputDialog(null, "Quantidade do produto: ");
                            int qtd = Integer.parseInt(qtdStr);

                            String precoStr = JOptionPane.showInputDialog(null, "Preco: ");
                            double preco = Double.parseDouble(precoStr);

                            Produto pp = new Produto(nomeProduto, qtd, preco);
                            produtoDAO.adiciona(pp);
                            break;
                        case 2:
                            //MOSTRA PRODUTOS
                            List<Produto> produtos = produtoDAO.listarProdutos();

                            StringBuilder mensagem = new StringBuilder();

                            for (Produto produto : produtos) {
                                mensagem.append("ID: ").append(produto.getId()).append("\n");
                                mensagem.append("Nome: ").append(produto.getNome_produto()).append("\n");
                                mensagem.append("Preco: ").append(produto.getPreco()).append("\n");
                                mensagem.append("Quantidade: ").append(produto.getQuantidade_estoque()).append("\n");
                                mensagem.append("-----").append("\n");
                            }
                            JOptionPane.showMessageDialog(null, mensagem.toString(), "Informações dos Produtos", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        case 3:
                            //REMOVE PRODUTOS
                            String nmProdRemover = JOptionPane.showInputDialog(null, "Digite o nome do produto que deseja Remover: ");
                            produtoDAO.removeProdutoPorNome(nmProdRemover);
                            break;
                        case 4:
                            //ALTERA PRODUTOS
                            List<Produto> prodAltera = produtoDAO.listarProdutos();

                            StringBuilder msg = new StringBuilder();

                            for (Produto produto : prodAltera) {
                                msg.append("ID: ").append(produto.getId()).append("\n");
                                msg.append("Nome: ").append(produto.getNome_produto()).append("\n");
                                msg.append("Preco: ").append(produto.getPreco()).append("\n");
                                msg.append("Quantidade: ").append(produto.getQuantidade_estoque()).append("\n");
                                msg.append("-----").append("\n");
                            }
                            JOptionPane.showMessageDialog(null, msg.toString(), "Informações dos Produtos", JOptionPane.INFORMATION_MESSAGE);
                            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            String idAlteraStr = JOptionPane.showInputDialog(null, "ID do produto a ser alterado: ");
                            int idAltera = Integer.parseInt(idAlteraStr);

                            String nomeAltera = JOptionPane.showInputDialog(null, "Digite o nome do produto que deseja alterar:");

                            String qtdAlteraStr = JOptionPane.showInputDialog(null, "Quantidade do produto: ");
                            int qtdAltera = Integer.parseInt(qtdAlteraStr);

                            String precoAlteraStr = JOptionPane.showInputDialog(null, "Preco: ");
                            double precoAltera = Double.parseDouble(precoAlteraStr);

                            Produto produtoParaAtualizar = produtoDAO.buscarProdutoPorId(idAltera);

                            produtoParaAtualizar.setNome_produto(nomeAltera); // Substitua "Novo Nome" pelo novo nome desejado
                            produtoParaAtualizar.setQuantidade_estoque(qtdAltera); // Substitua 100 pela nova quantidade desejada
                            produtoParaAtualizar.setPreco(precoAltera); // Substitua 25.0 pelo novo preço desejado

                            try {
                                produtoDAO.atualizarProduto(produtoParaAtualizar);
                                System.out.println("Produto atualizado com sucesso.");
                            } catch (SQLException e) {
                                System.out.println("Erro ao atualizar o produto: " + e.getMessage());
                            }

                            break;
                    }
                    break;
                //RELATORIO PEDIDOS
                case 5:

                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;

            }

        } while (opc != 0);

    }

    private static List<Produto> inicializarListaDeProdutos() {
        List<Produto> listaDeProdutos = new ArrayList<>();

        listaDeProdutos.add(new Produto("Colher", 50, 7.0));
        listaDeProdutos.add(new Produto("Garfo", 30, 9.5));
        listaDeProdutos.add(new Produto("Faca", 40, 5.75));
        listaDeProdutos.add(new Produto("Prato", 20, 16.0));
        listaDeProdutos.add(new Produto("Panela", 25, 41.99));

        return listaDeProdutos;
    }

}
