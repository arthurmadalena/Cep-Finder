package br.com.arthur.madalena.cepmanager.rest;

import br.com.arthur.madalena.cepmanager.dto.CepDTO;
import br.com.arthur.madalena.cepmanager.service.CepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ceps")
@RequiredArgsConstructor
@Tag(name = "CEP", description = "API para gerenciamento de CEPs")
public class CepRestController {

    private final CepService cepService;

    @GetMapping("/id/{id}")
    @Operation(summary = "Buscar CEP por ID", description = "Retorna os dados de um CEP específico pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CEP encontrado"),
        @ApiResponse(responseCode = "404", description = "CEP não encontrado")
    })
    public ResponseEntity<CepDTO> findById(
            @Parameter(description = "ID do CEP", example = "1")
            @PathVariable Long id) {
        CepDTO cep = cepService.findById(id);
        return ResponseEntity.ok(cep);
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Buscar CEP por código", description = "Retorna os dados de um CEP específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CEP encontrado"),
        @ApiResponse(responseCode = "404", description = "CEP não encontrado")
    })
    public ResponseEntity<CepDTO> findByCodigo(
            @Parameter(description = "Código do CEP (8 dígitos)", example = "01310100")
            @PathVariable String codigo) {
        CepDTO cep = cepService.findByCodigo(codigo);
        return ResponseEntity.ok(cep);
    }

    @GetMapping("/logradouro/{logradouro}")
    @Operation(summary = "Buscar CEPs por logradouro", description = "Retorna lista de CEPs contendo o logradouro informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de CEPs retornada com sucesso")
    })
    public ResponseEntity<Page<CepDTO>> findByLogradouro(
            @Parameter(description = "Logradouro a ser buscado", example = "Paulista")
            @PathVariable String logradouro,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CepDTO> ceps = cepService.findByLogradouro(logradouro, pageable);
        return ResponseEntity.ok(ceps);
    }

    @GetMapping("/cidade/{cidade}")
    @Operation(summary = "Buscar CEPs por cidade", description = "Retorna lista de CEPs de uma cidade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de CEPs retornada com sucesso")
    })
    public ResponseEntity<Page<CepDTO>> findByCidade(
            @Parameter(description = "Nome da cidade", example = "São Paulo")
            @PathVariable String cidade,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CepDTO> ceps = cepService.findByCidade(cidade, pageable);
        return ResponseEntity.ok(ceps);
    }

    @GetMapping("/cidade/{cidade}/uf/{uf}")
    @Operation(summary = "Buscar CEPs por cidade e UF", description = "Retorna lista de CEPs de uma cidade específica e UF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de CEPs retornada com sucesso")
    })
    public ResponseEntity<Page<CepDTO>> findByCidadeAndUf(
            @Parameter(description = "Nome da cidade", example = "São Paulo")
            @PathVariable String cidade,
            @Parameter(description = "UF (2 caracteres)", example = "SP")
            @PathVariable String uf,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CepDTO> ceps = cepService.findByCidadeAndUf(cidade, uf, pageable);
        return ResponseEntity.ok(ceps);
    }

    @GetMapping
    @Operation(summary = "Listar todos os CEPs", description = "Retorna lista paginada de todos os CEPs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de CEPs retornada com sucesso")
    })
    public ResponseEntity<Page<CepDTO>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        Page<CepDTO> ceps = cepService.findAll(pageable);
        return ResponseEntity.ok(ceps);
    }

    @GetMapping("/search")
    @Operation(summary = "Pesquisar CEPs", description = "Pesquisa CEPs por logradouro, bairro ou cidade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resultados da pesquisa retornados com sucesso")
    })
    public ResponseEntity<Page<CepDTO>> search(
            @Parameter(description = "Termo de pesquisa", example = "Centro")
            @RequestParam String termo,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CepDTO> ceps = cepService.search(termo, pageable);
        return ResponseEntity.ok(ceps);
    }

    @PostMapping
    @Operation(summary = "Criar novo CEP", description = "Cria um novo registro de CEP")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "CEP criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "CEP já cadastrado")
    })
    public ResponseEntity<CepDTO> create(@Valid @RequestBody CepDTO cepDTO) {
        CepDTO created = cepService.create(cepDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    @Operation(summary = "Atualizar CEP", description = "Atualiza os dados de um CEP existente (codigo vem no body)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CEP atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "CEP não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<CepDTO> update(@Valid @RequestBody CepDTO cepDTO) {
        CepDTO updated = cepService.update(cepDTO.getCodigo(), cepDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Deletar CEP", description = "Remove um CEP do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "CEP deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "CEP não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Código do CEP (8 dígitos)", example = "01310100")
            @PathVariable String codigo) {
        cepService.delete(codigo);
        return ResponseEntity.noContent().build();
    }
}

