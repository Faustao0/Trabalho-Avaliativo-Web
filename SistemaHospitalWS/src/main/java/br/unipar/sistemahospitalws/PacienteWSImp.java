package br.unipar.sistemahospitalws;

import br.unipar.sistemahospitalws.domain.Paciente;
import br.unipar.sistemahospitalws.dto.PacienteInsertRequestDTO;
import br.unipar.sistemahospitalws.dto.PacienteUpdateRequestDTO;
import br.unipar.sistemahospitalws.exceptions.BusinessException;
import br.unipar.sistemahospitalws.interfaces.PacienteWS;
import br.unipar.sistemahospitalws.services.PacienteService;
import jakarta.jws.WebService;
import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;

@WebService
public class PacienteWSImp implements PacienteWS {

    private final PacienteService pacienteService;

    public PacienteWSImp(){
        this.pacienteService = new PacienteService();
    }

    @Override
    public Paciente inserir(PacienteInsertRequestDTO pacienteDTO) throws BusinessException, SQLException, NamingException {
        Paciente paciente = new Paciente();
        paciente.setNome(pacienteDTO.getNome());
        paciente.setCpf(pacienteDTO.getCpf());
        paciente.setEndereco(pacienteDTO.getEndereco());

        return pacienteService.inserir(pacienteDTO);
    }

    @Override
    public PacienteUpdateRequestDTO editar(PacienteUpdateRequestDTO pacienteDTO) throws BusinessException, SQLException, NamingException {
        return pacienteService.editar(pacienteDTO);
    }

    @Override
    public List<Paciente> listar() throws SQLException, NamingException {
        return pacienteService.listar();
    }

    @Override
    public void excluir(Long id) throws BusinessException, SQLException, NamingException {
        pacienteService.excluir(Math.toIntExact(id));
    }
}
