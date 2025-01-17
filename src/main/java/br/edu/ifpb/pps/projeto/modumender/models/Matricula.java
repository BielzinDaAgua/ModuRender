package br.edu.ifpb.pps.projeto.modumender.models;

import java.sql.Date;

public class Matricula {
    private int id;
    private int usuarioId;
    private int cursoId;
    private Date dataMatricula; // Alterado de String para java.sql.Date
    private String status;

    public Matricula() {}

    public Matricula(int id, int usuarioId, int cursoId, Date dataMatricula, String status) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
        this.dataMatricula = dataMatricula;
        this.status = status;
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

    public Date getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(Date dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Matricula{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", cursoId=" + cursoId +
                ", dataMatricula=" + dataMatricula +
                ", status='" + status + '\'' +
                '}';
    }
}
