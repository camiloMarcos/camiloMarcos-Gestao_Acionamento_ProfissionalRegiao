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

import br.com.univida_test.demo.dtos.ProfissionalDTO;
import br.com.univida_test.demo.models.Profissional;
import br.com.univida_test.demo.service.ProfissionalService;

@RestController
@RequestMapping("/profissional")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<ProfissionalDTO>> findAll() {

        List<Profissional> list = profissionalService.findAll();
        List<ProfissionalDTO> listDto = new ArrayList<>();
        for (Profissional p : list) {
            listDto.add(modelMapper.map(p, ProfissionalDTO.class));
        }
        return ResponseEntity.ok().body(listDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalDTO> findById(@PathVariable Integer id) {
        Profissional profissional = profissionalService.findById(id);
        return ResponseEntity.ok().body(modelMapper.map(profissional, ProfissionalDTO.class));
    }

    @PostMapping
    public ResponseEntity<ProfissionalDTO> save(@RequestBody ProfissionalDTO profissionalDto) {
        Profissional profissional = modelMapper.map(profissionalDto, Profissional.class);
        Profissional profSave = profissionalService.save(profissional);
        return ResponseEntity.ok().body(modelMapper.map(profSave, ProfissionalDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalDTO> update(@PathVariable Integer id, @RequestBody ProfissionalDTO profissionalDto) {
        profissionalDto.setId(id);
        Profissional profissional = modelMapper.map(profissionalDto, Profissional.class);
        Profissional updated = profissionalService.update(profissional);
        return ResponseEntity.ok().body(modelMapper.map(updated, ProfissionalDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        profissionalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
