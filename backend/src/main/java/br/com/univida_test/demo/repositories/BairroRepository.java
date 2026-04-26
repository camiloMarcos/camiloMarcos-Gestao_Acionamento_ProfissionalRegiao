package br.com.univida_test.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.univida_test.demo.models.Bairro;

@Repository
public interface BairroRepository extends JpaRepository<Bairro, Integer>, JpaSpecificationExecutor<Bairro> {

    // Buscar bairro por nome exato (case-insensitive)
    Optional<Bairro> findByNomeIgnoreCase(String nome);

    // Buscar bairros por nome contendo (busca parcial, case-insensitive)
    List<Bairro> findByNomeContainingIgnoreCase(String nome);

    // Buscar bairros por cidade
    List<Bairro> findByCidade(String cidade);

    // Buscar bairros por risco/perigo (usando @Query para contornar o underscore)
    @Query("SELECT b FROM Bairro b WHERE b.perigo_Distante = :perigo")
    List<Bairro> findByPerigo_Distante(boolean perigo);
}
