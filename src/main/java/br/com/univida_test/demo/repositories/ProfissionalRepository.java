package br.com.univida_test.demo.repositories;

import br.com.univida_test.demo.models.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Integer> {
    Optional<Profissional> findByNomeIgnoreCaseContaining(String nome);
}
