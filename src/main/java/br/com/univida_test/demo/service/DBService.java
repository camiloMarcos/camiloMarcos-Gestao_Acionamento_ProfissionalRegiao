package br.com.univida_test.demo.service;

import br.com.univida_test.demo.models.Bairro;
import br.com.univida_test.demo.models.Profissional;
import br.com.univida_test.demo.repositories.BairroRepository;
import br.com.univida_test.demo.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
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

        Profissional prof1 = new Profissional(null, "Marisa Monte ", "Geral", "777777777","7777777",
                "marisamonte@gmail.com", "rua do céu, 107.","Recife",Arrays.asList(bairro2,bairro3));
        Profissional prof2 = new Profissional(null, "Maria Bthânia", "PED", "88888888887","888888",
                "mariabethania@hotmail.com", "rua do paraiso, 108.","Olinda", null);

      //  bairro2.getProfissionais().addAll(Arrays.asList(prof1));

       // prof2.getBairrosAtendidos().addAll(Arrays.asList(bairro1, bairro3));


        bairroRepository.saveAll(Arrays.asList(bairro1, bairro2, bairro3));
        profissionalRepository.saveAll(Arrays.asList(prof1));


    }

}
