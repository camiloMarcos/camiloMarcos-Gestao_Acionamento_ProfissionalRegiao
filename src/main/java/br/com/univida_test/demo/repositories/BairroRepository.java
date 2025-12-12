package br.com.univida_test.demo.repositories;

import br.com.univida_test.demo.models.Bairro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BairroRepository extends JpaRepository<Bairro, Integer> {

    public Optional<Bairro> findByNomeIgnoreCaseContaining(String nome);
}
