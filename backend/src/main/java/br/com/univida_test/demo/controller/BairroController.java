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
import org.springframework.web.bind.annotation.RequestParam;
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

    // Busca Dinâmica Unificada
    @GetMapping("/pesquisa")
    public ResponseEntity<List<BairroDto>> findDinamico(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) Boolean perigoso) {
        
        List<Bairro> list = bairroService.findByFiltrosDinamicos(id, nome, cidade, perigoso);
        List<BairroDto> listDto = new ArrayList<>();
        for (Bairro obj : list) {
            listDto.add(modelMapper.map(obj, BairroDto.class));
        }
        return ResponseEntity.ok().body(listDto);
    }

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


    // Criar/salvar um novo Bairro.
    @PostMapping
    public ResponseEntity<BairroDto> save(@RequestBody BairroDto bairroDto) {
        Bairro bairro = modelMapper.map(bairroDto, Bairro.class);
        Bairro sBairro = bairroService.save(bairro);
        return ResponseEntity.ok().body(modelMapper.map(sBairro, BairroDto.class));
    }

    // Atualizar um Bairro existente.
    @PutMapping("/{id}")
    public ResponseEntity<BairroDto> update (@PathVariable Integer id, @RequestBody BairroDto bairroDto) {
         bairroDto.setId(id);
         Bairro bairroUp = bairroService.update(modelMapper.map(bairroDto, Bairro.class));
         return ResponseEntity.ok().body(modelMapper.map(bairroUp, BairroDto.class));
        }

    // Deletar um Bairro por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bairroService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Buscar Bairro por nome exato (case-INsensitive)
    @GetMapping("/nome/{nome}")
    public ResponseEntity<BairroDto> findByNomeExato(@PathVariable String nome) {
        Bairro bairro = bairroService.findByNomeExato(nome);
        return ResponseEntity.ok().body(modelMapper.map(bairro, BairroDto.class));
    }

    // Buscar Bairro(s) por nome contendo (busca parcial, case-insensitive)
    @GetMapping("/buscar/{nome}")
    public ResponseEntity<List<BairroDto>> findByNomeContendo(@PathVariable String nome) {
        List<Bairro> list = bairroService.findByNomeContendo(nome);
        List<BairroDto> listDto = new ArrayList<>();
        for (Bairro obj : list) {
            listDto.add(modelMapper.map(obj, BairroDto.class));
        }
        return ResponseEntity.ok().body(listDto);
    }

    // Buscar Bairro(s) por cidade - manter
    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<BairroDto>> findByCidade(@PathVariable String cidade) {
        List<Bairro> list = bairroService.findByCidade(cidade);
        List<BairroDto> listDto = new ArrayList<>();
        for (Bairro obj : list) {
            listDto.add(modelMapper.map(obj, BairroDto.class));
        }
        return ResponseEntity.ok().body(listDto);
    }

    // Buscar Bairro(s) por risco/perigo
    @GetMapping("/risco/{perigo}")
    public ResponseEntity<List<BairroDto>> findByPerigoso(@PathVariable boolean perigo) {
        List<Bairro> list = bairroService.findByPerigoso(perigo);
        List<BairroDto> listDto = new ArrayList<>();
        for (Bairro obj : list) {
            listDto.add(modelMapper.map(obj, BairroDto.class));
        }
        return ResponseEntity.ok().body(listDto);
    }
}





