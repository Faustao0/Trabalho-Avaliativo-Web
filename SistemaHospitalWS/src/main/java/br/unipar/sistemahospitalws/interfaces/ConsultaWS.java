package br.unipar.sistemahospitalws.interfaces;

import br.unipar.sistemahospitalws.domain.Consulta;
import br.unipar.sistemahospitalws.dto.ConsultaCancelamentoRequestDTO;
import br.unipar.sistemahospitalws.dto.ConsultaInsertRequestDTO;
import br.unipar.sistemahospitalws.exceptions.BusinessException;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

import javax.naming.NamingException;
import java.sql.SQLException;

@WebService
public interface ConsultaWS {

    @WebMethod
    Consulta agendar(ConsultaInsertRequestDTO consultaDTO) throws BusinessException, SQLException, NamingException;

    @WebMethod
    void cancelar(ConsultaCancelamentoRequestDTO cancelamentoDTO) throws BusinessException, SQLException, NamingException;
}
