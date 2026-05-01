package br.com.univida_test.demo.dtos.response;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * DTO: PROFISSIONAL COM BAIRROS - RESPONSE
 * ============================================================================
 * 
 * Representa um profissional completo com sua lista de bairros atendidos.
 * 
 * Responsabilidades:
 * - Retornar dados completos do profissional
 * - Retornar lista de bairros que o profissional atende
 * - Evitar serialização circular (usa BairroResumoDTO, não Bairro completo)
 * - Servir como resposta padrão para operações de leitura
 * 
 * Caso de Uso:
 * Quando uma operação retorna um profissional com seus bairros associados:
 * - GET /api/profissional-bairro/{profissionalId}/bairros
 * - POST /api/profissional-bairro/criar-com-bairros (resposta)
 * 
 * Estrutura:
 * - Dados principais do profissional (ID, nome, especialidade, etc)
 * - Lista de bairros resumidos (apenas ID, nome, cidade)
 * 
 * Problema Evitado:
 * Se retornássemos Profissional completo com lista de Bairro completos,
 * cada Bairro teria lista de Profissional, criando referência circular:
 * 
 * ❌ Profissional → Bairro → Profissional → Bairro → ... (CIRCULAR!)
 * ✅ Profissional → BairroResumoDTO → (sem referência de volta)
 * 
 * Exemplo de JSON Retornado:
 * ```json
 * {
 *     "id": 5,
 *     "nome": "Dr. João Silva",
 *     "especialidade": "Cardiologia",
 *     "numeroConselho": "123456-SP",
 *     "telefone": "(11) 98765-4321",
 *     "email": "joao@clinica.com",
 *     "endereco": "Rua das Flores, 100",
 *     "cidade": "São Paulo",
 *     "bairrosAtendidos": [
 *         {"id": 1, "nome": "Centro", "cidade": "São Paulo"},
 *         {"id": 3, "nome": "Pinheiros", "cidade": "São Paulo"},
 *         {"id": 5, "nome": "Vila Mariana", "cidade": "São Paulo"}
 *     ]
 * }
 * ```
 * 
 * ============================================================================
 */
public class ProfissionalComBairrosDTO {

    /**
     * ID único do profissional.
     * Gerado pelo banco de dados (JPA).
     */
    private Integer id;

    /**
     * Nome completo do profissional.
     */
    private String nome;

    /**
     * Especialidade médica ou profissional.
     * Exemplo: "Cardiologia", "Neurologia", "Fisioterapia"
     */
    private String especialidade;

    /**
     * Número de registro no conselho profissional.
     * Exemplo: "123456-SP", "CREMESP12345"
     */
    private String numeroConselho;

    /**
     * Telefone para contato do profissional.
     */
    private String telefone;

    /**
     * E-mail para contato do profissional.
     */
    private String email;

    /**
     * Endereço completo do profissional.
     */
    private String endereco;

    /**
     * Cidade onde o profissional reside/trabalha.
     */
    private String cidade;

    /**
     * Lista de bairros que o profissional atende.
     * 
     * Importante:
     * - Usa BairroResumoDTO (não Bairro completo)
     * - Cada BairroResumoDTO contém apenas: ID, nome, cidade
     * - Evita referência circular (Bairro não contém lista de Profissional)
     * - Inicializado com ArrayList para evitar NullPointerException
     * 
     * Exemplo:
     * [
     *     {"id": 1, "nome": "Centro", "cidade": "São Paulo"},
     *     {"id": 3, "nome": "Pinheiros", "cidade": "São Paulo"}
     * ]
     */
    private List<BairroResumoDTO> bairrosAtendidos = new ArrayList<>();

    // ========================================================================
    // CONSTRUTORES
    // ========================================================================

    /**
     * Construtor padrão sem argumentos.
     * Necessário para desserialização JSON.
     */
    public ProfissionalComBairrosDTO() {
    }

    /**
     * Construtor com dados principais do profissional (sem bairros).
     *
     * @param id              ID do profissional
     * @param nome            Nome do profissional
     * @param especialidade   Especialidade
     * @param numeroConselho  Número do conselho
     * @param telefone        Telefone
     * @param email           E-mail
     * @param endereco        Endereço
     * @param cidade          Cidade
     */
    public ProfissionalComBairrosDTO(Integer id, String nome, String especialidade,
            String numeroConselho, String telefone, String email, String endereco, String cidade) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.numeroConselho = numeroConselho;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.cidade = cidade;
        this.bairrosAtendidos = new ArrayList<>();
    }

    /**
     * Construtor com todos os campos incluindo bairros.
     *
     * @param id                 ID do profissional
     * @param nome               Nome do profissional
     * @param especialidade      Especialidade
     * @param numeroConselho     Número do conselho
     * @param telefone           Telefone
     * @param email              E-mail
     * @param endereco           Endereço
     * @param cidade             Cidade
     * @param bairrosAtendidos   Lista de bairros (resumidos)
     */
    public ProfissionalComBairrosDTO(Integer id, String nome, String especialidade,
            String numeroConselho, String telefone, String email, String endereco, String cidade,
            List<BairroResumoDTO> bairrosAtendidos) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.numeroConselho = numeroConselho;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.cidade = cidade;
        this.bairrosAtendidos = bairrosAtendidos != null ? bairrosAtendidos : new ArrayList<>();
    }

    // ========================================================================
    // GETTERS E SETTERS
    // ========================================================================

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

    public List<BairroResumoDTO> getBairrosAtendidos() {
        return bairrosAtendidos;
    }

    /**
     * Define a lista de bairros atendidos.
     * Se null, inicializa com ArrayList vazio.
     *
     * @param bairrosAtendidos Lista de bairros a definir
     */
    public void setBairrosAtendidos(List<BairroResumoDTO> bairrosAtendidos) {
        this.bairrosAtendidos = bairrosAtendidos != null ? bairrosAtendidos : new ArrayList<>();
    }

    // ========================================================================
    // MÉTODOS UTILITÁRIOS
    // ========================================================================

    @Override
    public String toString() {
        return "ProfissionalComBairrosDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", especialidade='" + especialidade + '\'' +
                ", numeroConselho='" + numeroConselho + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", endereco='" + endereco + '\'' +
                ", cidade='" + cidade + '\'' +
                ", bairrosAtendidos=" + bairrosAtendidos +
                '}';
    }
}
