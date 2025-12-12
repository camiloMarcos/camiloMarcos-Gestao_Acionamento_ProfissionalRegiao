package br.com.univida_test.demo.service;

import br.com.univida_test.demo.models.Profissional;
import br.com.univida_test.demo.repositories.ProfissionalRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    // Listar todos os profissionais -> se a lista estiver vazia.
    public List<Profissional> findAll() {
        List<Profissional> list = profissionalRepository.findAll();
        if (list.isEmpty()) {
            throw new ObjectNotFoundException(HttpStatus.NOT_FOUND, "Nenhum profissional encontrado");
        }
        return list;
    }

    // Buscar profissional por ID -> se o ID não existir, ele retorna um ObjectNotFoundException.
    public Profissional findById(Integer id) {
        Optional<Profissional> profissional = profissionalRepository.findById(id);
                return profissional.orElseThrow(() -> new ObjectNotFoundException(HttpStatus.NOT_FOUND, "Profissional não encontrado com id " + id));
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

    //   Deletar profissional por ID -> se o ID não existir, ele retorna um ObjectNotFoundException.
    // ele não verifica se o profissional tem associações, apenas deleta.
    public void delete(Integer id) {
       Profissional profDelete = findById(id);
       if (profDelete == null) {
           throw new ObjectNotFoundException(HttpStatus.NOT_FOUND, "Profissional não encontrado com id " + id);
       }
            profissionalRepository.deleteById(id);
    }


    public void buscarPorNome(Profissional profissional) {
        Optional<Profissional> prof = profissionalRepository.findByNomeIgnoreCaseContaining(profissional.getNome());
        if (prof.isPresent() && !prof.get().getId().equals(profissional.getId())) {
            throw new IllegalArgumentException("Profissional já existente com o nome: " + profissional.getNome());
        }
    }

    //if (cat.isPresent()) {
    //  if (!Objects.equals(cat.get().getId(), categoria.getId())) {
    //      throw new IllegalArgumentException("Categoria já existente com o nome: " + categoria.getNome());
    //  }


}
