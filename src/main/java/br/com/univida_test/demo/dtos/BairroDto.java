package br.com.univida_test.demo.dtos;

import java.util.List;

import br.com.univida_test.demo.models.Profissional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class BairroDto {


    private Integer id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    private String nome;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(min = 3, max = 50)
    private String cidade;

    @NotBlank(message = "Perigo Distante é obrigatório")
    private boolean perigo_Distante;


    private List<Profissional> profissionais;


        public BairroDto(){
        }


        public BairroDto(Integer id, String nome, String cidade, boolean perigo_Distante) {
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