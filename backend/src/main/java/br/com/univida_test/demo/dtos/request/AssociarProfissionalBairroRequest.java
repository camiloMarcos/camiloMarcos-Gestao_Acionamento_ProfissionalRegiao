package br.com.univida_test.demo.dtos.request;

import jakarta.validation.constraints.NotNull;

/**
 * ============================================================================
 * DTO: ASSOCIAR PROFISSIONAL A BAIRRO - REQUEST
 * ============================================================================
 * 
 * Representa uma requisição de associação entre um profissional e um bairro.
 * 
 * Responsabilidades:
 * - Receber dados de uma requisição HTTP POST para associar profissional
 * - Validar que ambos os IDs (profissional e bairro) são fornecidos
 * - Servir como contrato da API para o endpoint de associação
 * 
 * Caso de Uso:
 * Um profissional já existente no sistema será associado a um bairro
 * que também já existe. Ambos devem ter IDs válidos e já estar persistidos.
 * 
 * Exemplo de Uso:
 * ```json
 * POST /api/profissional-bairro/associar
 * {
 *     "profissionalId": 5,
 *     "bairroId": 10
 * }
 * ```
 * 
 * Validações:
 * - profissionalId não pode ser nulo
 * - bairroId não pode ser nulo
 * 
 * Fluxo na Controller:
 * 1. Cliente envia HTTP POST com este JSON
 * 2. Spring desserializa em AssociarProfissionalBairroRequest
 * 3. Validações (não nulo) são aplicadas automaticamente
 * 4. Controller passa para Service
 * 5. Service realiza validações adicionais (> 0, existência em BD, etc)
 * 6. Response é retornada
 * 
 * ============================================================================
 */
public class AssociarProfissionalBairroRequest {

    /**
     * ID do profissional que será associado.
     * 
     * Este ID deve corresponder a um profissional que já existe no banco
     * de dados com JPA ID gerado.
     * 
     * Validação: @NotNull garante que não vem null do cliente
     * Validação adicional: Service verifica se > 0 e se existe no BD
     * 
     * Exemplo de valor válido: 5
     */
    @NotNull(message = "ID do profissional não pode ser nulo")
    private Integer profissionalId;

    /**
     * ID do bairro que o profissional será associado.
     * 
     * Este ID deve corresponder a um bairro que já existe no banco
     * de dados com JPA ID gerado.
     * 
     * Validação: @NotNull garante que não vem null do cliente
     * Validação adicional: Service verifica se > 0 e se existe em BD
     * 
     * Exemplo de valor válido: 10
     */
    @NotNull(message = "ID do bairro não pode ser nulo")
    private Integer bairroId;

    // ========================================================================
    // CONSTRUTORES
    // ========================================================================

    /**
     * Construtor padrão sem argumentos.
     * Necessário para que o Spring consiga desserializar o JSON.
     */
    public AssociarProfissionalBairroRequest() {
    }

    /**
     * Construtor com os dois IDs.
     *
     * @param profissionalId ID do profissional
     * @param bairroId       ID do bairro
     */
    public AssociarProfissionalBairroRequest(Integer profissionalId, Integer bairroId) {
        this.profissionalId = profissionalId;
        this.bairroId = bairroId;
    }

    // ========================================================================
    // GETTERS E SETTERS
    // ========================================================================

    /**
     * Obtém o ID do profissional.
     *
     * @return ID do profissional
     */
    public Integer getProfissionalId() {
        return profissionalId;
    }

    /**
     * Define o ID do profissional.
     *
     * @param profissionalId ID a definir
     */
    public void setProfissionalId(Integer profissionalId) {
        this.profissionalId = profissionalId;
    }

    /**
     * Obtém o ID do bairro.
     *
     * @return ID do bairro
     */
    public Integer getBairroId() {
        return bairroId;
    }

    /**
     * Define o ID do bairro.
     *
     * @param bairroId ID a definir
     */
    public void setBairroId(Integer bairroId) {
        this.bairroId = bairroId;
    }

    // ========================================================================
    // MÉTODOS UTILITÁRIOS
    // ========================================================================

    @Override
    public String toString() {
        return "AssociarProfissionalBairroRequest{" +
                "profissionalId=" + profissionalId +
                ", bairroId=" + bairroId +
                '}';
    }
}
