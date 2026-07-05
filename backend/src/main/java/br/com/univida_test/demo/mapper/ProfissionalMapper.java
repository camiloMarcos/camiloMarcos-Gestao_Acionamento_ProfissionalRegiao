package br.com.univida_test.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.univida_test.demo.dtos.ProfissionalDTO;
import br.com.univida_test.demo.dtos.response.BairroResumoDTO;
import br.com.univida_test.demo.dtos.response.ProfissionalComBairrosDTO;
import br.com.univida_test.demo.models.Bairro;
import br.com.univida_test.demo.models.Profissional;

@Component
public class ProfissionalMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ProfissionalDTO toDto(Profissional p) {
        if (p == null) return null;
        return modelMapper.map(p, ProfissionalDTO.class);
    }

    public Profissional toEntity(ProfissionalDTO dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, Profissional.class);
    }

    public ProfissionalComBairrosDTO toComBairrosDto(Profissional p) {
        if (p == null) return null;

        List<BairroResumoDTO> bairros = (p.getBairrosAtendidos() == null)
            ? new java.util.ArrayList<>()
            : p.getBairrosAtendidos().stream()
                .map(this::toBairroResumoDto)
                .collect(Collectors.toList());

        return new ProfissionalComBairrosDTO(
            p.getId(),
            p.getNome(),
            p.getEspecialidade(),
            p.getNumeroConselho(),
            p.getTelefone(),
            p.getEmail(),
            p.getEndereco(),
            p.getCidade(),
            bairros
        );
    }

    public BairroResumoDTO toBairroResumoDto(Bairro b) {
        if (b == null) return null;
        return new BairroResumoDTO(b.getId(), b.getNome(), b.getCidade());
    }
}
