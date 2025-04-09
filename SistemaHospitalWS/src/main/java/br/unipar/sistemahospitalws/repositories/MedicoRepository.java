package br.unipar.sistemahospitalws.repositories;

import br.unipar.sistemahospitalws.domain.Medico;
import br.unipar.sistemahospitalws.dto.MedicoInsertRequestDTO;
import br.unipar.sistemahospitalws.dto.MedicoUpdateRequestDTO;
import br.unipar.sistemahospitalws.enums.Especialidade;
import br.unipar.sistemahospitalws.infraestructure.ConnectionFactory;
import javax.naming.NamingException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicoRepository {

    private static final String INSERT_MEDICO =
            "INSERT INTO medico (nome, email, telefone, crm, especialidade, ativo) " +
                    "VALUES (?, ?, ?, ?, ?, true) RETURNING id";

    private static final String INSERT_ENDERECO =
            "INSERT INTO endereco (medico_id, logradouro, numero, complemento, bairro) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_MEDICO =
            "UPDATE medico SET nome = ?, telefone = ? WHERE id = ? AND ativo = true";

    private static final String UPDATE_ENDERECO =
            "UPDATE endereco SET logradouro = ?, numero = ?, complemento = ?, bairro = ? " +
                    "WHERE medico_id = ?";

    private static final String DELETE =
            "UPDATE medico SET ativo = false WHERE id = ?";

    private static final String SELECT_ALL =
            "SELECT id, nome, email, crm, especialidade FROM medico WHERE ativo = true ORDER BY nome ASC";

    public Medico inserir(MedicoInsertRequestDTO dto) throws SQLException, NamingException {
        Connection conn = null;
        PreparedStatement pstmtMedico = null;
        PreparedStatement pstmtEndereco = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            conn.setAutoCommit(false);

            pstmtMedico = conn.prepareStatement(INSERT_MEDICO, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtMedico.setString(1, dto.getNome());
            pstmtMedico.setString(2, dto.getEmail());
            pstmtMedico.setString(3, dto.getTelefone());
            pstmtMedico.setLong(4, dto.getCrm());
            pstmtMedico.setString(5, dto.getEspecialidade().name());
            pstmtMedico.executeUpdate();

            rs = pstmtMedico.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("Erro ao obter ID do médico");
            }
            int medicoId = rs.getInt(1);

            pstmtEndereco = conn.prepareStatement(INSERT_ENDERECO);
            pstmtEndereco.setInt(1, medicoId);
            pstmtEndereco.setString(2, dto.getEndereco().getLogradouro());
            pstmtEndereco.setString(3, dto.getEndereco().getNumero());
            pstmtEndereco.setString(4, dto.getEndereco().getComplemento());
            pstmtEndereco.setString(5, dto.getEndereco().getBairro());
            pstmtEndereco.executeUpdate();

            conn.commit();

            Medico medico = new Medico();
            medico.setId(medicoId);
            medico.setNome(dto.getNome());
            medico.setEmail(dto.getEmail());
            medico.setTelefone(dto.getTelefone());
            medico.setCrm(dto.getCrm());
            medico.setEspecialidade(dto.getEspecialidade());
            medico.setEndereco(dto.getEndereco());

            return medico;

        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (pstmtMedico != null) pstmtMedico.close();
            if (pstmtEndereco != null) pstmtEndereco.close();
            if (conn != null) conn.close();
        }
    }

    public MedicoUpdateRequestDTO editar(MedicoUpdateRequestDTO dto) throws SQLException, NamingException {
        Connection conn = null;
        PreparedStatement pstmtMedico = null;
        PreparedStatement pstmtEndereco = null;

        try {
            conn = new ConnectionFactory().getConnection();
            conn.setAutoCommit(false);

            pstmtMedico = conn.prepareStatement(UPDATE_MEDICO);
            pstmtMedico.setString(1, dto.getNome());
            pstmtMedico.setString(2, dto.getTelefone());
            pstmtMedico.setInt(3, dto.getId());
            pstmtMedico.executeUpdate();

            pstmtEndereco = conn.prepareStatement(UPDATE_ENDERECO);
            pstmtEndereco.setString(1, dto.getEndereco().getLogradouro());
            pstmtEndereco.setString(2, dto.getEndereco().getNumero());
            pstmtEndereco.setString(3, dto.getEndereco().getComplemento());
            pstmtEndereco.setString(4, dto.getEndereco().getBairro());
            pstmtEndereco.setInt(5, dto.getId());
            pstmtEndereco.executeUpdate();

            conn.commit();
            return dto;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (pstmtMedico != null) pstmtMedico.close();
            if (pstmtEndereco != null) pstmtEndereco.close();
            if (conn != null) conn.close();
        }
    }

    public void excluir(Integer id) throws SQLException, NamingException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = new ConnectionFactory().getConnection();
            pstmt = conn.prepareStatement(DELETE);
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nenhum médico encontrado com ID: " + id);
            }

        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public List<Medico> listar() throws SQLException, NamingException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Medico> medicos = new ArrayList<>();

        try {
            conn = new ConnectionFactory().getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Medico medico = new Medico();
                medico.setId(rs.getInt("id"));
                medico.setNome(rs.getString("nome"));
                medico.setEmail(rs.getString("email"));
                medico.setCrm(rs.getLong("crm"));
                medico.setEspecialidade(Especialidade.valueOf(rs.getString("especialidade")));

                medicos.add(medico);
            }

            return medicos;

        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public Medico buscarPorId(Integer id) throws SQLException, NamingException {
        String query = "SELECT * FROM medico WHERE id = ? AND ativo = true";
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Medico medico = new Medico();
                medico.setId(rs.getInt("id"));
                medico.setNome(rs.getString("nome"));
                // Preencher outros campos se necessário
                return medico;
            }

            return null;
        }
    }

    public List<Medico> buscarMedicosAtivos() throws SQLException, NamingException {
        List<Medico> medicos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM medico WHERE ativo = true";

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Medico medico = new Medico();
                medico.setId(rs.getInt("id"));
                medico.setNome(rs.getString("nome"));
                medico.setCrm(rs.getLong("crm"));
                medico.setEspecialidade(Especialidade.valueOf(rs.getString("especialidade")));
                medico.setAtivo(rs.getBoolean("ativo"));
                medicos.add(medico);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }

        return medicos;
    }

    public Medico buscarMedicoDisponivelAleatorio(LocalDateTime dataHora) throws SQLException, NamingException {
        String sql = "SELECT * FROM medico WHERE id NOT IN (" +
                "SELECT medico_id FROM consulta WHERE data_hora = ? AND cancelada = false) " +
                "ORDER BY RANDOM() LIMIT 1";

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(dataHora));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Medico medico = new Medico();
                medico.setId(rs.getInt("id"));
                medico.setNome(rs.getString("nome"));
                return medico;
            } else {
                return null;
            }
        }
    }
}
