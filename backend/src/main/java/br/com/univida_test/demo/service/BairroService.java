package br.com.univida_test.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import br.com.univida_test.demo.exceptions.ObjectNotFoundException;
import br.com.univida_test.demo.models.Bairro;
import br.com.univida_test.demo.repositories.BairroRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

@Service
public class BairroService {

    @Autowired
    private BairroRepository bairroRepository;

    // Buscar bairros dinamicamente com filtros opcionais
    public List<Bairro> findByFiltrosDinamicos(Integer id, String nome, String cidade, Boolean perigoso, Integer profissionalId, String profissionalNome) {
        Specification<Bairro> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }
            if (nome != null && !nome.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
            }
            if (cidade != null && !cidade.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("cidade")), "%" + cidade.toLowerCase() + "%"));
            }
            if (perigoso != null) {
                predicates.add(criteriaBuilder.equal(root.get("perigo_Distante"), perigoso));
            }
            
            // Filtros relacionados ao Profissional
            if (profissionalId != null || (profissionalNome != null && !profissionalNome.trim().isEmpty())) {
                Join<Bairro, Object> joinProfissionais = root.join("profissionais");
                
                if (profissionalId != null) {
                    predicates.add(criteriaBuilder.equal(joinProfissionais.get("id"), profissionalId));
                }
                
                if (profissionalNome != null && !profissionalNome.trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(joinProfissionais.get("nome")), 
                        "%" + profissionalNome.toLowerCase() + "%"
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<Bairro> resultados = bairroRepository.findAll(spec);
        if (resultados.isEmpty()) {
            throw new ObjectNotFoundException("Nenhum bairro encontrado com os filtros informados.");
        }
        return resultados;
    }

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


    // Buscar bairros por risco/perigo
    public List<Bairro> findByPerigoso(boolean perigo) {
        List<Bairro> list = bairroRepository.findByPerigo_Distante(perigo);
        if (list.isEmpty()) {
            throw new ObjectNotFoundException("Nenhum bairro encontrado com o status perigo/distante: " + perigo);
        }
        return list;
    }

    // Deletar bairro por ID -> valida se o bairro tem profissionais associados, se tiver, ele lança uma DataIntegrityViolationException.
    public void delete(Integer id) {
        Bairro bairroDel = findById(id);
        if (bairroDel.getProfissionais() != null && !bairroDel.getProfissionais().isEmpty()) {
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

    // Método legado - mantido para compatibilidade (podera ser removido)
    public void buscarPorNome(Bairro bairro) {
        validarNomeDuplicado(bairro);
    }
}
