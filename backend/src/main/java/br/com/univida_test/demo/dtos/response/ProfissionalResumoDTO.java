package br.com.univida_test.demo.dtos.response;

/**
 * ============================================================================
 * DTO: PROFISSIONAL RESUMO - RESPONSE
 * ============================================================================
 * 
 * Representa uma versão resumida de um profissional para resposta em API.
 * 
 * Responsabilidades:
 * - Retornar apenas informações essenciais do profissional
 * - Evitar serialização de dados sensíveis ou desnecessários
 * - Evitar referência circular com Bairro (não inclui lista de bairros)
 * - Servir como contrato de resposta minimalista
 * 
 * Caso de Uso:
 * Quando um bairro retorna sua lista de profissionais (em BairroComProfissionaisDTO),
 * usa esta classe resumida para cada profissional, evitando circular references:
 * 
 * Bairro → Lista de BairroResumoDTO → ... (sem reference de volta para Profissional)
 * 
 * Estrutura Compacta:
 * - ID: Identificador único
 * - Nome: Nome do profissional
 * - Especialidade: Sua área de atuação
 * 
 * O que NÃO inclui:
 * - Não inclui lista de bairros (evita referência circular)
 * - Não inclui dados sensíveis (telefone, email, endereço)
 * - Não inclui dados administrativos (numeroConselho)
 * 
 * Exemplo de JSON Retornado:
 * ```json
 * {
 *     "id": 5,
 *     "nome": "Dr. João Silva",
 *     "especialidade": "Cardiologia"
 * }
 * ```
 * 
 * ============================================================================
 */
public class ProfissionalResumoDTO {

    /**
     * ID único do profissional.
     * 
     * Exemplo: 5
     */
    private Integer id;

    /**
     * Nome completo do profissional.
     * 
     * Exemplo: "Dr. João Silva"
     */
    private String nome;

    /**
     * Especialidade médica do profissional.
     * 
     * Exemplo: "Cardiologia"
     */
    private String especialidade;

    // ========================================================================
    // CONSTRUTORES
    // ========================================================================

    /**
     * Construtor padrão sem argumentos.
     * Necessário para desserialização JSON.
     */
    public ProfissionalResumoDTO() {
    }

    /**
     * Construtor com todos os campos.
     *
     * @param id             ID do profissional
     * @param nome           Nome do profissional
     * @param especialidade  Especialidade médica
     */
    public ProfissionalResumoDTO(Integer id, String nome, String especialidade) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
    }

    // ========================================================================
    // GETTERS E SETTERS
    // ========================================================================

    /**
     * Obtém o ID do profissional.
     *
     * @return ID do profissional
     */
    public Integer getId() {
        return id;
    }

    /**
     * Define o ID do profissional.
     *
     * @param id ID a definir
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtém o nome do profissional.
     *
     * @return Nome do profissional
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome do profissional.
     *
     * @param nome Nome a definir
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém a especialidade do profissional.
     *
     * @return Especialidade do profissional
     */
    public String getEspecialidade() {
        return especialidade;
    }

    /**
     * Define a especialidade do profissional.
     *
     * @param especialidade Especialidade a definir
     */
    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    // ========================================================================
    // MÉTODOS UTILITÁRIOS
    // ========================================================================

    @Override
    public String toString() {
        return "ProfissionalResumoDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", especialidade='" + especialidade + '\'' +
                '}';
    }
}
