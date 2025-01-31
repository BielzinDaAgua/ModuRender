package br.edu.ifpb.pps.projeto.modumender.model;

import br.edu.ifpb.pps.projeto.modumender.annotations.Column;
import br.edu.ifpb.pps.projeto.modumender.annotations.Entity;
import br.edu.ifpb.pps.projeto.modumender.annotations.Id;
import br.edu.ifpb.pps.projeto.modumender.annotations.Length;

@Entity(tableName = "usuarios")
public class Usuario {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "nome", nullable = false)
    @Length(min = 3, max = 50) // Nome deve ter entre 3 e 50 caracteres
    private String nome;

    @Column(name = "email", nullable = false)
    @Length(min = 5, max = 100) // Email deve ter entre 5 e 100 caracteres
    private String email;

    @Column(name = "senha", nullable = false)
    @Length(min = 6, max = 20) // Senha deve ter 6 a 20 caracteres
    private String senha;

    @Column(name = "tipo_usuario", nullable = false)
    @Length(min = 5, max = 9) // "ALUNO" (5) ou "INSTRUTOR" (9)
    private String tipoUsuario; // "ALUNO" ou "INSTRUTOR"

    public Usuario() {
    }

    public Usuario(int id, String nome, String email, String senha, String tipoUsuario) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }
    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                '}';
    }
}
