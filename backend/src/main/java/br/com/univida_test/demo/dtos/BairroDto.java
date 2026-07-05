package br.com.univida_test.demo.dtos;

import java.util.List;

import br.com.univida_test.demo.models.Profissional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BairroDto {

    private Integer id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(min = 3, max = 50, message = "Cidade deve ter entre 3 e 50 caracteres")
    private String cidade;

    // boolean não precisa de @NotBlank (sempre tem valor padrão false)
    private boolean perigoDistante;

    private List<Profissional> profissionais;

    public BairroDto() {
    }

    public BairroDto(Integer id, String nome, String cidade, boolean perigoDistante) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
        this.perigoDistante = perigoDistante;
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

    public boolean isPerigoDistante() {
        return perigoDistante;
    }

    public void setPerigoDistante(boolean perigoDistante) {
        this.perigoDistante = perigoDistante;
    }

    public List<Profissional> getProfissionais() {
        return profissionais;
    }

    public void setProfissionais(List<Profissional> profissionais) {
        this.profissionais = profissionais;
    }
}