package br.com.univida_test.demo.controller;

import java.util.ArrayList;
import java.util.List;

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
import br.com.univida_test.demo.mapper.ProfissionalMapper;
import br.com.univida_test.demo.models.Profissional;
import br.com.univida_test.demo.service.ProfissionalService;

@RestController
@RequestMapping("/profissional")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;

    @Autowired
    private ProfissionalMapper profissionalMapper;

// Buscar todos os Profissionais    
    @GetMapping
    public ResponseEntity<List<ProfissionalDTO>> findAll() {

        List<Profissional> list = profissionalService.findAll();
        List<ProfissionalDTO> listDto = new ArrayList<>();
        for (Profissional p : list) {
            listDto.add(profissionalMapper.toDto(p));
        }
        return ResponseEntity.ok().body(listDto);
    }

    // Buscar Profissional por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalDTO> findById(@PathVariable Integer id) {
        Profissional profissional = profissionalService.findById(id);
        return ResponseEntity.ok().body(profissionalMapper.toDto(profissional));
    }

    // Buscar Profissional(is) por Bairro ID
    @GetMapping("/bairro/{bairroId}")
    public ResponseEntity<List<ProfissionalDTO>> findByBairroId(@PathVariable Integer bairroId) {
        List<Profissional> list = profissionalService.findByBairroId(bairroId);
        List<ProfissionalDTO> listDto = new ArrayList<>();
        for (Profissional p : list) {
            listDto.add(profissionalMapper.toDto(p));
        }
        return ResponseEntity.ok().body(listDto);
    }
// Criar um novo Profissional
    @PostMapping
    public ResponseEntity<ProfissionalDTO> save(@RequestBody ProfissionalDTO profissionalDto) {
        Profissional profissional = profissionalMapper.toEntity(profissionalDto);
        Profissional profSave = profissionalService.save(profissional);
        return ResponseEntity.ok().body(profissionalMapper.toDto(profSave));
    }
// Atualizar um Profissional existente
    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalDTO> update(@PathVariable Integer id, @RequestBody ProfissionalDTO profissionalDto) {
        profissionalDto.setId(id);
        Profissional profissional = profissionalMapper.toEntity(profissionalDto);
        Profissional updated = profissionalService.update(profissional);
        return ResponseEntity.ok().body(profissionalMapper.toDto(updated));
    }
// Deletar um Profissional por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        profissionalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Associar um Bairro ao Profissional
    @PostMapping("/{profissionalId}/bairro/{bairroId}")
    public ResponseEntity<ProfissionalDTO> adicionarBairroAoProfissional(@PathVariable Integer profissionalId,
            @PathVariable Integer bairroId) {
        Profissional profissional = profissionalService.adicionarBairroAoProfissional(profissionalId, bairroId);
        return ResponseEntity.ok().body(profissionalMapper.toDto(profissional));
    }

    // Remover associação entre Profissional e Bairro
    @DeleteMapping("/{profissionalId}/bairro/{bairroId}")
    public ResponseEntity<Void> removerBairroDoProfissional(@PathVariable Integer profissionalId,
            @PathVariable Integer bairroId) {
        profissionalService.removerBairroDoProfissional(profissionalId, bairroId);
        return ResponseEntity.noContent().build();
    }
}
