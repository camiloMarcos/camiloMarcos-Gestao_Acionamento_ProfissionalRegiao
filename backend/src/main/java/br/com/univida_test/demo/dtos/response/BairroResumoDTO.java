package br.com.univida_test.demo.dtos.response;

/**
 * ============================================================================
 * DTO: BAIRRO RESUMO - RESPONSE
 * ============================================================================
 * 
 * Representa uma versão resumida de um bairro para resposta em API.
 * 
 * Responsabilidades:
 * - Retornar apenas informações essenciais do bairro
 * - Evitar serialização de dados desnecessários
 * - Evitar referência circular com Profissional
 * - Servir como contrato de resposta minimalista
 * 
 * Caso de Uso:
 * Quando um profissional retorna sua lista de bairros (em ProfissionalComBairrosDTO),
 * usa esta classe resumida para cada bairro, evitando circular references:
 * 
 * Profissional → Lista de BairroResumoDTO → ... (sem reference de volta)
 * 
 * Estrutura Compacta:
 * - ID: Identificador único
 * - Nome: Nome do bairro/distrito
 * - Cidade: Cidade onde está o bairro
 * 
 * O que NÃO inclui:
 * - Não inclui lista de profissionais (evita referência circular)
 * - Não inclui dados desnecessários (perigosidade, descrição, etc)
 * 
 * Exemplo de JSON Retornado:
 * ```json
 * {
 *     "id": 10,
 *     "nome": "Centro",
 *     "cidade": "São Paulo"
 * }
 * ```
 * 
 * ============================================================================
 */
public class BairroResumoDTO {

    /**
     * ID único do bairro.
     * 
     * Exemplo: 10
     */
    private Integer id;

    /**
     * Nome do bairro ou distrito.
     * 
     * Exemplo: "Centro", "Vila Mariana", "Pinheiros"
     */
    private String nome;

    /**
     * Cidade onde está localizado o bairro.
     * 
     * Exemplo: "São Paulo"
     */
    private String cidade;

    // ========================================================================
    // CONSTRUTORES
    // ========================================================================

    /**
     * Construtor padrão sem argumentos.
     * Necessário para desserialização JSON.
     */
    public BairroResumoDTO() {
    }

    /**
     * Construtor com todos os campos.
     *
     * @param id     ID do bairro
     * @param nome   Nome do bairro
     * @param cidade Cidade do bairro
     */
    public BairroResumoDTO(Integer id, String nome, String cidade) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
    }

    // ========================================================================
    // GETTERS E SETTERS
    // ========================================================================

    /**
     * Obtém o ID do bairro.
     *
     * @return ID do bairro
     */
    public Integer getId() {
        return id;
    }

    /**
     * Define o ID do bairro.
     *
     * @param id ID a definir
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtém o nome do bairro.
     *
     * @return Nome do bairro
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome do bairro.
     *
     * @param nome Nome a definir
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém a cidade do bairro.
     *
     * @return Cidade do bairro
     */
    public String getCidade() {
        return cidade;
    }

    /**
     * Define a cidade do bairro.
     *
     * @param cidade Cidade a definir
     */
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    // ========================================================================
    // MÉTODOS UTILITÁRIOS
    // ========================================================================

    @Override
    public String toString() {
        return "BairroResumoDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cidade='" + cidade + '\'' +
                '}';
    }
}
