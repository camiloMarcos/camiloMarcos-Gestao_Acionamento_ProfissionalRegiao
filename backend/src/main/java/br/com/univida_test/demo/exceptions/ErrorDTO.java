package br.com.univida_test.demo.exceptions;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * DTO: ERROR RESPONSE
 * ============================================================================
 * 
 * Representa a estrutura padrão de erro retornada pela API.
 * 
 * Responsabilidades:
 * - Padronizar formato de mensagens de erro
 * - Incluir informações úteis para debugging
 * - Ser serializável em JSON
 * - Ser consistente em todas as operações
 * 
 * Estrutura:
 * - codigo: Código de erro interno (ex: "PROFISSIONAL_NAO_ENCONTRADO")
 * - titulo: Título amigável do erro (ex: "Profissional não encontrado")
 * - detalhe: Informações específicas sobre o erro
 * - timestamp: Quando o erro ocorreu
 * - path: Rota HTTP que causou o erro (opcional)
 * 
 * Exemplo de JSON Retornado:
 * ```json
 * {
 *     "codigo": "PROFISSIONAL_NAO_ENCONTRADO",
 *     "titulo": "Profissional não encontrado",
 *     "detalhe": "Profissional com ID 999 não existe no banco de dados",
 *     "timestamp": "2026-05-01T10:30:45.123",
 *     "path": "/api/profissional-bairro/associar"
 * }
 * ```
 * 
 * ============================================================================
 */
public class ErrorDTO {

    /**
     * Código de erro interno.
     * 
     * Usado para identificar programaticamente qual erro ocorreu.
     * Permite que clientes façam lógica condicional baseada no tipo de erro.
     * 
     * Exemplos:
     * - "PROFISSIONAL_NAO_ENCONTRADO"
     * - "BAIRRO_NAO_ENCONTRADO"
     * - "ASSOCIACAO_JA_EXISTE"
     * - "DADOS_INVALIDOS"
     * - "ERRO_TRANSACAO"
     * 
     * Padrão: SNAKE_CASE em MAIÚSCULAS
     */
    private String codigo;

    /**
     * Título amigável do erro.
     * 
     * Exibido ao usuário final, deve ser claro e compreensível.
     * 
     * Exemplos:
     * - "Profissional não encontrado"
     * - "Dados inválidos"
     * - "Operação duplicada"
     */
    private String titulo;

    /**
     * Detalhes específicos do erro.
     * 
     * Fornece contexto adicional para facilitar resolução.
     * Pode incluir IDs, nomes, razão do erro, etc.
     * 
     * Exemplos:
     * - "Profissional com ID 999 não existe no banco de dados"
     * - "Telefone deve estar no formato (XX) 9XXXX-XXXX"
     * - "Bairro ID 10 já está associado ao Profissional ID 5"
     */
    private String detalhe;

    /**
     * Timestamp de quando o erro ocorreu.
     * 
     * Formato: ISO 8601 (2026-05-01T10:30:45.123)
     * Útil para logging e debugging.
     */
    private LocalDateTime timestamp;

    /**
     * Caminho HTTP que causou o erro (opcional).
     * 
     * Exemplo: "/api/profissional-bairro/associar"
     * Útil para cliente identificar qual endpoint retornou erro.
     */
    private String path;

    // ========================================================================
    // CONSTRUTORES
    // ========================================================================

    /**
     * Construtor padrão sem argumentos.
     * Necessário para desserialização JSON.
     */
    public ErrorDTO() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Construtor com informações essenciais.
     *
     * @param codigo Código de erro
     * @param titulo Título do erro
     * @param detalhe Detalhe do erro
     */
    public ErrorDTO(String codigo, String titulo, String detalhe) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.detalhe = detalhe;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Construtor com todas as informações incluindo path.
     *
     * @param codigo Código de erro
     * @param titulo Título do erro
     * @param detalhe Detalhe do erro
     * @param path Caminho HTTP
     */
    public ErrorDTO(String codigo, String titulo, String detalhe, String path) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.detalhe = detalhe;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }

    /**
     * Construtor com timestamp customizado.
     *
     * @param codigo Código de erro
     * @param titulo Título do erro
     * @param detalhe Detalhe do erro
     * @param timestamp Timestamp customizado
     */
    public ErrorDTO(String codigo, String titulo, String detalhe, LocalDateTime timestamp) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.detalhe = detalhe;
        this.timestamp = timestamp;
    }

    /**
     * Construtor com todas as informações.
     *
     * @param codigo Código de erro
     * @param titulo Título do erro
     * @param detalhe Detalhe do erro
     * @param timestamp Timestamp customizado
     * @param path Caminho HTTP
     */
    public ErrorDTO(String codigo, String titulo, String detalhe, LocalDateTime timestamp, String path) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.detalhe = detalhe;
        this.timestamp = timestamp;
        this.path = path;
    }

    // ========================================================================
    // GETTERS E SETTERS
    // ========================================================================

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // ========================================================================
    // MÉTODOS UTILITÁRIOS
    // ========================================================================

    @Override
    public String toString() {
        return "ErrorDTO{" +
                "codigo='" + codigo + '\'' +
                ", titulo='" + titulo + '\'' +
                ", detalhe='" + detalhe + '\'' +
                ", timestamp=" + timestamp +
                ", path='" + path + '\'' +
                '}';
    }
}
