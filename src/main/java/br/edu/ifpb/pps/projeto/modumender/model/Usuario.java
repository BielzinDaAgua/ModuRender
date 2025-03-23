package br.edu.ifpb.pps.projeto.modumender.model;

import br.edu.ifpb.pps.projeto.modumender.annotations.*;

@Entity(tableName = "usuarios")
public class Usuario {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotEmpty // custom: não pode ser vazio
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotEmpty // exige string não vazia
    @Email    // exige ter "@"
    @Column(name = "email", nullable = false)
    private String email;

    @PasswordComplex(min=8, max=20, requireLetters=true, requireDigits=true)
    @Column(name = "senha", nullable = false)
    private String senha;

    // por exemplo, "ALUNO" ou "INSTRUTOR"
    @NotEmpty
    @Column(name = "tipo_usuario", nullable = false)
    private String tipoUsuario;
    public Usuario() {
    }

    public Usuario(Integer id, String nome, String email, String senha, String tipoUsuario) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
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
