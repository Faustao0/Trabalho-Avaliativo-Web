package br.unipar.sistemahospitalws.services;

import br.unipar.sistemahospitalws.domain.Paciente;
import br.unipar.sistemahospitalws.dto.PacienteInsertRequestDTO;
import br.unipar.sistemahospitalws.dto.PacienteUpdateRequestDTO;
import br.unipar.sistemahospitalws.exceptions.BusinessException;
import br.unipar.sistemahospitalws.repositories.PacienteRepository;
import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;

public class PacienteService {

    private PacienteRepository pacienteRepository;

    public PacienteService(){
        this.pacienteRepository = new PacienteRepository();
    }

    public Paciente inserir(PacienteInsertRequestDTO dto) throws BusinessException, SQLException, NamingException {
        validarDadosInsercao(dto);

        List<Paciente> pacientes = pacienteRepository.listar();
        for (Paciente p : pacientes) {
            if (p.getCpf().equals(dto.getCpf())) {
                throw new BusinessException("Já existe um paciente cadastrado com este CPF");
            }
        }

        return pacienteRepository.inserir(dto);
    }

    public PacienteUpdateRequestDTO editar(PacienteUpdateRequestDTO dto) throws BusinessException, SQLException, NamingException {
        validarDadosEdicao(dto);

        List<Paciente> pacientes = pacienteRepository.listar();
        boolean encontrado = pacientes.stream().anyMatch(p -> p.getId().equals(dto.getId()));

        if (!encontrado) {
            throw new BusinessException("Paciente não encontrado ou ID inválido");
        }

        return pacienteRepository.editar(dto);
    }

    public void excluir(Integer id) throws BusinessException, SQLException, NamingException {
        List<Paciente> pacientes = pacienteRepository.listar();
        boolean encontrado = pacientes.stream().anyMatch(p -> p.getId().equals(id));

        if (!encontrado) {
            throw new BusinessException("Paciente não encontrado ou já inativo");
        }

        pacienteRepository.excluir(id);
    }

    public List<Paciente> listar() throws SQLException, NamingException {
        return pacienteRepository.listar();
    }

    private void validarDadosInsercao(PacienteInsertRequestDTO dto) throws BusinessException {
        if (dto.getNome() == null || dto.getNome().isEmpty()) {
            throw new BusinessException("Nome do paciente é obrigatório");
        }

        if (dto.getCpf() == null || dto.getCpf().isEmpty() || dto.getCpf().length() != 11) {
            throw new BusinessException("CPF inválido. Deve ter 11 caracteres");
        }

        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            throw new BusinessException("E-mail é obrigatório");
        }

        if (dto.getEndereco().getBairro() == null || dto.getEndereco().getBairro().isEmpty()) {
            throw new BusinessException("O bairro do paciente é obrigatório");
        }

        if (dto.getEndereco().getCidade() == null || dto.getEndereco().getCidade().isEmpty()) {
            throw new BusinessException("Cidade do paciente é obrigatória");
        }

        if (dto.getEndereco().getUf() == null || dto.getEndereco().getUf().isEmpty()) {
            throw new BusinessException("UF do paciente é obrigatória");
        }

        if (dto.getEndereco().getCep() == null || dto.getEndereco().getCep() < 10000000 || dto.getEndereco().getCep() > 99999999) {
            throw new BusinessException("CEP do paciente é obrigatório e deve conter exatamente 8 dígitos numéricos");
        }

        String numero = dto.getEndereco().getNumero();
        if (numero != null && !numero.isBlank() && !numero.matches("\\d+")) {
            throw new BusinessException("Número do endereço deve conter apenas dígitos!");
        }
    }

    private void validarDadosEdicao(PacienteUpdateRequestDTO dto) throws BusinessException {
        if (dto.getNome() == null || dto.getNome().isEmpty()) {
            throw new BusinessException("Nome do paciente é inválido");
        }

        if (dto.getTelefone() == null || dto.getTelefone().isEmpty()) {
            throw new BusinessException("Telefone é obrigatório");
        }

        if (dto.getEndereco().getUf() == null || dto.getEndereco().getUf().isEmpty()) {
            throw new BusinessException("UF do paciente é obrigatória");
        }

        String numero = dto.getEndereco().getNumero();
        if (numero != null && !numero.isBlank() && !numero.matches("\\d+")) {
            throw new BusinessException("Número do endereço deve conter apenas dígitos, se informado.");
        }
    }
}
