package br.com.univida_test.demo.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Bairro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String cidade;
    private boolean perigo_Distante;

    @ManyToMany
    @JoinTable(name = "bairro_profissional",
            joinColumns = @JoinColumn(name = "bairro_id"),
            inverseJoinColumns = @JoinColumn(name = "profissional_id"))
    private List<Profissional> profissionais;

// Construtores
    public Bairro(){
    }

    public Bairro(Integer id, String nome, String cidade, boolean perigo_Distante) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
        this.perigo_Distante = perigo_Distante;
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

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public boolean isPerigo_Distante() {
        return perigo_Distante;
    }

    public void setPerigo_Distante(boolean perigo_Distante) {
        this.perigo_Distante = perigo_Distante;
    }

    public List<Profissional> getProfissionais() {
        return profissionais;
    }

    public void setProfissionais(List<Profissional> profissionais) {
        this.profissionais = profissionais;
    }
}


