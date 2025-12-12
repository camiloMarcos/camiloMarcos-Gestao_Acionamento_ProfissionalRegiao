package br.com.univida_test.demo.service;

import br.com.univida_test.demo.models.Profissional;
import br.com.univida_test.demo.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;


    public List<Profissional> findAll() {
        return profissionalRepository.findAll();
    }


    public Profissional findById(Integer id) {
        return profissionalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado com id " + id));
    }


    public Profissional save(Profissional profissional) {
        buscarPorNome(profissional);
        return profissionalRepository.save(profissional);
    }


    public Profissional update(Profissional profissional) {
        findById(profissional.getId());
        buscarPorNome(profissional);
        return profissionalRepository.save(profissional);
    }


    public void delete(Integer id) {
        if (!profissionalRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado com id " + id);
        }
        profissionalRepository.deleteById(id);
    }


    public void buscarPorNome(Profissional profissional) {
        Optional<Profissional> prof = profissionalRepository.findByNomeIgnoreCaseContaining(profissional.getNome());
        if (prof.isPresent() && !prof.get().getId().equals(profissional.getId())) {
            throw new IllegalArgumentException("Profissional já existente com o nome: " + profissional.getNome());
        }
    }


}
