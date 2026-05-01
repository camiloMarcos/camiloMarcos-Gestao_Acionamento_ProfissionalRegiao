package br.com.univida_test.demo.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.univida_test.demo.exceptions.ObjectNotFoundException;
import br.com.univida_test.demo.models.Bairro;
import br.com.univida_test.demo.models.Profissional;
import br.com.univida_test.demo.repositories.BairroRepository;
import br.com.univida_test.demo.repositories.ProfissionalRepository;

/**
 * ============================================================================
 * SERVIÇO ESPECIALIZADO: GERENCIAMENTO DE RELACIONAMENTOS
 * ============================================================================
 * Gerenciar exclusivamente as operações: associação e dissociação entre as entidades Profissional e Bairro.
 * 
 * Responsabilidades:
 * - Associar profissional existente a um bairro existente
 * - Criar novo profissional e associar a um ou múltiplos bairros
 * - Desassociar profissional de um bairro
 * - Validar regras de negócio do relacionamento
 * - Garantir integridade bidirecional da associação */
@Service
public class ProfissionalBairroService {

    
    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private BairroRepository bairroRepository;


    // ========================================================================
    // OPERAÇÃO 1: ASSOCIAR PROFISSIONAL EXISTENTE A BAIRRO EXISTENTE
    // ========================================================================
       @Transactional
    public void associarProfissionalExistenteABairro(Integer profissionalId, Integer bairroId) {
        
        // ▶ Validação de entrada
        validarIds(profissionalId, bairroId);

        // ▶ Buscar entidades no banco de dados
        Profissional profissional = buscarProfissionalOuThrow(profissionalId);
        Bairro bairro = buscarBairroOuThrow(bairroId);

        // ▶ Validar se já não está associado
        validarAssociacaoJaExistente(profissional, bairro);

        // ▶ Adicionar associação (método da entidade mantém bidirecionalidade)
        bairro.adicionarProfissionalExistente(profissional);

        // ▶ Persistir mudanças
        bairroRepository.save(bairro);
    }


    // ========================================================================
    // OPERAÇÃO 2: CRIAR NOVO PROFISSIONAL E ASSOCIAR A BAIRRO(S)
    // ========================================================================
    /*Cria um novo profissional com seus dados básicos e o associa a um ou múltiplos bairros em uma única transação. */
    @Transactional
    public Profissional criarNovoProfissionalComBairros(
            String nome,
            String especialidade,
            String numeroConselho,
            String telefone,
            String email,
            String endereco,
            String cidade,
            List<Integer> bairroIds) {

        // ▶ Validar dados do novo profissional
        validarDadosNovoProfissional(nome, especialidade, numeroConselho, telefone, email, endereco, cidade);

        // ▶ Validar lista de bairros
        if (bairroIds == null || bairroIds.isEmpty()) {
            throw new IllegalArgumentException(
                "Deve-se associar ao menos um bairro ao criar novo profissional"
            );
        }

        // ▶ Buscar todos os bairros
        List<Bairro> bairros = bairroRepository.findAllById(bairroIds);

        // ▶ Verificar se todos bairros foram encontrados
        if (bairros.size() != bairroIds.size()) {
            throw new ObjectNotFoundException(
                "Alguns bairros não foram encontrados. Esperava " + bairroIds.size() + 
                " bairros, mas encontrou " + bairros.size()
            );
        }

        // ▶ Criar nova instância de profissional
        Profissional novoProfissional = new Profissional();
        novoProfissional.setNome(nome);
        novoProfissional.setEspecialidade(especialidade);
        novoProfissional.setNumeroConselho(numeroConselho);
        novoProfissional.setTelefone(telefone);
        novoProfissional.setEmail(email);
        novoProfissional.setEndereco(endereco);
        novoProfissional.setCidade(cidade);

        // ▶ Associar cada bairro ao novo profissional
        for (Bairro bairro : bairros) {
            bairro.adicionarNovoProfissional(novoProfissional);
        }

        // ▶ Persistir profissional (cascata persiste associações)
        return profissionalRepository.save(novoProfissional);
    }


