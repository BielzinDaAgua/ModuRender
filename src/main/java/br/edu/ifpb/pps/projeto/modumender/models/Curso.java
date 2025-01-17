package br.edu.ifpb.pps.projeto.modumender.models;

import java.sql.Date;

public class Curso {
    private int id;
    private String titulo;
    private String descricao;
    private double preco;
    private Date dataCriacao; // Alterado de String para java.sql.Date
    private int instrutorId;

    public Curso() {}

    public Curso(int id, String titulo, String descricao, double preco, Date dataCriacao, int instrutorId) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.preco = preco;
        this.dataCriacao = dataCriacao;
        this.instrutorId = instrutorId;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public int getInstrutorId() {
        return instrutorId;
    }

    public void setInstrutorId(int instrutorId) {
        this.instrutorId = instrutorId;
    }

    @Override
    public String toString() {
        return "Curso{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco=" + preco +
                ", dataCriacao=" + dataCriacao +
                ", instrutorId=" + instrutorId +
                '}';
    }
}
