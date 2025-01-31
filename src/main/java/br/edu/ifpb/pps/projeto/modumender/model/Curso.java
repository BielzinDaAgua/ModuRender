package br.edu.ifpb.pps.projeto.modumender.model;

import br.edu.ifpb.pps.projeto.modumender.annotations.Column;
import br.edu.ifpb.pps.projeto.modumender.annotations.Entity;
import br.edu.ifpb.pps.projeto.modumender.annotations.Id;

import java.sql.Date;

@Entity(tableName = "cursos")
public class Curso {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "preco", nullable = false)
    private double preco;

    @Column(name = "data_criacao", nullable = false)
    private Date dataCriacao;

    // Se quiser relacionar instrutor, poderia usar:
    // @ManyToOne(referencedColumnName = "id")
    // private Usuario instrutor;
    // Mas, aqui vamos apenas manter o ID ou se preferir, troque para ManyToOne.
    @Column(name = "instrutor_id", nullable = false)
    private int instrutorId;

    public Curso() {
    }

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
