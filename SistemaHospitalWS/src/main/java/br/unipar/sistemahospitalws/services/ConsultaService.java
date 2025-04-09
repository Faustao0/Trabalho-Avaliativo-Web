package br.unipar.sistemahospitalws.services;

import br.unipar.sistemahospitalws.domain.Consulta;
import br.unipar.sistemahospitalws.domain.Medico;
import br.unipar.sistemahospitalws.domain.Paciente;
import br.unipar.sistemahospitalws.dto.ConsultaCancelamentoRequestDTO;
import br.unipar.sistemahospitalws.dto.ConsultaInsertRequestDTO;
import br.unipar.sistemahospitalws.exceptions.BusinessException;
import br.unipar.sistemahospitalws.repositories.ConsultaRepository;
import br.unipar.sistemahospitalws.repositories.MedicoRepository;
import br.unipar.sistemahospitalws.repositories.PacienteRepository;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public ConsultaService() {
        this.consultaRepository = new ConsultaRepository();
        this.medicoRepository = new MedicoRepository();
        this.pacienteRepository = new PacienteRepository();
    }

    public Consulta agendar(ConsultaInsertRequestDTO dto) throws SQLException, NamingException, BusinessException {
        LocalDateTime dataHora = dto.getDataHoraAsLocalDateTime();
        validarDadosConsulta(dataHora);

        Medico medico;

        if (dto.getMedicoId() == null || dto.getMedicoId() == 0) {
            medico = medicoRepository.buscarMedicoDisponivelAleatorio(dto.getDataHoraAsLocalDateTime());
            if (medico == null) {
                throw new IllegalStateException("Nenhum médico disponível para o horário informado");
            }
            dto.setMedicoId(medico.getId());
        } else {
            medico = medicoRepository.buscarPorId(dto.getMedicoId());
        }

        if (medico == null || Boolean.FALSE.equals(medico.getAtivo())) {
            throw new BusinessException("Médico não encontrado ou inativo.");
        }

        Paciente paciente = pacienteRepository.buscarPorId(dto.getPacienteId());
        if (paciente == null || Boolean.FALSE.equals(paciente.getAtivo())) {
            throw new BusinessException("Paciente não encontrado ou inativo.");
        }

        if (consultaRepository.jaPossuiConsultaNoDia(dto.getPacienteId(), dataHora.toLocalDate())) {
            throw new BusinessException("Paciente já possui uma consulta agendada neste dia.");
        }

        if (consultaRepository.medicoIndisponivel(medico.getId(), dataHora)) {
            throw new BusinessException("Médico já possui uma consulta nesse horário.");
        }

        return consultaRepository.inserir(dto, medico, paciente);
    }

    public void cancelar(ConsultaCancelamentoRequestDTO dto) throws SQLException, NamingException, BusinessException {
        if (dto.getMotivoCancelamento() == null) {
            throw new BusinessException("Motivo do cancelamento é obrigatório.");
        }

        Consulta consulta = consultaRepository.buscarPorId(dto.getId());
        if (consulta == null) {
            throw new BusinessException("Consulta não encontrada.");
        }

        LocalDateTime dataHoraConsulta = consulta.getDaraHora();
        Duration duracao = Duration.between(LocalDateTime.now(), dataHoraConsulta);
        if (duracao.toHours() < 24) {
            throw new BusinessException("Cancelamento deve ser feito com no mínimo 24 horas de antecedência.");
        }

        consultaRepository.cancelar(dto.getId(), dto.getMotivoCancelamento());
    }

    private void validarDadosConsulta(LocalDateTime dataHora) throws BusinessException {
        if (dataHora.isBefore(LocalDateTime.now().plusMinutes(30))) {
            throw new BusinessException("Consulta deve ser agendada com no mínimo 30 minutos de antecedência.");
        }

        DayOfWeek dia = dataHora.getDayOfWeek();
        int hora = dataHora.getHour();

        if (dia == DayOfWeek.SUNDAY || hora < 7 || hora >= 19) {
            throw new BusinessException("Consulta fora do horário de funcionamento da clínica.");
        }
    }

    private Medico buscarMedicoDisponivel(LocalDateTime dataHora) throws SQLException, NamingException {
        List<Medico> medicos = medicoRepository.buscarMedicosAtivos();
        for (Medico medico : medicos) {
            boolean indisponivel = consultaRepository.medicoIndisponivel(medico.getId(), dataHora);
            if (!indisponivel) return medico;
        }
        return null;
    }
}
