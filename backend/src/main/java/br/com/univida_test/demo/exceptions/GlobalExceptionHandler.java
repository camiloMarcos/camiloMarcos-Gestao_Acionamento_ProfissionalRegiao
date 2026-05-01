package br.com.univida_test.demo.exceptions;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * ============================================================================
 * HANDLER: EXCEÇÕES GLOBAL
 * ============================================================================
 * 
 * Centralizador de tratamento de exceções para toda a aplicação.
 * 
 * Responsabilidades:
 * - Capturar exceções lançadas em qualquer @RestController
 * - Traduzir exceções em respostas HTTP apropriadas
 * - Padronizar formato de mensagens de erro
 * - Retornar ErrorDTO com informações úteis
 * - Logar erros para debugging
 * 
 * Arquitetura:
 * Quando uma exceção é lançada em qualquer Controller/Service:
 * 1. Spring procura por @ExceptionHandler correspondente
 * 2. Este handler captura a exceção
 * 3. Traduz em ResponseEntity com HTTP status apropriado
 * 4. Retorna ErrorDTO com informações estruturadas
 * 5. Spring serializa em JSON
 * 6. Cliente recebe resposta estruturada
 * 
 * Anotações:
 * - @RestControllerAdvice: Marca como handler global para @RestController
 * - @ExceptionHandler: Especifica qual exceção vai ser tratada
 * 
 * Fluxo Completo de Erro:
 * ```
 * ┌─ Controller lança exceção
 * │  throw new ProfissionalBairroException(...)
 * │
 * ├─ Spring detecta exceção
 * │  (procura por @ExceptionHandler correspondente)
 * │
 * ├─ GlobalExceptionHandler.handleProfissionalBairro() é chamado
 * │  (método correspondente ao tipo de exceção)
 * │
 * ├─ Handler cria ErrorDTO com informações do erro
 * │  (código, título, detalhe, timestamp)
 * │
 * ├─ Handler retorna ResponseEntity com HTTP status
 * │  (200, 400, 404, 409, 500, etc)
 * │
 * ├─ Spring serializa ErrorDTO em JSON
 * │
 * └─ Cliente recebe resposta estruturada com informações do erro
 *    {
 *        "codigo": "PROFISSIONAL_NAO_ENCONTRADO",
 *        "titulo": "Profissional não encontrado",
 *        "detalhe": "Profissional com ID 999 não existe",
 *        "timestamp": "2026-05-01T10:30:45.123"
 *    }
 * ```
 * 
 * Exemplos de Uso:
 * 
 * 1. Controller lança ProfissionalBairroException
 * 2. Handler captura
 * 3. Retorna 400 BAD REQUEST com ErrorDTO
 * 
 * 1. Spring detecta validação quebrada (@Valid falhou)
 * 2. Handler captura MethodArgumentNotValidException
 * 3. Retorna 400 BAD REQUEST com lista de erros
 * 
 * 1. ObjectNotFoundException é lançada
 * 2. Handler captura
 * 3. Retorna 404 NOT FOUND com ErrorDTO
 * 
 * ============================================================================
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========================================================================
    // HANDLER 1: PROFISSIONAL-BAIRRO EXCEPTION (CUSTOMIZADA)
    // ========================================================================

    /**
     * ▶ HANDLER 1: PROFISSIONAL BAIRRO EXCEPTION
     * 
     * Trata exceções customizadas lançadas pelo domínio de relacionamento
     * entre Profissional e Bairro.
     * 
     * Casos Tratados:
     * - Profissional não encontrado
     * - Bairro não encontrado
     * - Associação já existe (duplicação)
     * - Dados inválidos
     * - Erro em transação
     * 
     * HTTP Status Retornado:
     * - 400 BAD REQUEST: Erro de negócio genérico
     * 
     * Fluxo:
     * 1. Exceção é lançada em Service ou Controller
     * 2. Handler captura ProfissionalBairroException
     * 3. Extrai título e detalhe da exceção
     * 4. Mapeia para código apropriado
     * 5. Cria ErrorDTO com informações
     * 6. Retorna 400 BAD REQUEST
     * 
     * Exemplo de Captura:
     * ```
     * Service lança:
     * throw new ProfissionalBairroException(
     *     "Profissional não encontrado",
     *     "Profissional com ID 999 não existe"
     * );
     * 
     * Handler retorna:
     * HTTP 400 BAD REQUEST
     * {
     *     "codigo": "PROFISSIONAL_NAO_ENCONTRADO",
     *     "titulo": "Profissional não encontrado",
     *     "detalhe": "Profissional com ID 999 não existe",
     *     "timestamp": "2026-05-01T10:30:45"
     * }
     * ```
     * 
     * @param ex Exceção capturada
     * @param request Requisição HTTP (para extrair path)
     * @return ResponseEntity com HTTP 400 e ErrorDTO
     */
    @ExceptionHandler(ProfissionalBairroException.class)
    public ResponseEntity<ErrorDTO> handleProfissionalBairroException(
            ProfissionalBairroException ex,
            WebRequest request) {
        
        // ▶ Extrair informações da exceção
        String titulo = ex.getTitulo();
        String detalhe = ex.getDetalhe();
        
        // ▶ Mapear título para código interno
        String codigo = mapearTituloParaCodigo(titulo);
        
        // ▶ Log (útil para debugging)
        System.err.println(String.format(
            "❌ ProfissionalBairroException [%s]: %s",
            codigo, detalhe
        ));
        
        // ▶ Criar ErrorDTO
        ErrorDTO errorDTO = new ErrorDTO(
            codigo,
            titulo,
            detalhe,
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        
        // ▶ Retornar resposta com HTTP 400 BAD REQUEST
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorDTO);
    }

    // ========================================================================
    // HANDLER 2: OBJECT NOT FOUND EXCEPTION (EXISTENTE)
    // ========================================================================

    /**
     * ▶ HANDLER 2: OBJECT NOT FOUND EXCEPTION
     * 
     * Trata exceção genérica de "não encontrado" do projeto.
     * 
     * Casos Tratados:
     * - Qualquer entidade não encontrada por ID
     * - Busca retornou vazio
     * 
     * HTTP Status Retornado:
     * - 404 NOT FOUND: Recurso não existe
     * 
     * Exemplo de Captura:
     * ```
     * Service lança:
     * throw new ObjectNotFoundException("Bairro com ID 999 não encontrado");
     * 
     * Handler retorna:
     * HTTP 404 NOT FOUND
     * {
     *     "codigo": "RECURSO_NAO_ENCONTRADO",
     *     "titulo": "Recurso não encontrado",
     *     "detalhe": "Bairro com ID 999 não encontrado",
     *     "timestamp": "2026-05-01T10:30:45"
     * }
     * ```
     * 
     * @param ex Exceção capturada
     * @param request Requisição HTTP
     * @return ResponseEntity com HTTP 404 e ErrorDTO
     */
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleObjectNotFoundException(
            ObjectNotFoundException ex,
            WebRequest request) {
        
        // ▶ Log
        System.err.println(String.format(
            "❌ ObjectNotFoundException: %s",
            ex.getMessage()
        ));
        
        // ▶ Criar ErrorDTO
        ErrorDTO errorDTO = new ErrorDTO(
            "RECURSO_NAO_ENCONTRADO",
            "Recurso não encontrado",
            ex.getMessage(),
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        
        // ▶ Retornar resposta com HTTP 404 NOT FOUND
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorDTO);
    }

    // ========================================================================
    // HANDLER 3: VALIDAÇÃO DO BEAN (@Valid falhou)
    // ========================================================================

    /**
     * ▶ HANDLER 3: METHOD ARGUMENT NOT VALID
     * 
     * Trata exceção lançada quando @Valid encontra validação quebrada.
     * 
     * Ativado Quando:
     * - @NotNull encontra null
     * - @NotBlank encontra string vazia
     * - @Email encontra formato inválido
     * - @Pattern encontra regex que não combina
     * - @Size encontra tamanho fora do intervalo
     * - Qualquer outra validação de Bean Validation
     * 
     * HTTP Status Retornado:
     * - 400 BAD REQUEST: Dados inválidos
     * 
     * Detalhes Inclusos:
     * - Lista completa de campos com erro
     * - Mensagem de validação para cada campo
     * 
     * Exemplo de Captura:
     * ```
     * Cliente envia:
     * POST /api/profissional-bairro/criar-com-bairros
     * {
     *     "nome": "Jo",          ← Tamanho menor que 3
     *     "email": "invalido",   ← Não é email válido
     *     "telefone": "123"      ← Não combina com padrão
     * }
     * 
     * Handler retorna:
     * HTTP 400 BAD REQUEST
     * {
     *     "codigo": "DADOS_INVALIDOS",
     *     "titulo": "Dados inválidos",
     *     "detalhe": "nome: Nome deve ter entre 3 e 150 caracteres; 
     *                 email: E-mail deve ser válido; 
     *                 telefone: Telefone deve estar no formato (XX)...",
     *     "timestamp": "2026-05-01T10:30:45"
     * }
     * ```
     * 
     * @param ex Exceção capturada
     * @param request Requisição HTTP
     * @return ResponseEntity com HTTP 400 e ErrorDTO
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        // ▶ Extrair erros de validação
        String detalheErros = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> String.format(
                "%s: %s",
                error.getField(),
                error.getDefaultMessage()
            ))
            .collect(Collectors.joining("; "));
        
        // ▶ Log
        System.err.println(String.format(
            "❌ MethodArgumentNotValidException: %s",
            detalheErros
        ));
        
        // ▶ Criar ErrorDTO
        ErrorDTO errorDTO = new ErrorDTO(
            "DADOS_INVALIDOS",
            "Dados inválidos",
            detalheErros,
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        
        // ▶ Retornar resposta com HTTP 400 BAD REQUEST
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorDTO);
    }

    // ========================================================================
    // HANDLER 4: ILLEGAL STATE EXCEPTION
    // ========================================================================

    /**
     * ▶ HANDLER 4: ILLEGAL STATE EXCEPTION
     * 
     * Trata exceção lançada quando o estado do objeto é inválido
     * para a operação.
     * 
     * Casos Típicos:
     * - Tentar adicionar profissional já adicionado
     * - Tentar remover algo que não está no estado esperado
     * - Operação em estado incompatível
     * 
     * HTTP Status Retornado:
     * - 409 CONFLICT: Conflito de estado
     * 
     * @param ex Exceção capturada
     * @param request Requisição HTTP
     * @return ResponseEntity com HTTP 409 e ErrorDTO
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDTO> handleIllegalStateException(
            IllegalStateException ex,
            WebRequest request) {
        
        // ▶ Log
        System.err.println(String.format(
            "❌ IllegalStateException: %s",
            ex.getMessage()
        ));
        
        // ▶ Criar ErrorDTO
        ErrorDTO errorDTO = new ErrorDTO(
            "CONFLITO_ESTADO",
            "Conflito de estado",
            ex.getMessage(),
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        
        // ▶ Retornar resposta com HTTP 409 CONFLICT
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(errorDTO);
    }

    // ========================================================================
    // HANDLER 5: ILLEGAL ARGUMENT EXCEPTION
    // ========================================================================

    /**
     * ▶ HANDLER 5: ILLEGAL ARGUMENT EXCEPTION
     * 
     * Trata exceção lançada quando argumentos são inválidos.
     * 
     * Casos Típicos:
     * - IDs negativos ou zero
     * - Dados obrigatórios nulos
     * - Ranges ou formatos inválidos
     * 
     * HTTP Status Retornado:
     * - 400 BAD REQUEST: Requisição inválida
     * 
     * @param ex Exceção capturada
     * @param request Requisição HTTP
     * @return ResponseEntity com HTTP 400 e ErrorDTO
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {
        
        // ▶ Log
        System.err.println(String.format(
            "❌ IllegalArgumentException: %s",
            ex.getMessage()
        ));
        
        // ▶ Criar ErrorDTO
        ErrorDTO errorDTO = new ErrorDTO(
            "ARGUMENTO_INVALIDO",
            "Argumento inválido",
            ex.getMessage(),
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        
        // ▶ Retornar resposta com HTTP 400 BAD REQUEST
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorDTO);
    }

    // ========================================================================
    // HANDLER 6: EXCEPTION GENÉRICA (CATCH-ALL)
    // ========================================================================

    /**
     * ▶ HANDLER 6: EXCEPTION GENÉRICA
     * 
     * Captura qualquer exceção não tratada por handlers específicos.
     * 
     * Útil para:
     * - Não retornar stack trace ao cliente (segurança)
     * - Logar erro para debugging
     * - Retornar mensagem genérica apropriada
     * 
     * HTTP Status Retornado:
     * - 500 INTERNAL SERVER ERROR: Erro não esperado
     * 
     * Exemplo:
     * ```
     * Se qualquer exceção não prevista for lançada:
     * - NullPointerException
     * - IndexOutOfBoundsException
     * - Qualquer RuntimeException não tratada
     * 
     * Handler captura e retorna:
     * HTTP 500 INTERNAL SERVER ERROR
     * {
     *     "codigo": "ERRO_INTERNO",
     *     "titulo": "Erro interno do servidor",
     *     "detalhe": "Ocorreu um erro inesperado. Contate o suporte.",
     *     "timestamp": "2026-05-01T10:30:45"
     * }
     * ```
     * 
     * @param ex Exceção capturada
     * @param request Requisição HTTP
     * @return ResponseEntity com HTTP 500 e ErrorDTO
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        // ▶ Log detalhado (útil para debugging)
        System.err.println("❌ Exceção não tratada:");
        ex.printStackTrace();
        
        // ▶ Criar ErrorDTO com mensagem genérica (não expor detalhes internos)
        ErrorDTO errorDTO = new ErrorDTO(
            "ERRO_INTERNO",
            "Erro interno do servidor",
            "Ocorreu um erro inesperado. Contate o suporte.",
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        
        // ▶ Retornar resposta com HTTP 500 INTERNAL SERVER ERROR
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorDTO);
    }

    // ========================================================================
    // MÉTODO UTILITÁRIO: MAPEAR TÍTULO PARA CÓDIGO
    // ========================================================================

    /**
     * ▶ MÉTODO AUXILIAR: MAPEAR TÍTULO PARA CÓDIGO
     * 
     * Converte títulos de exceção em códigos internos.
     * 
     * Garante consistência: mesmo título sempre retorna mesmo código.
     * 
     * Exemplo:
     * ```
     * "Profissional não encontrado" → "PROFISSIONAL_NAO_ENCONTRADO"
     * "Bairro não encontrado" → "BAIRRO_NAO_ENCONTRADO"
     * "Associação já existe" → "ASSOCIACAO_JA_EXISTE"
     * ```
     *
     * @param titulo Título da exceção
     * @return Código mapeado (SNAKE_CASE em maiúsculas)
     */
    private String mapearTituloParaCodigo(String titulo) {
        if (titulo == null) {
            return "ERRO_DESCONHECIDO";
        }
        
        // ▶ Converter para SNAKE_CASE maiúsculo
        return titulo
            .toUpperCase()
            .replace(" ", "_")
            .replace("Á", "A")
            .replace("É", "E")
            .replace("Í", "I")
            .replace("Ó", "O")
            .replace("Ú", "U");
    }
}
