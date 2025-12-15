package br.com.univida_test.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String especialidade;
    private String numeroConselho;
    private String telefone;
    private String email;
    private String endereco;
    private String cidade;

    @JsonIgnore
    @ManyToMany(mappedBy = "profissionais")
    private List<Bairro> bairrosAtendidos = new ArrayList<>();

    public Profissional() {
    }


    public Profissional(Integer id, String nome, String especialidade, String numeroConselho,
                            String telefone, String email, String endereco, String cidade, List<Bairro> bairrosAtendidos) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.numeroConselho = numeroConselho;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.cidade = cidade;
        this.bairrosAtendidos = bairrosAtendidos;
    }

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

    public String getEspecialidade() {
        return especialidade;
    }
    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getNumeroConselho() {
        return numeroConselho;
    }
    public void setNumeroConselho(String numeroConselho) {
        this.numeroConselho = numeroConselho;
    }

    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public List<Bairro> getBairrosAtendidos() {
        return bairrosAtendidos;
    }
    public void setBairrosAtendidos(List<Bairro> bairrosAtendidos) {
        this.bairrosAtendidos = bairrosAtendidos;
    }

   // Métodos para adicionar bairro ao profissional

    public void addBairro(Bairro bairro) {
        this.bairrosAtendidos.add(bairro);
        bairro.getProfissionais().add(this);
    }

}