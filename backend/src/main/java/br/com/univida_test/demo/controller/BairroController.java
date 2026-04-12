package br.com.univida_test.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.univida_test.demo.dtos.BairroDto;
import br.com.univida_test.demo.models.Bairro;
import br.com.univida_test.demo.service.BairroService;

@RestController
@RequestMapping("/bairro")
public class BairroController {

    
    @Autowired
    private BairroService bairroService;
    @Autowired
    private ModelMapper modelMapper;


    // Buscar todos os Bairros
    @GetMapping
    public ResponseEntity<List<BairroDto>> findAll() {
        List<Bairro> list = bairroService.findAll();
        List<BairroDto> listDto = new ArrayList<>();
        for (Bairro obj : list) {
            listDto.add(modelMapper.map(obj, BairroDto.class));
        }
        return ResponseEntity.ok().body(listDto);
    }

    // Buscar Bairro por ID
    @GetMapping("/{id}")
    public ResponseEntity<BairroDto> findById(@PathVariable Integer id) {
        Bairro bairro = bairroService.findById(id);
        return ResponseEntity.ok().body(modelMapper.map(bairro, BairroDto.class));
    }


    // Criar um novo Bairro
    @PostMapping
    public ResponseEntity<BairroDto> save(@RequestBody BairroDto bairroDto) {
        Bairro bairro = modelMapper.map(bairroDto, Bairro.class);
        Bairro sBairro = bairroService.save(bairro);
        return ResponseEntity.ok().body(modelMapper.map(sBairro, BairroDto.class));
    }

    // Atualizar um Bairro existente
    @PutMapping("/{id}")
    public ResponseEntity<BairroDto> update (@PathVariable Integer id, @RequestBody BairroDto bairroDto) {
         bairroDto.setId(id);
         Bairro bairroUp = bairroService.update(modelMapper.map(bairroDto, Bairro.class));
         return ResponseEntity.ok().body(modelMapper.map(bairroUp, BairroDto.class));
        }

    // Deletar um Bairro por ID
    @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete (@PathVariable Integer id){
            bairroService.delete(id);
            return ResponseEntity.noContent().build();
        }

    }





