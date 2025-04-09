package br.unipar.sistemahospitalws.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ConsultaInsertRequestDTO {

    private Integer pacienteId;
    private Integer medicoId;
    private String dataHora;

    public Integer getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Integer pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Integer getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Integer medicoId) {
        this.medicoId = medicoId;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public LocalDateTime getDataHoraAsLocalDateTime() {
        try {
            return LocalDateTime.parse(dataHora); // ISO com T
        } catch (DateTimeParseException e1) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return LocalDateTime.parse(dataHora, formatter);
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("Formato de dataHora inválido. Use 'yyyy-MM-ddTHH:mm' ou 'yyyy-MM-dd HH:mm'");
            }
        }
    }
}