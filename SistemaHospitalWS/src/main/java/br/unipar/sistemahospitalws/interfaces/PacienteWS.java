package br.unipar.sistemahospitalws.interfaces;

import br.unipar.sistemahospitalws.domain.Paciente;
import br.unipar.sistemahospitalws.dto.PacienteInsertRequestDTO;
import br.unipar.sistemahospitalws.dto.PacienteUpdateRequestDTO;
import br.unipar.sistemahospitalws.exceptions.BusinessException;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;

@WebService
public interface PacienteWS {

    @WebMethod
    Paciente inserir(PacienteInsertRequestDTO paciente) throws BusinessException, SQLException, NamingException;

    @WebMethod
    PacienteUpdateRequestDTO editar(PacienteUpdateRequestDTO paciente) throws BusinessException, SQLException, NamingException;

    @WebMethod
    List<Paciente> listar() throws SQLException, NamingException;

    @WebMethod
    void excluir(Long id) throws BusinessException, SQLException, NamingException;

}
