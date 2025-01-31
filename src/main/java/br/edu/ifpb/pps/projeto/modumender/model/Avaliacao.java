package br.edu.ifpb.pps.projeto.modumender.model;

import br.edu.ifpb.pps.projeto.modumender.annotations.*;

import java.sql.Date;

@Entity(tableName = "avaliacoes")
public class Avaliacao {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(referencedColumnName = "id")   // FK para tabela "usuarios"
    private Usuario usuario;

    @ManyToOne(referencedColumnName = "id")   // FK para tabela "cursos"
    private Curso curso;

    @Column(name = "nota", nullable = false)
    @Min(0)
    @Max(10)  // nota deve estar entre 0 e 10
    private int nota;

    @Column(name = "comentario")
    @Length(min = 3, max = 200) // opcional, ex.: deve ter entre 3 e 200 chars se preenchido
    private String comentario;

    @Column(name = "data_avaliacao", nullable = false)
    private Date dataAvaliacao;

    public Avaliacao() {
    }

    public Avaliacao(int id, Usuario usuario, Curso curso, int nota, String comentario, Date dataAvaliacao) {
        this.id = id;
        this.usuario = usuario;
        this.curso = curso;
        this.nota = nota;
        this.comentario = comentario;
        this.dataAvaliacao = dataAvaliacao;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Curso getCurso() {
        return curso;
    }
    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public int getNota() {
        return nota;
    }
    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getDataAvaliacao() {
        return dataAvaliacao;
    }
    public void setDataAvaliacao(Date dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    @Override
    public String toString() {
        return "Avaliacao{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getId() : null) +
                ", curso=" + (curso != null ? curso.getId() : null) +
                ", nota=" + nota +
                ", comentario='" + comentario + '\'' +
                ", dataAvaliacao=" + dataAvaliacao +
                '}';
    }
}
