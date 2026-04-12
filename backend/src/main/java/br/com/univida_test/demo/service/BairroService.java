package br.com.univida_test.demo.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.univida_test.demo.exceptions.ObjectNotFoundException;
import br.com.univida_test.demo.models.Bairro;
import br.com.univida_test.demo.repositories.BairroRepository;



@Service
public class BairroService {

    @Autowired
    private BairroRepository bairroRepository;


    public List<Bairro> findAll() {                               // buscar todos os bairros
        List<Bairro> listBairros = bairroRepository.findAll();
        if (listBairros.isEmpty()) {
            throw new ObjectNotFoundException ("Nenhum bairro cadastrado");
        }
        return listBairros;
    }

    // Buscar bairro por ID.
    public Bairro findById(Integer id) {
       if (id == null || id<=0) {
            throw new IllegalArgumentException ("ID do bairro não pode ser nulo (DEVE SER MAIOR QUE zero");
        }
        return bairroRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException ("Bairro não encontrado com id " + id));
    }

    // Criar/Salvar bairro ->  valida se nome ja existe.
    public Bairro save(Bairro bairro) {
        buscarPorNome (bairro);
        return bairroRepository.save(bairro);
    }


    //PAREI AQUI VALIDAÇÃO E TRATAMENTO DE EXCEÇÃO NO UPDATE
    public Bairro update (Bairro bairro) {
        findById(bairro.getId());
        buscarPorNome(bairro);
        return bairroRepository.save(bairro);
    }

    // Deletar bairro por ID -> valida se o bairro tem profissionais associados, se tiver, ele lança uma DataIntegrityViolationException.
    public void delete(Integer id) {
        Bairro bairroDel = findById(id);
        if (!bairroDel.getProfissionais().isEmpty() && bairroDel.getProfissionais()!= null) {
            throw new DataIntegrityViolationException ("Não é possível deletar o bairro com id " + id + " pois ele está associado a profissionais.");
        }
        bairroRepository.delete(bairroDel);
    }

    // Buscar bairro por nome -> valida se o nome do bairro já existe.
    public void buscarPorNome(Bairro bairro) {
        Optional<Bairro> bairroPorNome = bairroRepository.findByNomeIgnoreCase(bairro.getNome());
        if (bairroPorNome.isPresent()) { 
                if (!Objects.equals(bairroPorNome.get().getId(), bairro.getId())){ 
                    throw new IllegalArgumentException("Bairro já existente com o nome: " + bairro.getNome());
                }
        }

    }

}













//  public void buscarPorNome(Bairro bairro) {
//      bairroRepository.findByNomeIgnoreCase(bairro.getNome()).ifPresent(b -> {
//            if (!Objects.equals(b.getId(), bairro.getId())) {
//                throw new IllegalArgumentException("Bairro já existente com o nome: " + bairro.getNome());
//            }
//        });
//}




//    public void buscarPorNome(Bairro bairro) {
//        Optional<Bairro> bairroPorNome = bairroRepository.findByNomeIgnoreCaseContaining(bairro.getNome());
//        if (bairroPorNome.isPresent()){ 
//                if (! bairroPorNome.get().getId().equals(bairro.getId())){
//
//                    throw new IllegalArgumentException("Bairro já existente com o nome: " + bairro.getNome());

//                }
 
                
//            }
//        }