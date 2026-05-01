package br.com.univida_test.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.univida_test.demo.dtos.request.AssociarProfissionalBairroRequest;
import br.com.univida_test.demo.dtos.request.CriarProfissionalComBairrosRequest;
import br.com.univida_test.demo.dtos.response.ProfissionalComBairrosDTO;
import br.com.univida_test.demo.service.ProfissionalBairroService;
import jakarta.validation.Valid;

/**
 * ============================================================================
 * CONTROLLER: PROFISSIONAL-BAIRRO
 * ============================================================================
 * 
 * Responsável por expor os endpoints da API REST para gerenciar relacionamentos
 * entre Profissionais e Bairros.
 * 
 * Operações Disponíveis:
 * 1. POST /api/profissional-bairro/associar
 *    - Associar um profissional já existente a um bairro já existente
 * 
 * 2. POST /api/profissional-bairro/criar-com-bairros
 *    - Criar um novo profissional e associá-lo a múltiplos bairros em uma operação
 * 
 * 3. DELETE /api/profissional-bairro/{profissionalId}/bairro/{bairroId}
 *    - Remover a associação entre um profissional e um bairro
 * 
 * Fluxo Geral:
 * 1. Cliente envia requisição HTTP com dados (JSON)
 * 2. Spring desserializa em DTO Request
 * 3. Validações automáticas (@Valid, @NotNull, @Email, etc)
 * 4. Controller passa para Service
 * 5. Service realiza lógica de negócio e validações adicionais
 * 6. Controller retorna Response com DTO ou código de sucesso
 * 7. Spring serializa DTO em JSON e envia para cliente
 * 
 * Camadas de Validação:
 * - Camada 1: DTOs Request (@NotNull, @Email, @Pattern, etc)
 * - Camada 2: Service (null checks, existência em BD, duplicação, etc)
 * - Camada 3: Exception Handler (centraliza tratamento de erros)
 * 
 * Anotações Spring Utilizadas:
 * - @RestController: Marca classe como controller REST (retorna JSON)
 * - @RequestMapping: Define prefixo de rotas (ex: /api/profissional-bairro)
 * - @PostMapping: Mapeia requisição HTTP POST
 * - @DeleteMapping: Mapeia requisição HTTP DELETE
 * - @PathVariable: Extrai valor da URL (ex: {profissionalId})
 * - @RequestBody: Desserializa JSON do corpo em objeto DTO
 * - @Valid: Ativa validações do DTO (Bean Validation)
 * - @Autowired: Injeta dependências (ProfissionalBairroService)
 * 
 * Códigos HTTP Retornados:
 * - 200 OK: Operação bem-sucedida (usuário
 * - 201 CREATED: Recurso criado com sucesso
 * - 204 NO CONTENT: Operação bem-sucedida sem conteúdo na resposta
 * - 400 BAD REQUEST: Dados inválidos ou faltando campos obrigatórios
 * - 404 NOT FOUND: Profissional ou Bairro não encontrado
 * - 409 CONFLICT: Associação já existe ou outra violação de restrição
 * - 500 INTERNAL SERVER ERROR: Erro interno do servidor
 * 
 * Exemplo de Uso - Fluxo Completo:
 * 
 * 1. ASSOCIAR PROFISSIONAL EXISTENTE A BAIRRO:
 *    POST /api/profissional-bairro/associar
 *    {
 *        "profissionalId": 5,
 *        "bairroId": 10
 *    }
 *    Response 200: { "mensagem": "Associação realizada com sucesso" }
 * 
 * 2. CRIAR NOVO PROFISSIONAL COM BAIRROS:
 *    POST /api/profissional-bairro/criar-com-bairros
 *    {
 *        "nome": "Dr. João Silva",
 *        "especialidade": "Cardiologia",
 *        "numeroConselho": "123456-SP",
 *        "telefone": "(11) 98765-4321",
 *        "email": "joao@clinica.com",
 *        "endereco": "Rua das Flores, 100",
 *        "cidade": "São Paulo",
 *        "bairroIds": [1, 3, 5]
 *    }
 *    Response 201: ProfissionalComBairrosDTO com dados do novo profissional
 * 
 * 3. REMOVER ASSOCIAÇÃO:
 *    DELETE /api/profissional-bairro/5/bairro/10
 *    Response 204: (vazio - operação bem-sucedida)
 * 
 * ============================================================================
 */
