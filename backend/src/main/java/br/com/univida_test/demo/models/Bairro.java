package br.com.univida_test.demo.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

/**
 * ============================================================================
 * ENTIDADE: BAIRRO -> Representa um bairro da região de atendimento com seus profissionais
 * associados.
 * ============================================================================
 * Responsabilidades:
 * - Manter dados do bairro (nome, cidade, nível de perigo)
 * - Gerenciar lista de profissionais que atendem este bairro
 * - Manter integridade bidirecional com Profissional
 * - Validar operações de associação/dissociação
 * 
 * Relacionamento:
 * - ManyToMany com Profissional (lado OWNING - controla tabela de junção)
 * - Tabela de junção: bairro_profissional */
@Entity
public class Bairro {

    // ========================================================================
    // ATRIBUTOS
    // ========================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String cidade;
    private boolean perigo_Distante;

    /**
     * Lista de profissionais que atendem este bairro.
     * 
     * Configuração JPA:
     * - @ManyToMany: Relacionamento muitos-para-muitos bilateral
     * - @JoinTable: Define a tabela de junção "bairro_profissional"
     * - joinColumns: FK para Bairro nesta tabela
     * - inverseJoinColumns: FK para Profissional nesta tabela
     * - CascadeType.DETACH: Remove cascade ao desanexar
     * @JsonIgnore: Previne serialização circular ao retornar JSON
     */
    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "bairro_profissional",
        joinColumns = @JoinColumn(name = "bairro_id"),
        inverseJoinColumns = @JoinColumn(name = "profissional_id")
    )
    private List<Profissional> profissionais = new ArrayList<>();

    // ========================================================================
    // CONSTRUTORES
    // ========================================================================
    public Bairro() {
    }

    public Bairro(Integer id, String nome, String cidade, boolean perigo_Distante) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
        this.perigo_Distante = perigo_Distante;
    }

    // ========================================================================
    // MÉTODOS AUXILIARES: GERENCIAMENTO DE ASSOCIAÇÕES
    // ========================================================================

    /* ▶ OPERAÇÃO 1: ADICIONAR PROFISSIONAL JÁ CADASTRADO
     * 
     * Associa um profissional que já existe no DB a este bairro.
     * Este método é usado quando o profissional já tem ID (foi persistido).
     *
     * Validações:
     * - Profissional não pode ser nulo
     * - Profissional deve ter ID válido (>0)
     * - Profissional não pode estar já associado
     * - Garante sincronização bidirecional */
    public void adicionarProfissionalExistente(Profissional profissional) {
        
        // ▶ Validação 1: Verificar se profissional é nulo
        if (profissional == null) {
            throw new IllegalArgumentException(
                "Profissional não pode ser nulo ao adicionar a bairro"
            );
        }

        // ▶ Validação 2: Verificar se profissional tem ID válido
        if (profissional.getId() == null || profissional.getId() <= 0) {
            throw new IllegalArgumentException(
                "Profissional deve estar cadastrado (ID não pode ser nulo ou menor que 1)"
            );
        }

        // ▶ Validação 3: Verificar se já não está associado
        boolean jaAssociado = this.profissionais.stream()
            .anyMatch(p -> Objects.equals(p.getId(), profissional.getId()));

        if (jaAssociado) {
            throw new IllegalStateException(
                String.format(
                    "Profissional ID %d (%s) já está associado ao bairro ID %d (%s)",
                    profissional.getId(), profissional.getNome(),
                    this.id, this.nome
                )
            );
        }

        // ▶ Adicionar: Mantém sincronização bidirecional
        this.profissionais.add(profissional);
        profissional.getBairrosAtendidos().add(this);
    }

    /** ▶ OPERAÇÃO 2: ADICIONAR NOVO PROFISSIONAL (ainda não cadastrado)
     * Cria uma associação com um novo profissional que ainda não foi
     * persistido no banco de dados. Este método é usado para criar
     * profissional e associar ao bairro em uma operação.
     *
     * Validações: as mesma da Operação 1.*/
    public void adicionarNovoProfissional(Profissional novoProfissional) {
        
        // ▶ Validação 1: Verificar se profissional é nulo
        if (novoProfissional == null) {
            throw new IllegalArgumentException(
                "Profissional não pode ser nulo ao adicionar a bairro"
            );
        }

        // ▶ Validação 2: Verificar se profissional JÁ tem ID (deve ser novo)
        if (novoProfissional.getId() != null && novoProfissional.getId() > 0) {
            throw new IllegalArgumentException(
                "Use adicionarProfissionalExistente() para profissionais já cadastrados. "
                + "Este método é para novos profissionais sem ID."
            );
        }

        // ▶ Validação 3: Verificar dados obrigatórios do novo profissional
        if (novoProfissional.getNome() == null || novoProfissional.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Nome do profissional é obrigatório"
            );
        }

        if (novoProfissional.getEspecialidade() == null || novoProfissional.getEspecialidade().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Especialidade do profissional é obrigatória"
            );
        }

        // ▶ Inicializar lista de bairros se necessário
        if (novoProfissional.getBairrosAtendidos() == null) {
            novoProfissional.setBairrosAtendidos(new ArrayList<>());
        }

        // ▶ Adicionar: Mantém sincronização bidirecional
        this.profissionais.add(novoProfissional);
        novoProfissional.getBairrosAtendidos().add(this);
    }

    /** ▶ OPERAÇÃO 3: REMOVER PROFISSIONAL DO BAIRRO
     * Remove a associação entre este bairro e um profissional específico.
     * O profissional NÃO É DELETADO DO DB */
    public void removerProfissional(Integer profissionalId) {
        
        // ▶ Validação: ID não pode ser nulo
        if (profissionalId == null) {
            throw new IllegalArgumentException(
                "ID do profissional não pode ser nulo"
            );
        }

        // ▶ Buscar profissional na lista
        Profissional profissionalParaRemover = this.profissionais.stream()
            .filter(p -> Objects.equals(p.getId(), profissionalId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                String.format(
                    "Profissional ID %d não está associado ao bairro ID %d (%s)",
                    profissionalId, this.id, this.nome
                )
            ));

        // ▶ Remover: Mantém sincronização bidirecional
        this.profissionais.remove(profissionalParaRemover);
        profissionalParaRemover.getBairrosAtendidos().remove(this);
    }

    // ========================================================================
    // MÉTODOS AUXILIARES: CONSULTAS E VERIFICAÇÕES
    // ========================================================================


    /** Verifica se um profissional está associado a este bairro.*/
    public boolean contemProfissional(Integer profissionalId) {
        if (profissionalId == null) {
            return false;
        }
        return this.profissionais.stream()
            .anyMatch(p -> Objects.equals(p.getId(), profissionalId));
    }

    /** Obtém um profissional específico associado a este bairro pelo ID. */
    public Profissional obterProfissional(Integer profissionalId) {
        if (profissionalId == null) {
            throw new IllegalArgumentException(
                "ID do profissional não pode ser nulo"
            );
        }

        return this.profissionais.stream()
            .filter(p -> Objects.equals(p.getId(), profissionalId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                String.format(
                    "Profissional ID %d não encontrado neste bairro",
                    profissionalId
                )
            ));
    }

    /** Retorna a quantidade de profissionais associados a este bairro. */
    public int countProfissionais() {
        return this.profissionais != null ? this.profissionais.size() : 0;
    }

    /** Remove TODOS os profissionais associados a este bairro. 
     * ⚠️ OPERAÇÃO DESTRUTIVA: Remove todas as associações
     * 
     * Mantém sincronização bidirecional com todos os profissionais.  */
    public void limparProfissionais() {
        // ▶ Remove bidirecionalidade: Remove este bairro da lista de cada profissional
        this.profissionais.forEach(prof -> 
            prof.getBairrosAtendidos().remove(this)
        );
        // ▶ Limpa a lista local
        this.profissionais.clear();
    }

    /**⚠️ Método legado para manter compatibilidade com código existente.
     * ⚠️ DESCONTINUADO: Prefira usar adicionarProfissionalExistente()
     * 
     * Este método foi substituído por versões mais especializadas que
     * fazem validação adequada.*/
    @Deprecated(since = "2.0", forRemoval = true)
    public void addProfissional(Profissional profissional) {
        if (this.profissionais == null) {
            this.profissionais = new ArrayList<>();
        }
        if (profissional != null && profissional.getId() != null) {
            adicionarProfissionalExistente(profissional);
        }
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

    public boolean isPerigo_Distante() {
        return perigo_Distante;
    }

    public void setPerigo_Distante(boolean perigo_Distante) {
        this.perigo_Distante = perigo_Distante;
    }

    public List<Profissional> getProfissionais() {
        return profissionais;
    }

    /** Define a lista de profissionais. Se nulo, inicializa com lista vazia. */
    public void setProfissionais(List<Profissional> profissionais) {
        this.profissionais = profissionais != null ? profissionais : new ArrayList<>();
    }
}
