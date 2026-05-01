package br.com.univida_test.demo.dtos.response;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * DTO: BAIRRO COM PROFISSIONAIS - RESPONSE
 * ============================================================================
 * 
 * Representa um bairro completo com sua lista de profissionais que ali atuam.
 * 
 * Responsabilidades:
 * - Retornar dados completos do bairro
 * - Retornar lista de profissionais que atuam neste bairro
 * - Evitar serialização circular (usa ProfissionalResumoDTO)
 * - Servir como resposta padrão para operações de leitura
 * 
 * Caso de Uso:
 * Quando uma operação retorna um bairro com seus profissionais associados:
 * - GET /api/bairro/{bairroId}/profissionais
 * - GET /api/bairro/{bairroId} (com profissionais inclusos)
 * 
 * Estrutura:
 * - Dados principais do bairro (ID, nome, cidade, perigosidade, etc)
 * - Lista de profissionais resumidos (apenas ID, nome, especialidade)
 * 
 * Problema Evitado:
 * Se retornássemos Bairro completo com lista de Profissional completos,
 * cada Profissional teria lista de Bairro, criando referência circular:
 * 
 * ❌ Bairro → Profissional → Bairro → Profissional → ... (CIRCULAR!)
 * ✅ Bairro → ProfissionalResumoDTO → (sem referência de volta)
 * 
 * Exemplo de JSON Retornado:
 * ```json
 * {
 *     "id": 1,
 *     "nome": "Centro",
 *     "cidade": "São Paulo",
 *     "perigoso": false,
 *     "descricao": "Centro da cidade de São Paulo",
 *     "profissionais": [
 *         {"id": 5, "nome": "Dr. João Silva", "especialidade": "Cardiologia"},
 *         {"id": 8, "nome": "Dra. Maria Santos", "especialidade": "Neurologia"},
 *         {"id": 12, "nome": "Dr. Carlos Oliveira", "especialidade": "Ortopedia"}
 *     ]
 * }
 * ```
 * 
 * ============================================================================
 */
public class BairroComProfissionaisDTO {

    /**
     * ID único do bairro.
     * Gerado pelo banco de dados (JPA).
     */
    private Integer id;

    /**
     * Nome do bairro ou distrito.
     * Exemplo: "Centro", "Pinheiros", "Vila Mariana"
     */
    private String nome;

    /**
     * Cidade onde está localizado o bairro.
     * Exemplo: "São Paulo"
     */
    private String cidade;

    /**
     * Indicador de perigosidade do bairro.
     * true = Bairro considerado perigoso
     * false = Bairro seguro
     */
    private Boolean perigoso;

    /**
     * Descrição complementar do bairro.
     * Pode conter informações sobre características, história, etc.
     */
    private String descricao;

    /**
     * Lista de profissionais que atuam neste bairro.
     * 
     * Importante:
     * - Usa ProfissionalResumoDTO (não Profissional completo)
     * - Cada ProfissionalResumoDTO contém apenas: ID, nome, especialidade
     * - Evita referência circular (Profissional não contém lista de Bairro)
     * - Inicializado com ArrayList para evitar NullPointerException
     * 
     * Exemplo:
     * [
     *     {"id": 5, "nome": "Dr. João Silva", "especialidade": "Cardiologia"},
     *     {"id": 8, "nome": "Dra. Maria Santos", "especialidade": "Neurologia"}
     * ]
     */
    private List<ProfissionalResumoDTO> profissionais = new ArrayList<>();

    // ========================================================================
    // CONSTRUTORES
    // ========================================================================

    /**
     * Construtor padrão sem argumentos.
     * Necessário para desserialização JSON.
     */
    public BairroComProfissionaisDTO() {
    }

    /**
     * Construtor com dados principais do bairro (sem profissionais).
     *
     * @param id           ID do bairro
     * @param nome         Nome do bairro
     * @param cidade       Cidade do bairro
     * @param perigoso     Indicador de perigosidade
     * @param descricao    Descrição do bairro
     */
    public BairroComProfissionaisDTO(Integer id, String nome, String cidade, Boolean perigoso, String descricao) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
        this.perigoso = perigoso;
        this.descricao = descricao;
        this.profissionais = new ArrayList<>();
    }

    /**
     * Construtor com todos os campos incluindo profissionais.
     *
     * @param id             ID do bairro
     * @param nome           Nome do bairro
     * @param cidade         Cidade do bairro
     * @param perigoso       Indicador de perigosidade
     * @param descricao      Descrição do bairro
     * @param profissionais  Lista de profissionais (resumidos)
     */
    public BairroComProfissionaisDTO(Integer id, String nome, String cidade, Boolean perigoso,
            String descricao, List<ProfissionalResumoDTO> profissionais) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
        this.perigoso = perigoso;
        this.descricao = descricao;
        this.profissionais = profissionais != null ? profissionais : new ArrayList<>();
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

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Boolean getPerigoso() {
        return perigoso;
    }

    public void setPerigoso(Boolean perigoso) {
        this.perigoso = perigoso;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ProfissionalResumoDTO> getProfissionais() {
        return profissionais;
    }

    /**
     * Define a lista de profissionais.
     * Se null, inicializa com ArrayList vazio.
     *
     * @param profissionais Lista de profissionais a definir
     */
    public void setProfissionais(List<ProfissionalResumoDTO> profissionais) {
        this.profissionais = profissionais != null ? profissionais : new ArrayList<>();
    }

    // ========================================================================
    // MÉTODOS UTILITÁRIOS
    // ========================================================================

    @Override
    public String toString() {
        return "BairroComProfissionaisDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cidade='" + cidade + '\'' +
                ", perigoso=" + perigoso +
                ", descricao='" + descricao + '\'' +
                ", profissionais=" + profissionais +
                '}';
    }
}
