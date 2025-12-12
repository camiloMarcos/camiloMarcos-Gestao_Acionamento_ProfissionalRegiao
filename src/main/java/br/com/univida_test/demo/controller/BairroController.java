package br.com.univida_test.demo.controller;

import br.com.univida_test.demo.dtos.BairroDto;
import br.com.univida_test.demo.models.Bairro;
import br.com.univida_test.demo.service.BairroService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bairro")
public class BairroController {

    @Autowired
    private BairroService bairroService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<BairroDto>> findAll() {
        List<Bairro> list = bairroService.findAll();
        List<BairroDto> listDto = new ArrayList<>();
        for (Bairro obj : list) {
            listDto.add(modelMapper.map(obj, BairroDto.class));
        }
        return ResponseEntity.ok().body(listDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BairroDto> findById(@PathVariable Integer id) {
        Bairro bairro = bairroService.findById(id);
        return ResponseEntity.ok().body(modelMapper.map(bairro, BairroDto.class));
    }


    @PostMapping
    public ResponseEntity<BairroDto> save(@RequestBody BairroDto bairroDto) {
        Bairro bairro = modelMapper.map(bairroDto, Bairro.class);
        Bairro saveBairro = bairroService.save(bairro);
        return ResponseEntity.ok().body(modelMapper.map(bairro, BairroDto.class));
    }


    @PutMapping("/{id}")
    public ResponseEntity<BairroDto> update (@PathVariable Integer id, @RequestBody BairroDto bairroDto) {
         bairroDto.setId(id);
         Bairro bairroUp = bairroService.update(modelMapper.map(bairroDto, Bairro.class));
         return ResponseEntity.ok().body(modelMapper.map(bairroUp, BairroDto.class));
        }


    @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete (@PathVariable Integer id){
            bairroService.delete(id);
            return ResponseEntity.noContent().build();
        }

    }





