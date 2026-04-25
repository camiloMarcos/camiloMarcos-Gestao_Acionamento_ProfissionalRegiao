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


    // Buscar todos os bairros.
    public List<Bairro> findAll() {
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


    // Buscar bairro por nome exato (case-insensitive)
    public Bairro findByNomeExato(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de busca não pode ser vazio");
        }
        return bairroRepository.findByNomeIgnoreCase(nome.trim())
                .orElseThrow(() -> new ObjectNotFoundException("Bairro não encontrado com nome: " + nome));
    }


    // Buscar bairros por nome contendo (busca parcial, case-insensitive)
    public List<Bairro> findByNomeContendo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de busca não pode ser vazio");
        }
        List<Bairro> listBairros = bairroRepository.findByNomeContainingIgnoreCase(nome.trim());
        if (listBairros.isEmpty()) {
            throw new ObjectNotFoundException("Nenhum bairro encontrado contendo o nome: " + nome);
        }
        return listBairros;
    }


// Validar se o nome do bairro já existe (para save e update)
    private void validarNomeDuplicado(Bairro bairro) {
        Optional<Bairro> bairroPorNome = bairroRepository.findByNomeIgnoreCase(bairro.getNome());
        if (bairroPorNome.isPresent()) { 
            if (!Objects.equals(bairroPorNome.get().getId(), bairro.getId())){ 
                throw new IllegalArgumentException("Bairro já existente com o nome: " + bairro.getNome());
            }
        }
    }


    // Criar/Salvar bairro -> valida se nome ja existe.
    public Bairro save(Bairro bairro) {
        validarNomeDuplicado(bairro);
        return bairroRepository.save(bairro);
    }

    
    // Atualizar bairro existente -> valida se nome ja existe (exceto ele mesmo)
    public Bairro update(Bairro bairro) {
        findById(bairro.getId());
        validarNomeDuplicado(bairro);
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
  
       
    // Buscar bairros por cidade
    public List<Bairro> findByCidade(String cidade) {
        if (cidade == null || cidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade não pode ser vazia");
        }
        List<Bairro> listBairros = bairroRepository.findByCidade(cidade.trim());
        if (listBairros.isEmpty()) {
            throw new ObjectNotFoundException("Nenhum bairro encontrado para a cidade: " + cidade);
        }
        return listBairros;
    }


    // Buscar bairros por risco/perigo
    public List<Bairro> findByPerigoso(boolean perigo) {
        List<Bairro> listBairros = bairroRepository.findByPerigo_Distante(perigo);
        String tipoFiltro = perigo ? "perigosos/distantes" : "seguros";
        if (listBairros.isEmpty()) {
            throw new ObjectNotFoundException("Nenhum bairro encontrado nos bairros " + tipoFiltro);
        }
        return listBairros;
    }


    // MANTÉM COMPATIBILIDADE: renomear para validarNomeDuplicado
    // Método legado - mantido para compatibilidade (podera ser removido)
    public void buscarPorNome(Bairro bairro) {
        validarNomeDuplicado(bairro);
    }

}