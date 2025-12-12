package br.com.univida_test.demo.service;

import br.com.univida_test.demo.models.Bairro;
import br.com.univida_test.demo.repositories.BairroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class BairroService {

    @Autowired
    private BairroRepository bairroRepository;


    public List<Bairro> findAll() {                               // buscar todos os bairros
        List<Bairro> listBairros = bairroRepository.findAll();
        return listBairros;
    }

    public Bairro findById(Integer id) {
        return bairroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bairro não encontrado com id " + id));
    }

    public Bairro save(Bairro bairro) {
        buscarPorNome(bairro);
        return bairroRepository.save(bairro);
    }

    //PAREI AQUI VALIDAÇÃO E TRATAMENTO DE EXCEÇÃO NO UPDATE
    public Bairro update(Bairro bairro) {
        findById(bairro.getId());
        return bairroRepository.save(bairro);
    }

    public void delete(Integer id) {
        if (!bairroRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bairro não encontrado com id " + id);
        }
        bairroRepository.deleteById(id);
    }

    public void buscarPorNome(Bairro bairro) {
        Optional<Bairro> bair = bairroRepository.findByNomeIgnoreCaseContaining(bairro.getNome());
        if (bair.isPresent()) {
            if (bair.get().getId() != bairro.getId()) {
                throw new IllegalArgumentException("Bairro já existente com o nome: " + bairro.getNome());
            }
        }
    }




}
