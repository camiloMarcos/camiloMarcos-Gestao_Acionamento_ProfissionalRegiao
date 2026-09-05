package br.com.univida_test.demo.dtos.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * ============================================================================
 * DTO: CRIAR PROFISSIONAL COM BAIRROS - REQUEST
 * ============================================================================
 * 
 * Representa uma requisição para criar um novo profissional e associá-lo
 * a múltiplos bairros em uma única operação transacional.
 * 
 * Responsabilidades:
 * - Receber dados de um novo profissional (7 campos obrigatórios)
 * - Receber lista de IDs de bairros para associação
 * - Validar todos os campos seguindo regras de negócio
 * - Servir como contrato da API para criação com associação
 * 
 * Caso de Uso:
 * Um novo profissional será criado e imediatamente associado a vários
 * bairros em uma operação atômica (tudo ou nada).
 * 
 * Exemplo de Uso:
 * ```json
 * POST /api/profissional-bairro/criar-com-bairros
 * {
 *     "nome": "Dr. João Silva",
 *     "especialidade": "Cardiologia",
 *     "numeroConselho": "123456-SP",
 *     "telefone": "(11) 98765-4321",
 *     "email": "joao@clinica.com",
 *     "endereco": "Rua das Flores, 100",
 *     "cidade": "São Paulo",
 *     "bairroIds": [1, 3, 5, 7]
 * }
 * ```
 * 
 * Validações Ativadas Pelo @NotBlank, @Email, @Pattern, @NotEmpty:
 * - Todos os 7 campos obrigatórios devem estar preenchidos
 * - Email deve estar em formato válido
 * - Telefone deve estar em formato brasileira (11 dígitos com formatação)
 * - Lista de bairros não pode estar vazia (pelo menos um bairro)
 * 
 * Fluxo na Service:
 * 1. Spring desserializa JSON em CriarProfissionalComBairrosRequest
 * 2. Validações (estruturais) são aplicadas automaticamente
 * 3. Service realiza validações adicionais (existência de bairros, etc)
 * 4. Service cria Profissional novo
 * 5. Service associa a cada Bairro em lista
 * 6. Tudo é persistido em transação atômica
 * 7. Response retorna o profissional criado com seus bairros
 * 
 * ============================================================================
 */
public class CriarProfissionalComBairrosRequest {

    /**
     * Nome completo do profissional.
     * 
     * Validações:
     * - @NotBlank: Não pode estar vazio, null ou conter apenas espaços
     * - @Size: Mínimo 3 caracteres, máximo 150 caracteres
     * 
     * Exemplo de valor válido: "Dr. João Silva"
     * Exemplo de valor INVÁLIDO: "Jo" (muito curto)
     * Exemplo de valor INVÁLIDO: "" (vazio)
     */
    @NotBlank(message = "Nome do profissional não pode estar vazio")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    private String nome;

    /**
     * Especialidade médica do profissional.
     * 
     * Exemplos válidos: "Cardiologia", "Neurologia", "Ortopedia", "Fisioterapia"
     * 
     * Validações:
     * - @NotBlank: Não pode estar vazio
     * - @Size: Mínimo 3 caracteres, máximo 100 caracteres
     * 
     * Exemplo de valor válido: "Cardiologia"
     * Exemplo de valor INVÁLIDO: "" (vazio)
     */
    @NotBlank(message = "Especialidade não pode estar vazia")
    @Size(min = 3, max = 100, message = "Especialidade deve ter entre 3 e 100 caracteres")
    private String especialidade;

    /**
     * Número de registro no conselho profissional.
     * 
     * Exemplos válidos: "123456-SP", "789012-MG", "CREMESP12345"
     * 
     * Validações:
     * - @NotBlank: Não pode estar vazio
     * - @Size: Mínimo 5 caracteres, máximo 50 caracteres
     * 
     * Exemplo de valor válido: "123456-SP"
     * Exemplo de valor INVÁLIDO: "123" (muito curto)
     */
    @NotBlank(message = "Número do conselho não pode estar vazio")
    @Size(min = 5, max = 50, message = "Número do conselho deve ter entre 5 e 50 caracteres")
    private String numeroConselho;

    /**
     * Telefone para contato do profissional.
     * 
     * Formato esperado: Telefone brasileiro com 11 dígitos
     * Exemplos válidos: "(11) 98765-4321", "(21) 99876-5432"
     * 
     * Validações:
     * - @NotBlank: Não pode estar vazio
     * - @Pattern: Deve seguir formato de telefone brasileiro
     * 
     * Expressão Regular: \\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}
     * Explicação:
     * - \\(\\d{2}\\): Abre parêntese, 2 dígitos (DDD), fecha parêntese
     * - \\s?: Espaço opcional
     * - \\d{4,5}: 4 ou 5 dígitos
     * - -: Hífen obrigatório
     * - \\d{4}: 4 dígitos finais
     * 
     * Exemplo de valor válido: "(11) 98765-4321"
     * Exemplo de valor INVÁLIDO: "11987654321" (sem formatação)
     * Exemplo de valor INVÁLIDO: "9876-5432" (sem DDD)
     */
    @NotBlank(message = "Telefone não pode estar vazio")
    @Pattern(
        regexp = "\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}",
        message = "Telefone deve estar no formato (XX) 9XXXX-XXXX ou (XX) XXXX-XXXX"
    )
    private String telefone;

