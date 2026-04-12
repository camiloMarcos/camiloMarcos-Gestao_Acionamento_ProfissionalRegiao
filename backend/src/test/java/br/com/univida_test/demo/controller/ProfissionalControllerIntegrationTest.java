package br.com.univida_test.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import br.com.univida_test.demo.exceptions.ObjectNotFoundException;
import br.com.univida_test.demo.repositories.BairroRepository;
import br.com.univida_test.demo.repositories.ProfissionalRepository;
import br.com.univida_test.demo.service.ProfissionalService;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProfissionalControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private ProfissionalService profissionalService;

        @Autowired
        private ProfissionalRepository profissionalRepository;

        @Autowired
        private BairroRepository bairroRepository;

        @Test
        void deveAssociarProfissionalAoBairroEBuscarPorBairro() throws Exception {
                String sufixo = UUID.randomUUID().toString().substring(0, 8);
                Integer bairroId = criarBairro("Centro " + sufixo, "Fortaleza", false);
                Integer profissionalId = criarProfissional("Ana Lima " + sufixo, "Psicologia", sufixo);

                mockMvc.perform(post("/profissional/{profissionalId}/bairro/{bairroId}", profissionalId, bairroId))
                                .andExpect(status().isOk());

                String response = mockMvc.perform(get("/profissional/bairro/{bairroId}", bairroId))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                JsonNode root = objectMapper.readTree(response);
                assertEquals(1, root.size());
                assertEquals(profissionalId, root.get(0).get("id").asInt());
        }

        @Test
        void deveRemoverAssociacaoEImpidirQueOBairroContinueRetornandoProfissional() throws Exception {
                String sufixo = UUID.randomUUID().toString().substring(0, 8);
                Integer bairroId = criarBairro("Aldeota " + sufixo, "Fortaleza", true);
                Integer profissionalId = criarProfissional("Carlos Souza " + sufixo, "Fisioterapia", sufixo);

                mockMvc.perform(post("/profissional/{profissionalId}/bairro/{bairroId}", profissionalId, bairroId))
                                .andExpect(status().isOk());

                mockMvc.perform(delete("/profissional/{profissionalId}/bairro/{bairroId}", profissionalId, bairroId))
                                .andExpect(status().isNoContent());

                assertThrows(ObjectNotFoundException.class, () -> profissionalService.findByBairroId(bairroId));
                assertEquals(0, bairroRepository.findById(bairroId).orElseThrow().getProfissionais().size());
                assertEquals(0, profissionalRepository.findById(profissionalId).orElseThrow().getBairrosAtendidos()
                                .size());
        }

        private Integer criarBairro(String nome, String cidade, boolean perigoDistante) throws Exception {
                String payload = """
                                {
                                    "nome": "%s",
                                    "cidade": "%s",
                                    "perigo_Distante": %s
                                }
                                """.formatted(nome, cidade, perigoDistante);

                String response = mockMvc.perform(post("/bairro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                return objectMapper.readTree(response).get("id").asInt();
        }

        private Integer criarProfissional(String nome, String especialidade, String sufixo) throws Exception {
                String payload = """
                                {
                                    "nome": "%s",
                                    "especialidade": "%s",
                                    "numeroConselho": "12345",
                                    "telefone": "(85) 99999-9999",
                                                        "email": "teste%s@email.com",
                                    "endereco": "Rua A, 123",
                                                        "cidade": "Fortaleza",
                                                        "bairrosAtendidos": []
                                }
                                                """.formatted(nome, especialidade, sufixo);

                String response = mockMvc.perform(post("/profissional")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                return objectMapper.readTree(response).get("id").asInt();
        }
}