@RestController
@RequestMapping("/api/profissional-bairro")
public class ProfissionalBairroController {

    /**
     * Injeção de dependência do Service especializado.
     * 
     * O Service conterá toda a lógica de negócio para relacionamento
     * entre Profissional e Bairro.
     * 
     * Anotação @Autowired permite que Spring injete automaticamente
     * uma instância de ProfissionalBairroService quando a classe
     * for criada.
     * 
     * Exemplo de injeção:
     * Spring detecta que precisa de ProfissionalBairroService
     * → Busca no contexto Spring
     * → Encontra uma única implementação (ProfissionalBairroService)
     * → Injeta automaticamente nesta variável
     * → Controller fica pronto para usar o Service
     */
    @Autowired
    private ProfissionalBairroService profissionalBairroService;

    // ========================================================================
    // ENDPOINT 1: ASSOCIAR PROFISSIONAL EXISTENTE A BAIRRO
    // ========================================================================

    /**
     * ▶ OPERAÇÃO 1: ASSOCIAR PROFISSIONAL EXISTENTE A BAIRRO
     * 
     * Endpoint: POST /api/profissional-bairro/associar
     * 
     * Descrição:
     * Associa um profissional já cadastrado no sistema a um bairro que também
     * já existe. Ambos devem estar persistidos no banco de dados.
     * 
     * Fluxo da Requisição:
     * 1. Cliente faz POST com JSON: {"profissionalId": 5, "bairroId": 10}
     * 2. Spring desserializa em AssociarProfissionalBairroRequest
     * 3. @Valid ativa validações @NotNull (não pode ser nulo)
     * 4. Se inválido → Retorna 400 BAD REQUEST com detalhes do erro
     * 5. Controller chama: service.associarProfissionalExistenteABairro(5, 10)
     * 6. Service executa lógica (validações, busca BD, associação, persistência)
     * 7. Se sucesso → Retorna 200 OK com mensagem
     * 8. Se erro (profissional não existe) → Service lança ObjectNotFoundException
     * 9. GlobalExceptionHandler captura → Retorna 404 NOT FOUND com detalhes
     * 
     * Validações Ativadas:
     * - profissionalId não pode ser nulo (validação DTO)
     * - bairroId não pode ser nulo (validação DTO)
     * - profissionalId deve ser > 0 (validação Service)
     * - bairroId deve ser > 0 (validação Service)
     * - Profissional deve existir em BD (validação Service)
     * - Bairro deve existir em BD (validação Service)
     * - Associação não pode já existir (validação Service)
     * 
     * Exemplo de Requisição Válida:
     * ```
     * POST /api/profissional-bairro/associar
     * Content-Type: application/json
     * 
     * {
     *     "profissionalId": 5,
     *     "bairroId": 10
     * }
     * ```
     * 
     * Exemplo de Resposta Sucesso:
     * ```
     * HTTP 200 OK
     * Content-Type: application/json
     * 
     * {
     *     "mensagem": "Profissional ID 5 (Dr. João Silva) associado com sucesso ao Bairro ID 10 (Centro)"
     * }
     * ```
     * 
     * Exemplo de Resposta Erro - Profissional não encontrado:
     * ```
     * HTTP 404 NOT FOUND
     * Content-Type: application/json
     * 
     * {
     *     "erro": "Profissional não encontrado",
     *     "detalhe": "Profissional com ID 999 não existe no banco de dados",
     *     "timestamp": "2026-05-01T10:30:00"
     * }
     * ```
     * 
     * Exemplo de Resposta Erro - Dados inválidos:
     * ```
     * HTTP 400 BAD REQUEST
     * Content-Type: application/json
     * 
     * {
     *     "erro": "Dados inválidos",
     *     "detalhe": "profissionalId não pode ser nulo",
     *     "timestamp": "2026-05-01T10:30:00"
     * }
     * ```
     * 
     * @param request DTO contendo profissionalId e bairroId
     * @return ResponseEntity com HTTP 200 OK e mensagem de sucesso
     * @throws ObjectNotFoundException se profissional ou bairro não existem
     * @throws IllegalStateException se associação já existe
     * 
     * @see AssociarProfissionalBairroRequest
     * @see ProfissionalBairroService#associarProfissionalExistenteABairro(Integer, Integer)
     */
    @PostMapping("/associar")
    public ResponseEntity<String> associarProfissionalABairro(
            @Valid @RequestBody AssociarProfissionalBairroRequest request) {
        
        // ▶ Extrair IDs do request
        Integer profissionalId = request.getProfissionalId();
        Integer bairroId = request.getBairroId();
        
        // ▶ Log de informação (útil para debugging)
        System.out.println(String.format(
            "🔗 Tentando associar: Profissional %d → Bairro %d",
            profissionalId, bairroId
        ));
        
        // ▶ Chamar Service para fazer a lógica de negócio
        profissionalBairroService.associarProfissionalExistenteABairro(profissionalId, bairroId);
        
        // ▶ Retornar resposta de sucesso
        String mensagem = String.format(
            "✅ Profissional ID %d associado com sucesso ao Bairro ID %d",
            profissionalId, bairroId
        );
        
        return ResponseEntity.ok(mensagem);
    }

