/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Alunos
 */
public class Pedido {

    Produto produto = new Produto();
    private int id;
    private double valortotal;
    private int quantidade;

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setValortotal(double valortotal) {
        this.valortotal = valortotal;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    //metodos para atualizaçao e verificação de pedidos

    public Pedido(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.valortotal = getValortotal();
    }

    public Pedido() {

    }

    public double getValortotal() {
        return quantidade * produto.getPreco();
    }

    public void salvarPedido() {
        if (verificarEstoque()) {
            // Lógica para salvar o pedido no banco de dados
            System.out.println("Pedido salvo com sucesso!");
        } else {
            System.out.println("Não há estoque suficiente para o produto solicitado.");
        }
    }

    private boolean verificarEstoque() {
        return produto.getQuantidade_estoque() >= quantidade;
    }
}
