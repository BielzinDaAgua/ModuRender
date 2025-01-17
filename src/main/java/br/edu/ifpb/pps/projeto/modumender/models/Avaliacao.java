package br.edu.ifpb.pps.projeto.modumender.models;

import java.sql.Date;

public class Avaliacao {
    private int id;
    private int usuarioId;
    private int cursoId;
    private int nota;
    private String comentario;
    private Date dataAvaliacao; // Alterado de String para java.sql.Date

    public Avaliacao() {}

    public Avaliacao(int id, int usuarioId, int cursoId, int nota, String comentario, Date dataAvaliacao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
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

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
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
                ", usuarioId=" + usuarioId +
                ", cursoId=" + cursoId +
                ", nota=" + nota +
                ", comentario='" + comentario + '\'' +
                ", dataAvaliacao=" + dataAvaliacao +
                '}';
    }
}
