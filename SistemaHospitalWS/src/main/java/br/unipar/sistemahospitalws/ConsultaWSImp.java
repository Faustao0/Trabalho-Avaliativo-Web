package br.unipar.sistemahospitalws;

import br.unipar.sistemahospitalws.domain.Consulta;
import br.unipar.sistemahospitalws.dto.ConsultaCancelamentoRequestDTO;
import br.unipar.sistemahospitalws.dto.ConsultaInsertRequestDTO;
import br.unipar.sistemahospitalws.exceptions.BusinessException;
import br.unipar.sistemahospitalws.interfaces.ConsultaWS;
import br.unipar.sistemahospitalws.services.ConsultaService;
import jakarta.jws.WebService;
import javax.naming.NamingException;
import java.sql.SQLException;

@WebService
public class ConsultaWSImp implements ConsultaWS {

    private final ConsultaService consultaService;

    public ConsultaWSImp(){
        this.consultaService = new ConsultaService();
    }

    @Override
    public Consulta agendar(ConsultaInsertRequestDTO dto) throws BusinessException, SQLException, NamingException {
        return consultaService.agendar(dto);
    }

    @Override
    public void cancelar(ConsultaCancelamentoRequestDTO dto) throws BusinessException, SQLException, NamingException {
        consultaService.cancelar(dto);
    }
}
