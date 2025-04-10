package br.unipar.sistemahospitalws.repositories;

import br.unipar.sistemahospitalws.domain.Consulta;
import br.unipar.sistemahospitalws.domain.Medico;
import br.unipar.sistemahospitalws.domain.Paciente;
import br.unipar.sistemahospitalws.dto.ConsultaInsertRequestDTO;
import br.unipar.sistemahospitalws.enums.MotivoCancelamento;
import br.unipar.sistemahospitalws.infraestructure.ConnectionFactory;
import javax.naming.NamingException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConsultaRepository {

    private static final String INSERT =
            "INSERT INTO consulta (paciente_id, medico_id, data_hora, cancelada) " +
                    "VALUES (?, ?, ?, false) RETURNING id";

    private static final String UPDATE_CANCELAR =
            "UPDATE consulta SET cancelada = true, motivo_cancelamento = ? WHERE id = ?";

    private static final String BUSCAR_CONSULTA_POR_ID =
            "SELECT * FROM consulta WHERE id = ?";

    private static final String JA_POSSUI_CONSULTA_NO_DIA =
            "SELECT COUNT(*) FROM consulta WHERE paciente_id = ? AND DATE(data_hora) = ? AND cancelada = false";

    private static final String MEDICO_INDISPONIVEL =
            "SELECT COUNT(*) FROM consulta WHERE medico_id = ? AND data_hora = ? AND cancelada = false";

    public Consulta inserir(ConsultaInsertRequestDTO dto, Medico medico, Paciente paciente) throws SQLException, NamingException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            pstmt = conn.prepareStatement(INSERT);
            pstmt.setInt(1, dto.getPacienteId());
            pstmt.setInt(2, dto.getMedicoId());
            pstmt.setTimestamp(3, Timestamp.valueOf(dto.getDataHoraAsLocalDateTime()));

            rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Erro ao obter ID da consulta inserida");
            }

            int consultaId = rs.getInt(1);
            Consulta consulta = new Consulta();
            consulta.setId(consultaId);
            consulta.setPaciente(null);
            consulta.setMedico(null);
            consulta.setDataHora(dto.getDataHoraAsLocalDateTime());
            consulta.setCancelada(false);

            return consulta;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public void cancelar(Integer idConsulta, MotivoCancelamento motivo) throws SQLException, NamingException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = new ConnectionFactory().getConnection();
            pstmt = conn.prepareStatement(UPDATE_CANCELAR);
            pstmt.setString(1, motivo.name());
            pstmt.setInt(2, idConsulta);

            int linhas = pstmt.executeUpdate();
            if (linhas == 0) {
                throw new SQLException("Consulta não encontrada para cancelamento");
            }
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public Consulta buscarPorId(Integer id) throws SQLException, NamingException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            pstmt = conn.prepareStatement(BUSCAR_CONSULTA_POR_ID);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Consulta consulta = new Consulta();
                consulta.setId(rs.getInt("id"));
                consulta.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
                consulta.setCancelada(rs.getBoolean("cancelada"));
                String motivoStr = rs.getString("motivo_cancelamento");
                if (motivoStr != null) {
                    consulta.setMotivoCancelamento(MotivoCancelamento.valueOf(motivoStr));
                }
                return consulta;
            }

            return null;

        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public boolean jaPossuiConsultaNoDia(Integer pacienteId, LocalDate data) throws SQLException, NamingException {
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(JA_POSSUI_CONSULTA_NO_DIA)) {
            pstmt.setInt(1, pacienteId);
            pstmt.setDate(2, Date.valueOf(data));

            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public boolean medicoIndisponivel(Integer medicoId, LocalDateTime dataHora) throws SQLException, NamingException {
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(MEDICO_INDISPONIVEL)) {
            pstmt.setInt(1, medicoId);
            pstmt.setTimestamp(2, Timestamp.valueOf(dataHora));

            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }
}
