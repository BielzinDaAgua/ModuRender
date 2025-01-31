package br.edu.ifpb.pps.projeto.modumender.model;

import br.edu.ifpb.pps.projeto.modumender.annotations.*;

import java.sql.Date;

@Entity(tableName = "matriculas")
public class Matricula {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(referencedColumnName = "id")  // FK para "usuarios"
    private Usuario usuario;

    @ManyToOne(referencedColumnName = "id")  // FK para "cursos"
    private Curso curso;

    @Column(name = "data_matricula", nullable = false)
    private Date dataMatricula;

    @Column(name = "status")
    private String status;

    public Matricula() {
    }

    public Matricula(int id, Usuario usuario, Curso curso, Date dataMatricula, String status) {
        this.id = id;
        this.usuario = usuario;
        this.curso = curso;
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
                ", usuario=" + (usuario != null ? usuario.getId() : null) +
                ", curso=" + (curso != null ? curso.getId() : null) +
                ", dataMatricula=" + dataMatricula +
                ", status='" + status + '\'' +
                '}';
    }
}
