package br.com.univida_test.demo.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.univida_test.demo.exceptions.ObjectNotFoundException;
import br.com.univida_test.demo.models.Bairro;
import br.com.univida_test.demo.models.Profissional;
import br.com.univida_test.demo.repositories.BairroRepository;
import br.com.univida_test.demo.repositories.ProfissionalRepository;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private BairroRepository bairroRepository;

    // Listar todos os profissionais -> se a lista estiver vazia.
    public List<Profissional> findAll() {
        List<Profissional> listProf = profissionalRepository.findAll();
        if (listProf.isEmpty()) {
            throw new ObjectNotFoundException("Nenhum profissional cadastrado");
        }
        return listProf;
    }

    // Buscar profissinal por bairro id
    public List<Profissional> findByBairroId(Integer bairroId) {
        if (bairroId == null || bairroId <= 0) {
            throw new IllegalArgumentException("ID do bairro não pode ser nulo (DEVE SER MAIOR QUE zero)");
        }

        bairroRepository.findById(bairroId)
                .orElseThrow(() -> new ObjectNotFoundException("Bairro não encontrado com id " + bairroId));

        List<Profissional> listProf = profissionalRepository.findByBairrosAtendidosId(bairroId);
        if (listProf.isEmpty()) {
            throw new ObjectNotFoundException("Nenhum profissional encontrado para o bairro com id " + bairroId);
        }
        return listProf;
    }

    // Buscar profissional por ID -> se o ID não existir, ele retorna um
    // ObjectNotFoundException.
    public Profissional findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do profissional não pode ser nulo (DEVE SER MAIOR QUE zero)");
        }
        return profissionalRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Profissional não encontrado com id " + id));
    }

    /*
     * public Profissional findById(Integer id) {
     * if (id == null || id <= 0) {
     * throw new
     * IllegalArgumentException("ID do profissional não pode ser nulo (DEVE SER MAIOR QUE zero)"
     * );
     * }
     * Optional<Profissional> profissional = profissionalRepository.findById(id);
     * if (!profissional.isPresent()) {
     * throw new ObjectNotFoundException(HttpStatus.NOT_FOUND,
     * "Profissional não encontrado com id " + id);
     * }
     * return profissional.get();
     * }
     */

    public Profissional save(Profissional profissional) {
        buscarPorNome(profissional);
        return profissionalRepository.save(profissional);
    }

    public Profissional update(Profissional profissional) {
        findById(profissional.getId());
        buscarPorNome(profissional);
        return profissionalRepository.save(profissional);
    }

    // Add vínculo entre Profissional e Bairro -> se o vínculo já existir, ñ
    // adiciona novamente, apenas retorna o prof atualizado.
    @Transactional
    public Profissional adicionarBairroAoProfissional(Integer profissionalId, Integer bairroId) {
        if (profissionalId == null || profissionalId <= 0) {
            throw new IllegalArgumentException("ID do profissional não pode ser nulo (DEVE SER MAIOR QUE zero)");
        }
        if (bairroId == null || bairroId <= 0) {
            throw new IllegalArgumentException("ID do bairro não pode ser nulo (DEVE SER MAIOR QUE zero)");
        }

        Profissional profissional = findById(profissionalId);
        Bairro bairro = bairroRepository.findById(bairroId)
                .orElseThrow(() -> new ObjectNotFoundException("Bairro não encontrado com id " + bairroId));

        boolean jaAssociado = profissional.getBairrosAtendidos().stream()
                .anyMatch(b -> Objects.equals(b.getId(), bairroId));

        if (!jaAssociado) {
            profissional.addBairro(bairro);
        }
        return profissionalRepository.save(profissional);
    }

    // Remove vínculo entre Profissional e Nairro -> se o vínculo ñ existir, retorna
    // ObjectNotFoundException.
    @Transactional
    public void removerBairroDoProfissional(Integer profissionalId, Integer bairroId) {
        if (profissionalId == null || profissionalId <= 0) {
            throw new IllegalArgumentException("ID do profissional não pode ser nulo (DEVE SER MAIOR QUE zero)");
        }
        if (bairroId == null || bairroId <= 0) {
            throw new IllegalArgumentException("ID do bairro não pode ser nulo (DEVE SER MAIOR QUE zero)");
        }

        Profissional profissional = findById(profissionalId);
        Bairro bairro = bairroRepository.findById(bairroId)
                .orElseThrow(() -> new ObjectNotFoundException("Bairro não encontrado com id " + bairroId));

        boolean removidoDoProfissional = profissional.getBairrosAtendidos()
                .removeIf(b -> Objects.equals(b.getId(), bairroId));
        boolean removidoDoBairro = bairro.getProfissionais()
                .removeIf(p -> Objects.equals(p.getId(), profissionalId));

        if (!removidoDoProfissional && !removidoDoBairro) {
            throw new ObjectNotFoundException("Associação não encontrada entre profissional " + profissionalId
                    + " e bairro " + bairroId);
        }

        profissionalRepository.save(profissional);
    }

    // Deletar profissional por ID -> se o ID não existir, ele retorna um
    // ObjectNotFoundException.
    // ele não verifica se o profissional tem associações, apenas deleta.
    public void delete(Integer id) {
        Profissional profDelete = findById(id);
        if (profDelete == null) {
            throw new ObjectNotFoundException("Profissional não encontrado com id " + id);
        }
        profissionalRepository.deleteById(id);
    }

    public void buscarPorNome(Profissional profissional) {
        Optional<Profissional> profPorNome = profissionalRepository.findByNomeIgnoreCase(profissional.getNome());

        if (profPorNome.isPresent()) {
            if (!Objects.equals(profPorNome.get().getId(), profissional.getId())) {

                throw new IllegalArgumentException("Profissional já existente com o nome: " + profissional.getNome());

            }

        }

    }
}
