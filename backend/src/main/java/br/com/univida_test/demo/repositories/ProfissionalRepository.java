package br.com.univida_test.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.univida_test.demo.models.Profissional;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Integer> {

    //buscar profissional por nome IgnoreCase
    Optional<Profissional> findByNomeIgnoreCase(String nome);

    //buscar profissional por Bairro /id
    java.util.List<Profissional> findByBairrosAtendidosId(Integer bairroId);
}
