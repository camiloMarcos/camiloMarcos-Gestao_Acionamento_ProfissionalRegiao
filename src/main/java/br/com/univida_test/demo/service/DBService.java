package br.com.univida_test.demo.service;

import br.com.univida_test.demo.models.Bairro;
import br.com.univida_test.demo.models.Profissional;
import br.com.univida_test.demo.repositories.BairroRepository;
import br.com.univida_test.demo.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


@Service
public class DBService {

    @Autowired
    private BairroRepository bairroRepository;
    @Autowired
    private ProfissionalRepository profissionalRepository;


    public void instanciaDB() {

        // Cria profissionais já com a lista de bairros inicializada
        Profissional prof1 = new Profissional(null, "Marisa Monte", "Geral", "777777777", "7777777",
                "marisamonte@gmail.com", "Rua do Céu, 177", "Recife", new ArrayList<>());

        Profissional prof2 = new Profissional(null, "Maria Bthânia", "PED", "88888888888", "888888",
                "mariabethania@hotmail.com", "Rua do Paraíso, 188", "Olinda", new ArrayList<>());

        // S@lva profission@is primeiro
        profissionalRepository.saveAll(Arrays.asList(prof1, prof2));

        // Cria b@irros
        Bairro bairro1 = new Bairro(null, "Rio Doce", "Olinda", true);
        Bairro bairro2 = new Bairro(null, "Centro", "Recife", true);
        Bairro bairro3 = new Bairro(null, "Boa Viagem", "Recife", false);

        // Associações bidirecionais usando métodos auxiliares
        bairro1.addProfissional(prof2); // bairro1 <-> prof2
        bairro2.addProfissional(prof1); // bairro2 <-> prof1
        bairro3.addProfissional(prof1); // bairro3 <-> prof1

        // Salva bairros com as associações
        bairroRepository.saveAll(Arrays.asList(bairro1, bairro2, bairro3));
    }
}



 //       Bairro bairro1 = new Bairro (null, "Rio Doce","Olinda", true);
//        Bairro bairro2 = new Bairro (null, "Centro","Recife", true);
//        Bairro bairro3 = new Bairro (null, "Boa Viagem","Recife", false);





  //      Profissional prof1 = new Profissional(null, "Marisa Monte ", "Geral", "777777777","7777777",
  //              "marisamonte@gmail.com", "rua do céu, 177.","Recife", new ArrayList<>());

    //    Profissional prof2 = new Profissional(null, "Maria Bthânia", "PED", "88888888888","888888",
    //            "mariabethania@hotmail.com", "rua do paraiso, 188.","Olinda", new ArrayList<>());


    //    bairro1.addProfissional(prof2);
      //  bairro2.addProfissional(prof1);
      //  bairro3.addProfissional(prof1);


       // profissionalRepository.saveAll(Arrays.asList(prof1, prof2));
       // bairroRepository.saveAll(Arrays.asList(bairro1, bairro2, bairro3));




