package br.com.arthur.madalena.cepmanager.beans;

import br.com.arthur.madalena.cepmanager.dto.CepDTO;
import br.com.arthur.madalena.cepmanager.service.CepService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Component("cepBean")
@ViewScoped
@Data
@RequiredArgsConstructor
public class CepBean implements Serializable {

    private final CepService cepService;

    private CepDTO cepSelecionado;
    private List<CepDTO> ceps;
    
    private String filtroCodigo;
    private String filtroLogradouro;
    private String filtroBairro;
    private String filtroCidade;
    private String filtroUf;
    private String pesquisaGeral;
    
    private boolean modoEdicao = false;
    private Long idSelecionado;
    
    private Long totalCeps;
    private Long totalCidades;
    private Long totalEstados;
    private List<Map.Entry<String, Long>> cepsPorEstado;
    private Long maxCepsPorEstado;
    
    private int currentPage = 0;
    private int pageSize = 10;
    private long totalElements;

    @PostConstruct
    public void init() {
        cepSelecionado = new CepDTO();
        ceps = null;
        loadDashboardData();
    }
    
    public void loadDashboardData() {
        try {
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            List<CepDTO> allCeps = cepService.findAll(pageable).getContent();
            
            totalCeps = (long) allCeps.size();
            totalCidades = allCeps.stream().map(CepDTO::getCidade).distinct().count();
            totalEstados = allCeps.stream().map(CepDTO::getUf).distinct().count();
            
            Map<String, Long> estadoMap = allCeps.stream()
                .collect(Collectors.groupingBy(CepDTO::getUf, Collectors.counting()));
            
            cepsPorEstado = estadoMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
            
            maxCepsPorEstado = estadoMap.values().stream()
                .max(Long::compare)
                .orElse(1L);
                
        } catch (Exception e) {
            totalCeps = 0L;
            totalCidades = 0L;
            totalEstados = 0L;
            cepsPorEstado = new ArrayList<>();
            maxCepsPorEstado = 1L;
        }
    }

    public void loadCeps() {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<CepDTO> page = cepService.findAll(pageable);
        ceps = page.getContent();
        totalElements = page.getTotalElements();
    }

    public void buscar() {
        try {
            Pageable pageable = PageRequest.of(currentPage, 15);
            Page<CepDTO> page;
            
            if (pesquisaGeral != null && !pesquisaGeral.trim().isEmpty()) {
                page = cepService.search(pesquisaGeral, pageable);
            } else if (filtroCodigo != null && !filtroCodigo.trim().isEmpty()) {
                String codigoLimpo = filtroCodigo.replaceAll("[^0-9]", "");
                CepDTO cepEncontrado = cepService.findByCodigo(codigoLimpo);
                ceps = List.of(cepEncontrado);
                totalElements = 1;
                addMessage(FacesMessage.SEVERITY_INFO, "CEP encontrado!");
                return;
            } else {
                page = cepService.findAll(pageable);
                
                List<CepDTO> resultado = page.getContent();
                
                if (filtroLogradouro != null && !filtroLogradouro.trim().isEmpty()) {
                    String logradouroLower = filtroLogradouro.toLowerCase();
                    resultado = resultado.stream()
                            .filter(c -> c.getLogradouro().toLowerCase().contains(logradouroLower))
                            .collect(Collectors.toList());
                }
                
                if (filtroBairro != null && !filtroBairro.trim().isEmpty()) {
                    String bairroLower = filtroBairro.toLowerCase();
                    resultado = resultado.stream()
                            .filter(c -> c.getBairro().toLowerCase().contains(bairroLower))
                            .collect(Collectors.toList());
                }
                
                if (filtroCidade != null && !filtroCidade.trim().isEmpty()) {
                    String cidadeLower = filtroCidade.toLowerCase();
                    resultado = resultado.stream()
                            .filter(c -> c.getCidade().toLowerCase().contains(cidadeLower))
                            .collect(Collectors.toList());
                }
                
                if (filtroUf != null && !filtroUf.trim().isEmpty()) {
                    resultado = resultado.stream()
                            .filter(c -> c.getUf().equalsIgnoreCase(filtroUf))
                            .collect(Collectors.toList());
                }
                
                ceps = resultado;
                totalElements = resultado.size();
            }
            
            if (ceps.isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN, "Nenhum CEP encontrado com os filtros informados");
            } else {
                addMessage(FacesMessage.SEVERITY_INFO, "Encontrado(s) " + totalElements + " CEP(s)");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao buscar: " + e.getMessage());
            ceps = new ArrayList<>();
        }
    }