    /**
     * E-mail para contato do profissional.
     * 
     * Validações:
     * - @NotBlank: Não pode estar vazio
     * - @Email: Deve estar em formato de e-mail válido
     * 
     * Exemplo de valor válido: "joao@clinica.com"
     * Exemplo de valor INVÁLIDO: "joao@" (e-mail incompleto)
     * Exemplo de valor INVÁLIDO: "joao.clinica.com" (sem @)
     */
    @NotBlank(message = "E-mail não pode estar vazio")
    @Email(message = "E-mail deve ser válido (ex: profissional@clinica.com)")
    private String email;

    /**
     * Endereço completo do profissional.
     * 
     * Deve incluir rua, número e complementos relevantes.
     * 
     * Validações:
     * - @NotBlank: Não pode estar vazio
     * - @Size: Mínimo 5 caracteres, máximo 255 caracteres
     * 
     * Exemplo de valor válido: "Rua das Flores, 100, Apto 201"
     * Exemplo de valor INVÁLIDO: "" (vazio)
     * Exemplo de valor INVÁLIDO: "Rua A" (muito genérico, mas aceito)
     */
    @NotBlank(message = "Endereço não pode estar vazio")
    @Size(min = 5, max = 255, message = "Endereço deve ter entre 5 e 255 caracteres")
    private String endereco;

    /**
     * Cidade onde o profissional reside/trabalha.
     * 
     * Exemplos válidos: "São Paulo", "Rio de Janeiro", "Salvador"
     * 
     * Validações:
     * - @NotBlank: Não pode estar vazio
     * - @Size: Mínimo 3 caracteres, máximo 100 caracteres
     * 
     * Exemplo de valor válido: "São Paulo"
     * Exemplo de valor INVÁLIDO: "" (vazio)
     * Exemplo de valor INVÁLIDO: "SP" (muito curto)
     */
    @NotBlank(message = "Cidade não pode estar vazia")
    @Size(min = 3, max = 100, message = "Cidade deve ter entre 3 e 100 caracteres")
    private String cidade;

    /**
     * Lista de IDs dos bairros que o profissional atenderá.
     * 
     * Esta lista deve conter IDs de bairros que já existem no banco de dados.
     * O profissional será associado a cada um destes bairros na mesma transação.
     * 
     * Validações:
     * - @NotEmpty: Não pode estar vasia, null ou conter zero elementos
     * - Service valida se todos os IDs existem no banco de dados
     * 
     * Exemplo de valor válido: [1, 3, 5, 7]
     * Exemplo de valor INVÁLIDO: [] (vazio)
     * Exemplo de valor INVÁLIDO: null (não fornecido)
     * 
     * Nota de Implementação:
     * Service buscará cada Bairro pelo ID e adicionará o novo Profissional
     * em uma transação, garantindo que tudo é persistido ou nada é.
     */
    @NotEmpty(message = "Lista de bairros não pode estar vazia - pelo menos um bairro obrigatório")
    private List<Integer> bairroIds;

    // ========================================================================
    // CONSTRUTORES
    // ========================================================================

    /**
     * Construtor padrão sem argumentos.
     * Necessário para que o Spring consiga desserializar o JSON.
     */
    public CriarProfissionalComBairrosRequest() {
    }

    /**
     * Construtor com todos os campos.
     *
     * @param nome           Nome do profissional
     * @param especialidade  Especialidade médica
     * @param numeroConselho Número do conselho
     * @param telefone       Telefone para contato
     * @param email          E-mail para contato
     * @param endereco       Endereço completo
     * @param cidade         Cidade
     * @param bairroIds      Lista de IDs dos bairros
     */
    public CriarProfissionalComBairrosRequest(String nome, String especialidade, String numeroConselho,
            String telefone, String email, String endereco, String cidade, List<Integer> bairroIds) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.numeroConselho = numeroConselho;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.cidade = cidade;
        this.bairroIds = bairroIds;
    }

    // ========================================================================
    // GETTERS E SETTERS
    // ========================================================================

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

    public List<Integer> getBairroIds() {
        return bairroIds;
    }

    public void setBairroIds(List<Integer> bairroIds) {
        this.bairroIds = bairroIds;
    }

    // ========================================================================
    // MÉTODOS UTILITÁRIOS
    // ========================================================================

    @Override
    public String toString() {
        return "CriarProfissionalComBairrosRequest{" +
                "nome='" + nome + '\'' +
                ", especialidade='" + especialidade + '\'' +
                ", numeroConselho='" + numeroConselho + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", endereco='" + endereco + '\'' +
                ", cidade='" + cidade + '\'' +
                ", bairroIds=" + bairroIds +
                '}';
    }
}
