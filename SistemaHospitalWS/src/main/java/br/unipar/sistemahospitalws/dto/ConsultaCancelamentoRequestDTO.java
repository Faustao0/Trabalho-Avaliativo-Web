package br.unipar.sistemahospitalws.dto;

import br.unipar.sistemahospitalws.enums.MotivoCancelamento;

public class ConsultaCancelamentoRequestDTO {

    private  Integer id;
    private MotivoCancelamento motivoCancelamento;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MotivoCancelamento getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(MotivoCancelamento motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }
}