    public void limparFiltros() {
        filtroCodigo = null;
        filtroLogradouro = null;
        filtroBairro = null;
        filtroCidade = null;
        filtroUf = null;
        pesquisaGeral = null;
        currentPage = 0;
        loadCeps();
        addMessage(FacesMessage.SEVERITY_INFO, "Filtros limpos");
    }

    public String salvar() {
        try {
            // Limpa o código CEP removendo hífen e caracteres não numéricos
            if (cepSelecionado.getCodigo() != null) {
                String codigoLimpo = cepSelecionado.getCodigo().replaceAll("[^0-9]", "");
                cepSelecionado.setCodigo(codigoLimpo);
            }
            
            // Converte strings vazias para null nos campos opcionais
            if (cepSelecionado.getComplemento() != null && cepSelecionado.getComplemento().trim().isEmpty()) {
                cepSelecionado.setComplemento(null);
            }
            if (cepSelecionado.getIbge() != null && cepSelecionado.getIbge().trim().isEmpty()) {
                cepSelecionado.setIbge(null);
            }
            
            if (cepSelecionado.getId() == null) {
                cepService.create(cepSelecionado);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "CEP cadastrado com sucesso!", null));
            } else {
                cepService.update(cepSelecionado.getCodigo(), cepSelecionado);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "CEP atualizado com sucesso!", null));
            }
            cepSelecionado = new CepDTO();
            return "consulta?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar: " + e.getMessage());
            return null;
        }
    }

    public void excluir() {
        try {
            if (cepSelecionado != null && cepSelecionado.getCodigo() != null) {
                cepService.delete(cepSelecionado.getCodigo());
                addMessage(FacesMessage.SEVERITY_INFO, "CEP excluído com sucesso!");
                cepSelecionado = new CepDTO();
                loadCeps();
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao excluir: " + e.getMessage());
        }
    }

    public String novoCep() {
        this.cepSelecionado = new CepDTO();
        return "cadastro?faces-redirect=true";
    }
    
    public String editarSelecionado() {
        if (cepSelecionado == null || cepSelecionado.getId() == null) {
            addMessage(FacesMessage.SEVERITY_WARN, "Selecione um CEP para editar");
            return null;
        }
        return "cadastro?faces-redirect=true";
    }
    
    public String visualizarSelecionado() {
        if (cepSelecionado == null || cepSelecionado.getId() == null) {
            addMessage(FacesMessage.SEVERITY_WARN, "Selecione um CEP para visualizar");
            return null;
        }
        this.modoEdicao = false;
        return "visualizar?faces-redirect=true";
    }

    public String editar(CepDTO cep) {
        this.cepSelecionado = cep;
        return "cadastro?faces-redirect=true";
    }

    public String visualizar(CepDTO cep) {
        this.cepSelecionado = cep;
        this.modoEdicao = false;
        return "visualizar?faces-redirect=true";
    }
    
    public void carregarCepPorId() {
        if (idSelecionado != null) {
            try {
                this.cepSelecionado = cepService.findById(idSelecionado);
            } catch (Exception e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "CEP não encontrado");
                this.cepSelecionado = new CepDTO();
            }
        }
    }
    
    public String toggleModoEdicao() {
        if (this.modoEdicao) {
            try {
                this.cepSelecionado = cepService.findById(cepSelecionado.getId());
                this.modoEdicao = false;
                return null;
            } catch (Exception e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao recarregar dados");
                return null;
            }
        } else {
            this.modoEdicao = true;
            return null;
        }
    }
    
    public String excluirEVoltar() {
        try {
            if (cepSelecionado != null && cepSelecionado.getCodigo() != null) {
                cepService.delete(cepSelecionado.getCodigo());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "CEP excluído com sucesso!", null));
                return "consulta?faces-redirect=true";
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao excluir: " + e.getMessage());
        }
        return null;
    }

    public List<String> completeCidade(String query) {
        try {
            Pageable pageable = PageRequest.of(0, 10);
            return cepService.findByCidade(query, pageable).getContent()
                    .stream()
                    .map(CepDTO::getCidade)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage("growl", 
                new FacesMessage(severity, message, null));
    }
}


