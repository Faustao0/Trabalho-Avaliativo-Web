package br.unipar.sistemahospitalws.repositories;

import br.unipar.sistemahospitalws.domain.Paciente;
import br.unipar.sistemahospitalws.dto.PacienteInsertRequestDTO;
import br.unipar.sistemahospitalws.dto.PacienteUpdateRequestDTO;
import br.unipar.sistemahospitalws.infraestructure.ConnectionFactory;
import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PacienteRepository {

    private static final String INSERT_PACIENTE =
            "INSERT INTO paciente (nome, email, telefone, cpf, ativo) " +
                    "VALUES (?, ?, ?, ?, true) RETURNING id";

    private static final String INSERT_ENDERECO =
            "INSERT INTO endereco (paciente_id, logradouro, numero, complemento, bairro, cidade, uf, cep) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_PACIENTE =
            "UPDATE paciente SET nome = ?, telefone = ? WHERE id = ? AND ativo = true";

    private static final String UPDATE_ENDERECO =
            "UPDATE endereco SET logradouro = ?, numero = ?, complemento = ?, bairro = ?, cidade = ?, uf = ?, cep = ? " +
                    "WHERE paciente_id = ?";

    private static final String DELETE =
            "UPDATE paciente SET ativo = false WHERE id = ?";

    private static final String SELECT_ALL =
            "SELECT id, nome, email, cpf FROM paciente WHERE ativo = true ORDER BY nome ASC";

    public Paciente inserir(PacienteInsertRequestDTO dto) throws SQLException, NamingException {
        Connection conn = null;
        PreparedStatement pstmtPaciente = null;
        PreparedStatement pstmtEndereco = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            conn.setAutoCommit(false);

            pstmtPaciente = conn.prepareStatement(INSERT_PACIENTE, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtPaciente.setString(1, dto.getNome());
            pstmtPaciente.setString(2, dto.getEmail());
            pstmtPaciente.setString(3, dto.getTelefone());
            pstmtPaciente.setString(4, dto.getCpf());
            pstmtPaciente.executeUpdate();

            rs = pstmtPaciente.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("Erro ao obter ID do paciente");
            }
            int pacienteId = rs.getInt(1);

            pstmtEndereco = conn.prepareStatement(INSERT_ENDERECO);
            pstmtEndereco.setInt(1, pacienteId);
            pstmtEndereco.setString(2, dto.getEndereco().getLogradouro());
            pstmtEndereco.setString(3, dto.getEndereco().getNumero());
            pstmtEndereco.setString(4, dto.getEndereco().getComplemento());
            pstmtEndereco.setString(5, dto.getEndereco().getBairro());
            pstmtEndereco.setString(6, dto.getEndereco().getCidade());
            pstmtEndereco.setString(7, dto.getEndereco().getUf());
            pstmtEndereco.setLong(8, dto.getEndereco().getCep());
            pstmtEndereco.executeUpdate();

            conn.commit();

            Paciente paciente = new Paciente();
            paciente.setId(pacienteId);
            paciente.setNome(dto.getNome());
            paciente.setEmail(dto.getEmail());
            paciente.setTelefone(dto.getTelefone());
            paciente.setCpf(dto.getCpf());
            paciente.setEndereco(dto.getEndereco());

            return paciente;

        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (pstmtPaciente != null) pstmtPaciente.close();
            if (pstmtEndereco != null) pstmtEndereco.close();
            if (conn != null) conn.close();
        }
    }

    public PacienteUpdateRequestDTO editar(PacienteUpdateRequestDTO dto) throws SQLException, NamingException {
        Connection conn = null;
        PreparedStatement pstmtPaciente = null;
        PreparedStatement pstmtEndereco = null;

        try {
            conn = new ConnectionFactory().getConnection();
            conn.setAutoCommit(false);

            pstmtPaciente = conn.prepareStatement(UPDATE_PACIENTE);
            pstmtPaciente.setString(1, dto.getNome());
            pstmtPaciente.setString(2, dto.getTelefone());
            pstmtPaciente.setInt(3, dto.getId());
            pstmtPaciente.executeUpdate();

            pstmtEndereco = conn.prepareStatement(UPDATE_ENDERECO);
            pstmtEndereco.setString(1, dto.getEndereco().getLogradouro());
            pstmtEndereco.setString(2, dto.getEndereco().getNumero());
            pstmtEndereco.setString(3, dto.getEndereco().getComplemento());
            pstmtEndereco.setString(4, dto.getEndereco().getBairro());
            pstmtEndereco.setString(5, dto.getEndereco().getCidade());
            pstmtEndereco.setString(6, dto.getEndereco().getUf());
            pstmtEndereco.setLong(7, dto.getEndereco().getCep());
            pstmtEndereco.setInt(8, dto.getId());
            pstmtEndereco.executeUpdate();

            conn.commit();
            return dto;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (pstmtPaciente != null) pstmtPaciente.close();
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
                throw new SQLException("Nenhum paciente encontrado com ID: " + id);
            }

        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public List<Paciente> listar() throws SQLException, NamingException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Paciente> pacientes = new ArrayList<>();

        try {
            conn = new ConnectionFactory().getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getInt("id"));
                paciente.setNome(rs.getString("nome"));
                paciente.setEmail(rs.getString("email"));
                paciente.setCpf(rs.getString("cpf"));
                pacientes.add(paciente);
            }

            return pacientes;

        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public Paciente buscarPorId(Integer id) throws SQLException, NamingException {
        String sql = "SELECT * FROM paciente WHERE id = ? AND ativo = true";

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getInt("id"));
                paciente.setNome(rs.getString("nome"));
                paciente.setAtivo(rs.getBoolean("ativo"));
                // Preencher outros campos se necessário
                return paciente;
            }
            return null;
        }
    }
}
