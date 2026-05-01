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
import jakarta.persistence.ManyToMany;

/**
 * ============================================================================
 * ENTIDADE: PROFISSIONAL -> Representa um profissional da saúde que atende em determinados bairros.
 * ============================================================================
 *  
 * Responsabilidades:
 * - Manter dados do profissional (nome, especialidade, contato, etc.)
 * - Manter lista de bairros que este profissional atende
 * - Manter integridade bidirecional com Bairro (lado mapped)
 * - Sincronizar mudanças com o lado owning (Bairro)
 * 
 * Relacionamento:
 * - ManyToMany com Bairro (lado MAPPED - não controla tabela de junção)
 * - Bairro é o lado owning (@JoinTable está lá) */
@Entity
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String especialidade;
    private String numeroConselho;
    private String telefone;
    private String email;
    private String endereco;
    private String cidade;

    /** Lista de bairros que este profissional atende.
     * 
     * Configuração JPA:
     * - @ManyToMany(mappedBy): Indica que Bairro é o lado owning
     * - Não pode ter @JoinTable aqui (está em Bairro)
     * - CascadeType.PERSIST e MERGE passam cascata de operações
     * 
     * Importante:
     * Como este é o lado mapped, operações nesta lista podem não ser
     * persistidas se feitas diretamente. Use os métodos de Bairro para
     * adicionar/remover associações quando possível.
     * 
     * @JsonIgnore: Previne serialização circular ao retornar JSON
     */
    @JsonIgnore
    @ManyToMany(mappedBy = "profissionais", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Bairro> bairrosAtendidos = new ArrayList<>();


    // ========================================================================
    // CONSTRUTORES
    // ========================================================================
    public Profissional() {
    }

    public Profissional(Integer id, String nome, String especialidade, String numeroConselho,
            String telefone, String email, String endereco, String cidade, List<Bairro> bairrosAtendidos) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.numeroConselho = numeroConselho;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.cidade = cidade;
        this.bairrosAtendidos = bairrosAtendidos != null ? bairrosAtendidos : new ArrayList<>();
    }

    // ========================================================================
    // MÉTODOS AUXILIARES: GERENCIAMENTO DE SINCRONIZAÇÃO
    // ========================================================================

    /** ▶ MÉTODO AUXILIAR 1: ADICIONAR BAIRRO JÁ CADASTRADO
     * 
     * Associa um bairro já existente no DB a este profissional. É usado quando o bairro já tem ID (foi persistido).
     
     * ✅ SINCRONIZAÇÃO AUTOMÁTICA BIDIRECIONAL:
     * Este método agora sincroniza automaticamente ambos os lados do relacionamento.
     * Você NÃO precisa usar métodos em Bairro.java anymore!
     *
     * Validações:
     * - Bairro não pode ser nulo
     * - Bairro deve ter ID válido (>0)
     * - Bairro não pode estar já associado
     * - Mantém sincronização automática com Bairro */
    public void adicionarBairro(Bairro bairro) {
        
        // ▶ Validação 1: Bairro não pode ser nulo
        if (bairro == null) {
            throw new IllegalArgumentException(
                "Bairro não pode ser nulo ao adicionar a profissional"
            );
        }

        // ▶ Validação 2: Verificar se bairro tem ID válido
        if (bairro.getId() == null || bairro.getId() <= 0) {
            throw new IllegalArgumentException(
                "Bairro deve estar cadastrado (ID não pode ser nulo ou menor que 1)"
            );
        }

        // ▶ Validação 3: Verificar se já não está associado
        boolean jaAssociado = this.bairrosAtendidos.stream()
            .anyMatch(b -> Objects.equals(b.getId(), bairro.getId()));

        if (jaAssociado) {
            throw new IllegalStateException(
                String.format(
                    "Bairro ID %d (%s) já está na lista de atendimento do profissional ID %d (%s)",
                    bairro.getId(), bairro.getNome(),
                    this.id, this.nome
                )
            );
        }

        // ▶ Adicionar: Sincroniza bidirecionalidade
        this.bairrosAtendidos.add(bairro);
        bairro.getProfissionais().add(this);
    }


    /** ▶ MÉTODO AUXILIAR 1B: ADICIONAR NOVO BAIRRO (ainda não cadastrado)
     * 
     * Cria uma associação com um NOVO bairro que ainda não foi persistido no DB. Este método é usado para criar bairro e associar ao profissional em uma operação.*/
    public void adicionarNovoBairro(Bairro novoBairro) {
        
        // ▶ Validação 1: Bairro não pode ser nulo
        if (novoBairro == null) {
            throw new IllegalArgumentException(
                "Bairro não pode ser nulo ao adicionar a profissional"
            );
        }

        // ▶ Validação 2: Verificar se bairro JÁ tem ID (deve ser novo)
        if (novoBairro.getId() != null && novoBairro.getId() > 0) {
            throw new IllegalArgumentException(
                "Use adicionarBairro() para bairros já cadastrados. "
                + "Este método é para novos bairros sem ID."
            );
        }

        // ▶ Validação 3: Verificar dados obrigatórios do novo bairro
        if (novoBairro.getNome() == null || novoBairro.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Nome do bairro é obrigatório"
            );
        }

        if (novoBairro.getCidade() == null || novoBairro.getCidade().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Cidade do bairro é obrigatória"
            );
        }

        // ▶ Inicializar lista de profissionais se necessário
        if (novoBairro.getProfissionais() == null) {
            novoBairro.setProfissionais(new ArrayList<>());
        }

        // ▶ Adicionar: Sincroniza bidirecionalidade
        this.bairrosAtendidos.add(novoBairro);
        novoBairro.getProfissionais().add(this);
    }


    /** ▶ MÉTODO AUXILIAR 2: REMOVER BAIRRO
     * 
     * Remove a associação entre este profissional e um bairro específico.
     * O bairro NÃO É DELETADO DO DB, apenas a associação é removida. */
    public void removerBairro(Integer bairroId) {
        
        // ▶ Validação: ID não pode ser nulo
        if (bairroId == null) {
            throw new IllegalArgumentException(
                "ID do bairro não pode ser nulo"
            );
        }

        // ▶ Buscar bairro na lista
        Bairro bairroParaRemover = this.bairrosAtendidos.stream()
            .filter(b -> Objects.equals(b.getId(), bairroId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                String.format(
                    "Bairro ID %d não está associado ao profissional ID %d (%s)",
                    bairroId, this.id, this.nome
                )
            ));

        // ▶ Remover: Sincroniza bidirecionalidade
        this.bairrosAtendidos.remove(bairroParaRemover);
        bairroParaRemover.getProfissionais().remove(this);
    }


    // ========================================================================
    // MÉTODOS AUXILIARES: CONSULTAS E VERIFICAÇÕES
    // ========================================================================

    /* Verifica se este profissional atende um bairro específico. */
    public boolean atendeBairro(Integer bairroId) {
        if (bairroId == null) {
            return false;
        }
        return this.bairrosAtendidos.stream()
            .anyMatch(b -> Objects.equals(b.getId(), bairroId));
    }

    /** Retorna a quantidade de bairros que este profissional atende. */
    public int countBairrosAtendidos() {
        return this.bairrosAtendidos != null ? this.bairrosAtendidos.size() : 0;
    }

    /** Remove TODOS os bairros associados a este profissional.
     * ⚠️ OPERAÇÃO DESTRUTIVA: Remove todas as associações */
    public void limparBairros() {
        // ▶ Remove bidirecionalidade: Remove este profissional da lista de cada bairro
        this.bairrosAtendidos.forEach(bairro -> 
            bairro.getProfissionais().remove(this)
        );
        // ▶ Limpa a lista local
        this.bairrosAtendidos.clear();
    }

    /** Método legado para manter compatibilidade com código existente.
     ⚠️ DESCONTINUADO: Prefira usar adicionarBairro() */
    @Deprecated(since = "2.0", forRemoval = true)
    public void addBairro(Bairro bairro) {
        if (bairro == null) {
            return;
        }
        if (this.bairrosAtendidos == null) {
            this.bairrosAtendidos = new ArrayList<>();
        }
        if (bairro.getProfissionais() == null) {
            bairro.setProfissionais(new ArrayList<>());
        }
        this.bairrosAtendidos.add(bairro);
        bairro.getProfissionais().add(this);
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

    public List<Bairro> getBairrosAtendidos() {
        return bairrosAtendidos;
    }

    /** Define a lista de bairros atendidos. Se nulo, inicializa com lista vazia.*/
    public void setBairrosAtendidos(List<Bairro> bairrosAtendidos) {
        this.bairrosAtendidos = bairrosAtendidos != null ? bairrosAtendidos : new ArrayList<>();
    }
}