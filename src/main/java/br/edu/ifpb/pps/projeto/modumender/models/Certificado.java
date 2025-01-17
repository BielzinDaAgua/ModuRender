package br.edu.ifpb.pps.projeto.modumender.models;

import java.sql.Date;

public class Certificado {
    private int id;
    private int usuarioId;
    private int cursoId;
    private Date dataEmissao; // Alterado de String para java.sql.Date

    public Certificado() {}

    public Certificado(int id, int usuarioId, int cursoId, Date dataEmissao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
        this.dataEmissao = dataEmissao;
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

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    @Override
    public String toString() {
        return "Certificado{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", cursoId=" + cursoId +
                ", dataEmissao=" + dataEmissao +
                '}';
    }
}
