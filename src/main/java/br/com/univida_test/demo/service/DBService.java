package br.com.univida_test.demo.service;

import br.com.univida_test.demo.models.Bairro;
import br.com.univida_test.demo.repositories.BairroRepository;
import br.com.univida_test.demo.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DBService {

    @Autowired
    private BairroRepository bairroRepository;
    @Autowired
    private ProfissionalRepository profissionalRepository;


    public void instanciaDB() {

        Bairro bairro1 = new Bairro (null, "Rio Doce","Olinda", true);
        Bairro bairro2 = new Bairro (null, "Centro","Recife", true);
        Bairro bairro3 = new Bairro (null, "Boa Viagem","Recife", false);

        bairroRepository.saveAll(Arrays.asList(bairro1, bairro2, bairro3));

    }

}