    // ========================================================================
    // OPERAÇÃO 2B: CRIAR NOVO BAIRRO E ASSOCIAR A PROFISSIONAL(IS)
    // ========================================================================
    /**
     * Cria um novo bairro e o associa a um ou múltiplos profissionais existentes.
     * Tudo em uma única transação.
     * 
     * @param nomeBairro Nome do novo bairro
     * @param cidadeBairro Cidade do novo bairro
     * @param perigoDistante Se o bairro é distante/perigoso
     * @param profissionalIds IDs dos profissionais a associar (deve ter ao menos um)
     * @return O novo Bairro criado e associado
     * @throws IllegalArgumentException se dados inválidos ou lista vazia
     * @throws ObjectNotFoundException se algum profissional não existir
     */
    @Transactional
    public Bairro criarNovoBairroComProfissionais(
            String nomeBairro,
            String cidadeBairro,
            boolean perigoDistante,
            List<Integer> profissionalIds) {

        // ▶ Validar dados do novo bairro
        if (nomeBairro == null || nomeBairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do bairro é obrigatório");
        }

        if (cidadeBairro == null || cidadeBairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade do bairro é obrigatória");
        }

        // ▶ Validar lista de profissionais
        if (profissionalIds == null || profissionalIds.isEmpty()) {
            throw new IllegalArgumentException(
                "Deve-se associar ao menos um profissional ao criar novo bairro"
            );
        }

        // ▶ Buscar todos os profissionais
        List<Profissional> profissionais = profissionalRepository.findAllById(profissionalIds);

        // ▶ Verificar se todos profissionais foram encontrados
        if (profissionais.size() != profissionalIds.size()) {
            throw new ObjectNotFoundException(
                "Alguns profissionais não foram encontrados. Esperava " + profissionalIds.size() + 
                " profissionais, mas encontrou " + profissionais.size()
            );
        }

        // ▶ Criar nova instância de bairro
        Bairro novoBairro = new Bairro();
        novoBairro.setNome(nomeBairro);
        novoBairro.setCidade(cidadeBairro);
        novoBairro.setPerigo_Distante(perigoDistante);

        // ▶ Associar cada profissional ao novo bairro
        for (Profissional prof : profissionais) {
            novoBairro.adicionarProfissionalExistente(prof);
        }

        // ▶ Persistir bairro (cascata persiste associações)
        return bairroRepository.save(novoBairro);
    }

    // ========================================================================
    // OPERAÇÃO 2C: CRIAR PROFISSIONAL COM NOVO BAIRRO (SEM BAIRRO PRÉ-EXISTENTE)
    // ========================================================================
    /**
     * Cria um novo profissional COM UM NOVO BAIRRO.
     * Útil quando você quer criar ambos simultaneously.
     * 
     * @param nomeProfissional Nome do novo profissional
     * @param especialidade Especialidade
     * @param numeroConselho Número do conselho
     * @param telefone Telefone
     * @param email Email
     * @param endereco Endereço
     * @param cidadeProfissional Cidade do profissional
     * @param nomeBairro Nome do novo bairro
     * @param cidadeBairro Cidade do novo bairro
     * @param perigoDistante Se o bairro é distante
     * @return O novo Profissional criado e associado
     * @throws IllegalArgumentException se dados inválidos
     */
    @Transactional
    public Profissional criarNovoProfissionalComNovoBairro(
            String nomeProfissional,
            String especialidade,
            String numeroConselho,
            String telefone,
            String email,
            String endereco,
            String cidadeProfissional,
            String nomeBairro,
            String cidadeBairro,
            boolean perigoDistante) {

        // ▶ Validar dados do novo profissional
        validarDadosNovoProfissional(nomeProfissional, especialidade, numeroConselho, 
                                     telefone, email, endereco, cidadeProfissional);

        // ▶ Validar dados do novo bairro
        if (nomeBairro == null || nomeBairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do bairro é obrigatório");
        }

        if (cidadeBairro == null || cidadeBairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade do bairro é obrigatória");
        }

        // ▶ Criar nova instância de profissional
        Profissional novoProfissional = new Profissional();
        novoProfissional.setNome(nomeProfissional);
        novoProfissional.setEspecialidade(especialidade);
        novoProfissional.setNumeroConselho(numeroConselho);
        novoProfissional.setTelefone(telefone);
        novoProfissional.setEmail(email);
        novoProfissional.setEndereco(endereco);
        novoProfissional.setCidade(cidadeProfissional);

        // ▶ Criar nova instância de bairro
        Bairro novoBairro = new Bairro();
        novoBairro.setNome(nomeBairro);
        novoBairro.setCidade(cidadeBairro);
        novoBairro.setPerigo_Distante(perigoDistante);

        // ▶ Associar com sincronização bidirecional automática
        novoProfissional.adicionarNovoBairro(novoBairro);

        // ▶ Persistir profissional (cascata persiste bairro e associações)
        return profissionalRepository.save(novoProfissional);
    }

