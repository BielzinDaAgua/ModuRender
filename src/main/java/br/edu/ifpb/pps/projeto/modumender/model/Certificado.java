package br.edu.ifpb.pps.projeto.modumender.model;

import br.edu.ifpb.pps.projeto.modumender.annotations.*;

import java.sql.Date;

@Entity(tableName = "certificados")
public class Certificado {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(referencedColumnName = "id")  // FK para usuarios
    private Usuario usuario;

    @ManyToOne(referencedColumnName = "id")  // FK para cursos
    private Curso curso;

    @Column(name = "data_emissao", nullable = false)
    private Date dataEmissao;

    public Certificado() {
    }

    public Certificado(int id, Usuario usuario, Curso curso, Date dataEmissao) {
        this.id = id;
        this.usuario = usuario;
        this.curso = curso;
        this.dataEmissao = dataEmissao;
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
                ", usuario=" + (usuario != null ? usuario.getId() : null) +
                ", curso=" + (curso != null ? curso.getId() : null) +
                ", dataEmissao=" + dataEmissao +
                '}';
    }
}
