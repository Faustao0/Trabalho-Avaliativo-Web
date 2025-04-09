package br.unipar.sistemahospitalws;

import br.unipar.sistemahospitalws.domain.Medico;
import br.unipar.sistemahospitalws.dto.MedicoInsertRequestDTO;
import br.unipar.sistemahospitalws.dto.MedicoUpdateRequestDTO;
import br.unipar.sistemahospitalws.exceptions.BusinessException;
import br.unipar.sistemahospitalws.interfaces.MedicoWS;
import br.unipar.sistemahospitalws.services.MedicoService;
import jakarta.jws.WebService;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;

@WebService
public class MedicoWSImp implements MedicoWS {

    private final MedicoService medicoService;

    public MedicoWSImp() {
        this.medicoService = new MedicoService();
    }

    @Override
    public Medico inserir(MedicoInsertRequestDTO medicoDTO) throws BusinessException, SQLException, NamingException {
        Medico medico = new Medico();
        medico.setNome(medicoDTO.getNome());
        medico.setCrm(medicoDTO.getCrm());
        medico.setEspecialidade(medicoDTO.getEspecialidade());
        medico.setEndereco(medicoDTO.getEndereco());

        return medicoService.inserir(medicoDTO);
    }

    @Override
    public MedicoUpdateRequestDTO editar(MedicoUpdateRequestDTO medicoDTO) throws BusinessException, SQLException, NamingException {
        return medicoService.editar(medicoDTO);
    }

    @Override
    public List<Medico> listar() throws SQLException, NamingException {
        return medicoService.listar();
    }

    @Override
    public void excluir(Long id) throws BusinessException, SQLException, NamingException {
        medicoService.excluir(Math.toIntExact(id));
    }
}
