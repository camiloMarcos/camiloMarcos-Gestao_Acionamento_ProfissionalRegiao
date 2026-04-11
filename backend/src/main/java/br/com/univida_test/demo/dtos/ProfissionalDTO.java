package br.com.univida_test.demo.dtos;

import java.util.List;

import br.com.univida_test.demo.models.Bairro;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class ProfissionalDTO {

    private Integer id;

   @NotBlank(message = "Nome é obrigatório")
   @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres.")
   private String nome;
    
    @NotBlank (message = "Especialidade é obrigatória")
    @Size(min = 3, max = 50, message = "Especialidade deve ter entre 3 e 50 caracteres.")
    private String especialidade;

    @NotBlank(message = "Número do conselho é obrigatório")
    @Size(min = 3, max = 20, message = "Número do conselho deve ter entre 3 e 20 caracteres.")
    private String numeroConselho;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern( regexp = "^\\(?\\d{2}\\)?\\s?9\\d{4}-?\\d{4}$",  message = "Telefone inválido")
    private String telefone;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank
    @Size(min = 3, max = 200, message = "Endereço deve ter entre 3 e 200 caracteres.")
    private String endereco;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(min = 3, max = 100, message = "Cidade deve ter entre 3 e 100 caracteres.")
    private String cidade;


        private List<Bairro> bairrosAtendidos;

        public ProfissionalDTO() {
        }

        public ProfissionalDTO(Integer id, String nome, String especialidade, String numeroConselho,
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
    }


