package br.com.arthur.madalena.cepmanager.mapper;

import br.com.arthur.madalena.cepmanager.dto.UsuarioDTO;
import br.com.arthur.madalena.cepmanager.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDTO toDTO(Usuario entity) {
        if (entity == null) {
            return null;
        }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setNomeCompleto(entity.getNomeCompleto());
        dto.setAtivo(entity.getAtivo());
        dto.setEmailVerificado(entity.getEmailVerificado());
        dto.setPermissoes(entity.getPermissoes());
        dto.setDatHoraCadastro(entity.getDatHoraCadastro());
        dto.setDatHoraAlteracao(entity.getDatHoraAlteracao());
        dto.setUsuarioCadastro(entity.getUsuarioCadastro());
        dto.setUsuarioAlteracao(entity.getUsuarioAlteracao());

        return dto;
    }

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }

        Usuario entity = new Usuario();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setNomeCompleto(dto.getNomeCompleto());
        entity.setAtivo(dto.getAtivo());
        entity.setEmailVerificado(dto.getEmailVerificado());
        entity.setPermissoes(dto.getPermissoes());
        entity.setUsuarioCadastro(dto.getUsuarioCadastro());
        entity.setUsuarioAlteracao(dto.getUsuarioAlteracao());

        return entity;
    }
}
