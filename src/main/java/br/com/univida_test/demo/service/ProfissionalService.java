package br.com.univida_test.demo.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.univida_test.demo.models.Profissional;
import br.com.univida_test.demo.repositories.ProfissionalRepository;
import br.com.univida_test.demo.exceptions.ObjectNotFoundException;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    // Listar todos os profissionais -> se a lista estiver vazia.
    public List<Profissional> findAll() {
        List<Profissional> listProf = profissionalRepository.findAll();
        if (listProf.isEmpty()) {
            throw new ObjectNotFoundException("Nenhum profissional cadastrado");
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