    // ========================================================================
    // ENDPOINT 2: CRIAR NOVO PROFISSIONAL COM BAIRROS
    // ========================================================================

    /**
     * ▶ OPERAÇÃO 2: CRIAR NOVO PROFISSIONAL E ASSOCIAR A MÚLTIPLOS BAIRROS
     * 
     * Endpoint: POST /api/profissional-bairro/criar-com-bairros
     * 
     * Descrição:
     * Cria um novo profissional com todos os seus dados e associa imediatamente
     * a múltiplos bairros em uma operação atômica (tudo ou nada).
     * 
     * Fluxo da Requisição:
     * 1. Cliente faz POST com JSON contendo dados do profissional e lista de bairros
     * 2. Spring desserializa em CriarProfissionalComBairrosRequest
     * 3. @Valid ativa validações:
     *    - Nome: @NotBlank, @Size(3-150)
     *    - Email: @NotBlank, @Email
     *    - Telefone: @NotBlank, @Pattern(formato brasileiro)
     *    - BairroIds: @NotEmpty (lista com pelo menos 1 elemento)
     * 4. Se inválido → Retorna 400 BAD REQUEST com detalhes
     * 5. Controller chama: service.criarNovoProfissionalComBairros(...)
     * 6. Service executa lógica transacional:
     *    - Valida dados do profissional
     *    - Busca todos os bairros na lista
     *    - Cria novo Profissional
     *    - Associa a cada bairro
     *    - Persiste (cascata automática)
     * 7. Se algum bairro não existe → Transação inteira é revertida (ROLLBACK)
     * 8. Se sucesso → Transação é confirmada (COMMIT)
     * 9. Retorna 201 CREATED com ProfissionalComBairrosDTO
     * 
     * Transações (@Transactional):
     * - Garante atomicidade (tudo ou nada)
     * - Se erro em qualquer etapa → Todos os dados são revertidos
     * - Se sucesso em todas → Todos os dados são persistidos
     * 
     * Validações Ativadas:
     * - Nome @NotBlank, @Size(3-150)
     * - Especialidade @NotBlank, @Size(3-100)
     * - NumeroConselho @NotBlank, @Size(5-50)
     * - Telefone @NotBlank, @Pattern (valida formato brasileiro (XX) 9XXXX-XXXX)
     * - Email @NotBlank, @Email (valida formato de e-mail)
     * - Endereco @NotBlank, @Size(5-255)
     * - Cidade @NotBlank, @Size(3-100)
     * - BairroIds @NotEmpty (lista não vazia)
     * - Todos os bairros devem existir em BD (validação Service)
     * 
     * Exemplo de Requisição Válida:
     * ```
     * POST /api/profissional-bairro/criar-com-bairros
     * Content-Type: application/json
     * 
     * {
     *     "nome": "Dr. Carlos Oliveira",
     *     "especialidade": "Ortopedia",
     *     "numeroConselho": "789456-RJ",
     *     "telefone": "(21) 99876-5432",
     *     "email": "carlos@clinica.com",
     *     "endereco": "Avenida Brasil, 2000",
     *     "cidade": "Rio de Janeiro",
     *     "bairroIds": [1, 3, 5, 7]
     * }
     * ```
     * 
     * Exemplo de Resposta Sucesso:
     * ```
     * HTTP 201 CREATED
     * Content-Type: application/json
     * 
     * {
     *     "id": 15,
     *     "nome": "Dr. Carlos Oliveira",
     *     "especialidade": "Ortopedia",
     *     "numeroConselho": "789456-RJ",
     *     "telefone": "(21) 99876-5432",
     *     "email": "carlos@clinica.com",
     *     "endereco": "Avenida Brasil, 2000",
     *     "cidade": "Rio de Janeiro",
     *     "bairrosAtendidos": [
     *         {"id": 1, "nome": "Centro", "cidade": "Rio de Janeiro"},
     *         {"id": 3, "nome": "Copacabana", "cidade": "Rio de Janeiro"},
     *         {"id": 5, "nome": "Ipanema", "cidade": "Rio de Janeiro"},
     *         {"id": 7, "nome": "Leblon", "cidade": "Rio de Janeiro"}
     *     ]
     * }
     * ```
     * 
     * Exemplo de Resposta Erro - Telefone inválido:
     * ```
     * HTTP 400 BAD REQUEST
     * Content-Type: application/json
     * 
     * {
     *     "erro": "Dados inválidos",
     *     "detalhe": "Telefone deve estar no formato (XX) 9XXXX-XXXX ou (XX) XXXX-XXXX",
     *     "timestamp": "2026-05-01T10:30:00"
     * }
     * ```
     * 
     * Exemplo de Resposta Erro - Bairro não existe:
     * ```
     * HTTP 404 NOT FOUND
     * Content-Type: application/json
     * 
     * {
     *     "erro": "Bairro não encontrado",
     *     "detalhe": "Bairro com ID 999 não existe no banco de dados. Transação revertida.",
     *     "timestamp": "2026-05-01T10:30:00"
     * }
     * ```
     * 
     * @param request DTO contendo dados do profissional e lista de bairroIds
     * @return ResponseEntity com HTTP 201 CREATED e ProfissionalComBairrosDTO
     * @throws ObjectNotFoundException se algum bairro não existir
     * @throws IllegalArgumentException se dados inválidos
     * 
     * @see CriarProfissionalComBairrosRequest
     * @see ProfissionalComBairrosDTO
     * @see ProfissionalBairroService#criarNovoProfissionalComBairros(String, String, String, String, String, String, String, java.util.List)
     */
    @PostMapping("/criar-com-bairros")
    public ResponseEntity<ProfissionalComBairrosDTO> criarProfissionalComBairros(
            @Valid @RequestBody CriarProfissionalComBairrosRequest request) {
        
        // ▶ Log de informação
        System.out.println(String.format(
            "👤 Criando novo profissional: %s com %d bairros",
            request.getNome(),
            request.getBairroIds().size()
        ));
        
        // ▶ Chamar Service para fazer a lógica de negócio
        ProfissionalComBairrosDTO resultado = profissionalBairroService.criarNovoProfissionalComBairros(
            request.getNome(),
            request.getEspecialidade(),
            request.getNumeroConselho(),
            request.getTelefone(),
            request.getEmail(),
            request.getEndereco(),
            request.getCidade(),
            request.getBairroIds()
        );
        
        // ▶ Retornar resposta com HTTP 201 CREATED
        // 201 = Recurso foi criado com sucesso
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // ========================================================================
    // ENDPOINT 3: REMOVER ASSOCIAÇÃO ENTRE PROFISSIONAL E BAIRRO
    // ========================================================================

    /**
     * ▶ OPERAÇÃO 3: REMOVER ASSOCIAÇÃO ENTRE PROFISSIONAL E BAIRRO
     * 
     * Endpoint: DELETE /api/profissional-bairro/{profissionalId}/bairro/{bairroId}
     * 
     * Descrição:
     * Remove a associação entre um profissional e um bairro específico.
     * Ambos os registros são preservados no banco - apenas o relacionamento
     * é removido.
     * 
     * Fluxo da Requisição:
     * 1. Cliente faz DELETE: /api/profissional-bairro/5/bairro/10
     * 2. Spring extrai IDs da URL via @PathVariable:
     *    - profissionalId = 5
     *    - bairroId = 10
     * 3. Controller chama: service.desassociarProfissionalDeBairro(5, 10)
     * 4. Service executa lógica:
     *    - Valida IDs (não nulo, > 0)
     *    - Busca profissional no BD
     *    - Busca bairro no BD
     *    - Verifica se associação existe
     *    - Remove do relacionamento ManyToMany
     *    - Persiste mudanças
     * 5. Se sucesso → Retorna 204 NO CONTENT (vazio)
     * 6. Se profissional não existe → Retorna 404 NOT FOUND
     * 7. Se bairro não existe → Retorna 404 NOT FOUND
     * 
     * Importante:
     * - Profissional não é deletado
     * - Bairro não é deletado
     * - Apenas o relacionamento é removido
     * - Operação é reversível (pode-se associar novamente depois)
     * 
     * Validações Ativadas:
     * - profissionalId deve ser > 0 (validação Service)
     * - bairroId deve ser > 0 (validação Service)
     * - Profissional deve existir em BD (validação Service)
     * - Bairro deve existir em BD (validação Service)
     * - Associação deve existir (validação Service)
     * 
     * Exemplo de Requisição:
     * ```
     * DELETE /api/profissional-bairro/5/bairro/10
     * ```
     * 
     * Exemplo de Resposta Sucesso:
     * ```
     * HTTP 204 NO CONTENT
     * (corpo vazio - apenas status HTTP indica sucesso)
     * ```
     * 
     * Exemplo de Resposta Erro - Profissional não encontrado:
     * ```
     * HTTP 404 NOT FOUND
     * Content-Type: application/json
     * 
     * {
     *     "erro": "Profissional não encontrado",
     *     "detalhe": "Profissional com ID 999 não existe no banco de dados",
     *     "timestamp": "2026-05-01T10:30:00"
     * }
     * ```
     * 
     * Exemplo de Resposta Erro - Bairro não encontrado:
     * ```
     * HTTP 404 NOT FOUND
     * Content-Type: application/json
     * 
     * {
     *     "erro": "Bairro não encontrado",
     *     "detalhe": "Bairro com ID 999 não existe no banco de dados",
     *     "timestamp": "2026-05-01T10:30:00"
     * }
     * ```
     * 
     * Exemplo de Resposta Erro - Associação não existe:
     * ```
     * HTTP 409 CONFLICT
     * Content-Type: application/json
     * 
     * {
     *     "erro": "Operação inválida",
     *     "detalhe": "Profissional ID 5 não está associado ao Bairro ID 10",
     *     "timestamp": "2026-05-01T10:30:00"
     * }
     * ```
     * 
     * @param profissionalId ID do profissional (extraído da URL)
     * @param bairroId ID do bairro (extraído da URL)
     * @return ResponseEntity com HTTP 204 NO CONTENT (corpo vazio)
     * @throws ObjectNotFoundException se profissional ou bairro não existem
     * @throws IllegalStateException se associação não existe
     * 
     * @see ProfissionalBairroService#desassociarProfissionalDeBairro(Integer, Integer)
     */
    @DeleteMapping("/{profissionalId}/bairro/{bairroId}")
    public ResponseEntity<Void> desassociarProfissionalDeBairro(
            @PathVariable Integer profissionalId,
            @PathVariable Integer bairroId) {
        
        // ▶ Log de informação
        System.out.println(String.format(
            "🔓 Removendo associação: Profissional %d ← Bairro %d",
            profissionalId, bairroId
        ));
        
        // ▶ Chamar Service para fazer a lógica de negócio
        profissionalBairroService.desassociarProfissionalDeBairro(profissionalId, bairroId);
        
        // ▶ Retornar resposta vazia com HTTP 204 NO CONTENT
        // 204 = Operação bem-sucedida, sem conteúdo na resposta
        return ResponseEntity.noContent().build();
    }
}
