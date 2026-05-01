package br.com.univida_test.demo.exceptions;

/**
 * ============================================================================
 * EXCEÇÃO CUSTOMIZADA: PROFISSIONAL BAIRRO
 * ============================================================================
 * 
 * Exceção especializada para tratamento de erros relacionados ao
 * relacionamento entre Profissional e Bairro.
 * 
 * Responsabilidades:
 * - Identificar erros específicos do domínio de negócio
 * - Facilitar tratamento centralizado em GlobalExceptionHandler
 * - Fornecer mensagens claras e descritivas para o cliente
 * - Documentar cenários de erro comuns
 * 
 * Herança:
 * Extends RuntimeException → Exceção não verificada (unchecked)
 * Vantagem: Não precisa de try-catch obrigatório
 * Uso: Relançar em qualquer camada (Controller, Service, Repository)
 * 
 * Casos de Uso:
 * 1. Profissional não existe
 * 2. Bairro não existe
 * 3. Associação já existe (duplicação)
 * 4. Associação não existe (tentou remover relação inexistente)
 * 5. Dados inválidos para operação
 * 6. Erro na transação (rollback detectado)
 * 
 * Exemplo de Lançamento:
 * ```
 * if (profissional == null) {
 *     throw new ProfissionalBairroException(
 *         "Profissional não encontrado",
 *         "Profissional com ID " + id + " não existe em BD"
 *     );
 * }
 * ```
 * 
 * Exemplo de Tratamento (GlobalExceptionHandler):
 * ```
 * @ExceptionHandler(ProfissionalBairroException.class)
 * public ResponseEntity<ErrorDTO> tratarProfissionalBairroException(
 *         ProfissionalBairroException ex) {
 *     ErrorDTO error = new ErrorDTO(
 *         "ERRO_PROFISSIONAL_BAIRRO",
 *         ex.getTitulo(),
 *         ex.getDetalhe(),
 *         LocalDateTime.now()
 *     );
 *     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
 * }
 * ```
 * 
 * ============================================================================
 */
public class ProfissionalBairroException extends RuntimeException {

    /**
     * Título/categoria do erro.
     * 
     * Exemplos:
     * - "Profissional não encontrado"
     * - "Bairro não encontrado"
     * - "Associação já existe"
     * - "Dados inválidos"
     * 
     * Este título é exibido ao cliente e deve ser autoexplicativo.
     */
    private String titulo;

    /**
     * Detalhes específicos do erro.
     * 
     * Fornece contexto adicional incluindo IDs, nomes, e razão do erro.
     * 
     * Exemplos:
     * - "Profissional com ID 999 não existe no banco de dados"
     * - "Bairro ID 10 já está associado ao Profissional ID 5"
     * - "Telefone deve estar no formato (XX) 9XXXX-XXXX"
     * 
     * Este detalhe é exibido ao cliente para melhor compreensão.
     */
    private String detalhe;

    // ========================================================================
    // CONSTRUTORES
    // ========================================================================

    /**
     * Construtor com mensagem genérica.
     * 
     * Uso para erros simples que não precisam de detalhamento extra.
     * O título e detalhe serão iguais à mensagem.
     *
     * @param mensagem Mensagem descritiva do erro
     */
    public ProfissionalBairroException(String mensagem) {
        super(mensagem);
        this.titulo = mensagem;
        this.detalhe = mensagem;
    }

    /**
     * Construtor com título e detalhe (RECOMENDADO).
     * 
     * Permite separar categorização (título) de contexto específico (detalhe).
     * Facilita tratamento diferenciado por tipo de erro.
     *
     * @param titulo Categoria ou tipo do erro
     * @param detalhe Informações específicas do erro
     */
    public ProfissionalBairroException(String titulo, String detalhe) {
        super(titulo + " - " + detalhe);
        this.titulo = titulo;
        this.detalhe = detalhe;
    }

