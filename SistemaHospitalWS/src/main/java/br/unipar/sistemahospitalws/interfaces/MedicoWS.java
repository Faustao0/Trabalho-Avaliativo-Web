package br.unipar.sistemahospitalws.interfaces;

import br.unipar.sistemahospitalws.domain.Medico;
import br.unipar.sistemahospitalws.dto.MedicoInsertRequestDTO;
import br.unipar.sistemahospitalws.dto.MedicoUpdateRequestDTO;
import br.unipar.sistemahospitalws.exceptions.BusinessException;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;

@WebService
public interface MedicoWS {

    @WebMethod
    Medico inserir(MedicoInsertRequestDTO medico) throws BusinessException, SQLException, NamingException;

    @WebMethod
    MedicoUpdateRequestDTO editar(MedicoUpdateRequestDTO medico) throws BusinessException, SQLException, NamingException;

    @WebMethod
    List<Medico> listar() throws SQLException, NamingException;

    @WebMethod
    void excluir(Long id) throws BusinessException, SQLException, NamingException;
}