    // ========================================================================
    // OPERAÇÃO 3: DESASSOCIAR PROFISSIONAL DE BAIRRO -> Remove a associação entre um profissional e um bairro.
    // ========================================================================
    @Transactional
    public void desassociarProfissionalDeBairro(Integer profissionalId, Integer bairroId) {
        
        // ▶ Validação de entrada
        validarIds(profissionalId, bairroId);

        // ▶ Buscar entidades no banco de dados
        Profissional profissional = buscarProfissionalOuThrow(profissionalId);
        Bairro bairro = buscarBairroOuThrow(bairroId);

        // ▶ Remover associação (método da entidade mantém bidirecionalidade)
        bairro.removerProfissional(profissionalId);

        // ▶ Persistir mudanças
        bairroRepository.save(bairro);
    }

    // ========================================================================
    // OPERAÇÃO 4: ASSOCIAR PROFISSIONAL EXISTENTE COM NOVO BAIRRO
    // ========================================================================
    /**
     * Associa um profissional existente com um novo bairro (que será criado).
     * 
     * @param profissionalId ID do profissional existente
     * @param nomeBairro Nome do novo bairro
     * @param cidadeBairro Cidade do novo bairro
     * @param perigoDistante Se é distante/perigoso
     * @return O novo Bairro criado e associado
     * @throws ObjectNotFoundException se profissional não existir
     */
    @Transactional
    public Bairro associarProfissionalExistenteComNovoBairro(
            Integer profissionalId,
            String nomeBairro,
            String cidadeBairro,
            boolean perigoDistante) {

        // ▶ Buscar profissional
        Profissional profissional = buscarProfissionalOuThrow(profissionalId);

        // ▶ Validar dados do novo bairro
        if (nomeBairro == null || nomeBairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do bairro é obrigatório");
        }

        if (cidadeBairro == null || cidadeBairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade do bairro é obrigatória");
        }

        // ▶ Criar novo bairro
        Bairro novoBairro = new Bairro();
        novoBairro.setNome(nomeBairro);
        novoBairro.setCidade(cidadeBairro);
        novoBairro.setPerigo_Distante(perigoDistante);

        // ▶ Associar profissional ao novo bairro
        profissional.adicionarNovoBairro(novoBairro);

        // ▶ Persistir profissional (cascata persiste bairro)
        profissionalRepository.save(profissional);

        return novoBairro;
    }