    /**
     * Construtor com título, detalhe e causa (para re-lançar exceções).
     * 
     * Útil quando você pega uma exceção de BD e quer relançar como
     * ProfissionalBairroException mantendo a stack trace original.
     *
     * @param titulo Categoria ou tipo do erro
     * @param detalhe Informações específicas do erro
     * @param causa Exceção original que causou este erro
     */
    public ProfissionalBairroException(String titulo, String detalhe, Throwable causa) {
        super(titulo + " - " + detalhe, causa);
        this.titulo = titulo;
        this.detalhe = detalhe;
    }

    // ========================================================================
    // GETTERS
    // ========================================================================

    /**
     * Obtém o título/categoria do erro.
     *
     * @return Título do erro
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Obtém os detalhes específicos do erro.
     *
     * @return Detalhe do erro
     */
    public String getDetalhe() {
        return detalhe;
    }

    // ========================================================================
    // MÉTODOS UTILITÁRIOS - FACTORY METHODS
    // ========================================================================

    /**
     * Factory method para erro de profissional não encontrado.
     * 
     * Centraliza a criação de exceção para este caso específico,
     * garantindo consistência de mensagens.
     * 
     * Exemplo de uso:
     * ```
     * if (!profissionalRepository.existsById(id)) {
     *     throw ProfissionalBairroException.profissionalNaoEncontrado(id);
     * }
     * ```
     *
     * @param profissionalId ID do profissional não encontrado
     * @return ProfissionalBairroException com mensagem apropriada
     */
    public static ProfissionalBairroException profissionalNaoEncontrado(Integer profissionalId) {
        return new ProfissionalBairroException(
            "Profissional não encontrado",
            String.format(
                "Profissional com ID %d não existe no banco de dados",
                profissionalId
            )
        );
    }

    /**
     * Factory method para erro de bairro não encontrado.
     *
     * @param bairroId ID do bairro não encontrado
     * @return ProfissionalBairroException com mensagem apropriada
     */
    public static ProfissionalBairroException bairroNaoEncontrado(Integer bairroId) {
        return new ProfissionalBairroException(
            "Bairro não encontrado",
            String.format(
                "Bairro com ID %d não existe no banco de dados",
                bairroId
            )
        );
    }

    /**
     * Factory method para erro de associação já existente.
     *
     * @param profissionalId ID do profissional
     * @param profissionalNome Nome do profissional
     * @param bairroId ID do bairro
     * @param bairroNome Nome do bairro
     * @return ProfissionalBairroException com mensagem apropriada
     */
    public static ProfissionalBairroException associacaoJaExiste(
            Integer profissionalId, String profissionalNome,
            Integer bairroId, String bairroNome) {
        return new ProfissionalBairroException(
            "Associação já existe",
            String.format(
                "Profissional ID %d (%s) já está associado ao Bairro ID %d (%s)",
                profissionalId, profissionalNome,
                bairroId, bairroNome
            )
        );
    }

    /**
     * Factory method para erro de IDs inválidos.
     *
     * @param profissionalId ID do profissional
     * @param bairroId ID do bairro
     * @return ProfissionalBairroException com mensagem apropriada
     */
    public static ProfissionalBairroException idsInvalidos(Integer profissionalId, Integer bairroId) {
        return new ProfissionalBairroException(
            "IDs inválidos",
            String.format(
                "IDs devem ser maiores que 0. Profissional: %s, Bairro: %s",
                profissionalId, bairroId
            )
        );
    }

    /**
     * Factory method para erro de dados inválidos.
     *
     * @param mensagem Descrição do problema com os dados
     * @return ProfissionalBairroException com mensagem apropriada
     */
    public static ProfissionalBairroException dadosInvalidos(String mensagem) {
        return new ProfissionalBairroException(
            "Dados inválidos",
            mensagem
        );
    }

    /**
     * Factory method para erro de operação de transação.
     *
     * @param operacao Descrição da operação que falhou
     * @param motivo Razão pela qual falhou
     * @return ProfissionalBairroException com mensagem apropriada
     */
    public static ProfissionalBairroException erroTransacao(String operacao, String motivo) {
        return new ProfissionalBairroException(
            "Erro na transação",
            String.format(
                "Operação '%s' falhou: %s. Transação foi revertida.",
                operacao, motivo
            )
        );
    }
}
