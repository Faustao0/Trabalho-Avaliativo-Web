package br.unipar.sistemahospitalws.services;

import br.unipar.sistemahospitalws.domain.Medico;
import br.unipar.sistemahospitalws.dto.MedicoInsertRequestDTO;
import br.unipar.sistemahospitalws.dto.MedicoUpdateRequestDTO;
import br.unipar.sistemahospitalws.exceptions.BusinessException;
import br.unipar.sistemahospitalws.repositories.MedicoRepository;
import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;

public class MedicoService {

    private MedicoRepository medicoRepository;

    public MedicoService() {
        this.medicoRepository = new MedicoRepository();
    }

    public Medico inserir(MedicoInsertRequestDTO dto) throws BusinessException, SQLException, NamingException {
        validarDadosInsercao(dto);

        List<Medico> medicos = medicoRepository.listar();
        for (Medico m : medicos) {
            if (m.getCrm().equals(dto.getCrm())) {
                throw new BusinessException("Já existe um médico cadastrado com este CRM");
            }
        }

        return medicoRepository.inserir(dto);
    }

    public MedicoUpdateRequestDTO editar(MedicoUpdateRequestDTO dto) throws BusinessException, SQLException, NamingException {
        validarDadosEdicao(dto);

        return medicoRepository.editar(dto);
    }

    public void excluir(Integer id) throws BusinessException, SQLException, NamingException {
        List<Medico> medicos = medicoRepository.listar();
        boolean encontrado = medicos.stream().anyMatch(m -> m.getId().equals(id));

        if (!encontrado) {
            throw new BusinessException("Médico não encontrado ou já inativo");
        }

        medicoRepository.excluir(id);
    }

    public List<Medico> listar() throws SQLException, NamingException {
        return medicoRepository.listar();
    }

    private void validarDadosInsercao(MedicoInsertRequestDTO dto) throws BusinessException {
        if (dto.getNome() == null || dto.getNome().isEmpty()) {
            throw new BusinessException("Nome do médico é obrigatório");
        }

        if (dto.getCrm() == null || dto.getCrm() < 100000 || dto.getCrm() > 999999) {
            throw new BusinessException("CRM inválido. Deve conter exatamente 6 dígitos numéricos");
        }

        if (dto.getEspecialidade() == null) {
            throw new BusinessException("Especialidade do médico é obrigatória");
        }

        if (dto.getEndereco().getBairro() == null || dto.getEndereco().getBairro().isEmpty()) {
            throw new BusinessException("O Bairro do médico é obrigatório");
        }

        String numero = dto.getEndereco().getNumero();
        if (numero != null && !numero.isBlank() && !numero.matches("\\d+")) {
            throw new BusinessException("Número do endereço deve conter apenas dígitos!");
        }
    }

    private void validarDadosEdicao(MedicoUpdateRequestDTO dto) throws BusinessException {
        if (dto.getNome() == null || dto.getNome().isEmpty()) {
            throw new BusinessException("Nome do médico é inválido");
        }

        if (dto.getTelefone() == null || dto.getTelefone().isEmpty()) {
            throw new BusinessException("Telefone é obrigatório");
        }

        String numero = dto.getEndereco().getNumero();
        if (numero != null && !numero.isBlank() && !numero.matches("\\d+")) {
            throw new BusinessException("Número do endereço deve conter apenas dígitos, se informado.");
        }

        if (dto.getEndereco().getBairro() == null || dto.getEndereco().getBairro().isEmpty()) {
            throw new BusinessException("O Bairro do médico é obrigatório");
        }
    }
}
