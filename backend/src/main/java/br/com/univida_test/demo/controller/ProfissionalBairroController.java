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
import br.com.univida_test.demo.mapper.ProfissionalMapper;
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
 * 1. ASSOCIAR PROFISSIONAL EXISTENTE A BAIRRO: 
 * 2. CRIAR NOVO PROFISSIONAL COM BAIRROS:
 * 3. REMOVER ASSOCIAÇÃO:
 * ============================================================================
 */
@RestController
@RequestMapping("/api/profissional-bairro")
public class ProfissionalBairroController {

    @Autowired
    private ProfissionalBairroService profissionalBairroService;

    @Autowired
    private ProfissionalMapper profissionalMapper;

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
      
     * Validações Ativadas:
     * - profissionalId não pode ser nulo (validação DTO)
     * - bairroId não pode ser nulo (validação DTO)
     * - profissionalId deve ser > 0 (validação Service)
     * - bairroId deve ser > 0 (validação Service)
     * - Profissional deve existir em BD (validação Service)
     * - Bairro deve existir em BD (validação Service)
     * - Associação não pode já existir (validação Service)
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
      
     * Descrição:
     * Cria um novo profissional com todos os seus dados e associa imediatamente
     * a múltiplos bairros em uma operação atômica (tudo ou nada). 
      
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
        // Service retorna entidade Profissional; convertemos para DTO aqui
        br.com.univida_test.demo.models.Profissional resultadoEntidade = profissionalBairroService.criarNovoProfissionalComBairros(
            request.getNome(),
            request.getEspecialidade(),
            request.getNumeroConselho(),
            request.getTelefone(),
            request.getEmail(),
            request.getEndereco(),
            request.getCidade(),
            request.getBairroIds()
        );

        // ▶ Converter entidade para DTO e retornar
        ProfissionalComBairrosDTO resultado = profissionalMapper.toComBairrosDto(resultadoEntidade);
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

     * Validações Ativadas:
     * - profissionalId deve ser > 0 (validação Service)
     * - bairroId deve ser > 0 (validação Service)
     * - Profissional deve existir em BD (validação Service)
     * - Bairro deve existir em BD (validação Service)
     * - Associação deve existir (validação Service)
     * 
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