    // ========================================================================
    // OPERAÇÃO 5: ASSOCIAR BAIRRO EXISTENTE COM NOVO PROFISSIONAL
    // ========================================================================
    /**
     * Associa um bairro existente com um novo profissional (que será criado).
     * 
     * @param bairroId ID do bairro existente
     * @param nomeProfissional Nome do novo profissional
     * @param especialidade Especialidade
     * @param numeroConselho Número do conselho
     * @param telefone Telefone
     * @param email Email
     * @param endereco Endereço
     * @param cidadeProfissional Cidade do profissional
     * @return O novo Profissional criado e associado
     * @throws ObjectNotFoundException se bairro não existir
     */
    @Transactional
    public Profissional associarBairroExistenteComNovoProfissional(
            Integer bairroId,
            String nomeProfissional,
            String especialidade,
            String numeroConselho,
            String telefone,
            String email,
            String endereco,
            String cidadeProfissional) {

        // ▶ Buscar bairro
        Bairro bairro = buscarBairroOuThrow(bairroId);

        // ▶ Validar dados do novo profissional
        validarDadosNovoProfissional(nomeProfissional, especialidade, numeroConselho,
                                     telefone, email, endereco, cidadeProfissional);

        // ▶ Criar novo profissional
        Profissional novoProfissional = new Profissional();
        novoProfissional.setNome(nomeProfissional);
        novoProfissional.setEspecialidade(especialidade);
        novoProfissional.setNumeroConselho(numeroConselho);
        novoProfissional.setTelefone(telefone);
        novoProfissional.setEmail(email);
        novoProfissional.setEndereco(endereco);
        novoProfissional.setCidade(cidadeProfissional);

        // ▶ Associar novo profissional ao bairro
        bairro.adicionarNovoProfissional(novoProfissional);

        // ▶ Persistir bairro (cascata persiste profissional)
        bairroRepository.save(bairro);

        return novoProfissional;
    }


    // ========================================================================
    // VALIDAÇÕES DE ENTRADA E REGRAS DE NEGÓCIO -> Valida se os IDs fornecidos são válidos (não nulos e maiores que zero).
    // ========================================================================
    private void validarIds(Integer profissionalId, Integer bairroId) {
        if (profissionalId == null || profissionalId <= 0) {
            throw new IllegalArgumentException(
                "ID do profissional é obrigatório e deve ser maior que zero"
            );
        }

        if (bairroId == null || bairroId <= 0) {
            throw new IllegalArgumentException(
                "ID do bairro é obrigatório e deve ser maior que zero"
            );
        }
    }

    /* Valida dados básicos de um novo profissional antes de criação.*/
    private void validarDadosNovoProfissional(String nome, String especialidade,
            String numeroConselho, String telefone, String email, String endereco, String cidade) {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do profissional é obrigatório");
        }

        if (especialidade == null || especialidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Especialidade do profissional é obrigatória");
        }

        if (numeroConselho == null || numeroConselho.trim().isEmpty()) {
            throw new IllegalArgumentException("Número do conselho é obrigatório");
        }

        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone é obrigatório");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("E-mail é obrigatório");
        }

        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereço é obrigatório");
        }

        if (cidade == null || cidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade é obrigatória");
        }
    }

    /* Valida se a associação entre profissional e bairro já não existe. */
    private void validarAssociacaoJaExistente(Profissional profissional, Bairro bairro) {
        boolean jaAssociado = profissional.getBairrosAtendidos().stream()
            .anyMatch(b -> Objects.equals(b.getId(), bairro.getId()));

        if (jaAssociado) {
            throw new IllegalStateException(
                String.format(
                    "Profissional %d (%s) já está associado ao bairro %d (%s)",
                    profissional.getId(), profissional.getNome(),
                    bairro.getId(), bairro.getNome()
                )
            );
        }
    }

    
    // ========================================================================
    // MÉTODOS AUXILIARES DE BUSCA -> Busca um profissional pelo ID ou lança exceção se não encontrar.
    // ========================================================================
    private Profissional buscarProfissionalOuThrow(Integer profissionalId) {
        return profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new ObjectNotFoundException(
                "Profissional não encontrado com ID: " + profissionalId
            ));
    }

    /* Busca um bairro pelo ID ou lança exceção se não encontrar.*/
    private Bairro buscarBairroOuThrow(Integer bairroId) {
        return bairroRepository.findById(bairroId)
            .orElseThrow(() -> new ObjectNotFoundException(
                "Bairro não encontrado com ID: " + bairroId
            ));
    }
}
