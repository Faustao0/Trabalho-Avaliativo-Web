package br.unipar.sistemahospitalws.domain;

import br.unipar.sistemahospitalws.dto.MedicoInsertRequestDTO;
import br.unipar.sistemahospitalws.enums.Especialidade;

public class Medico extends Pessoa{

    private Integer id;
    private Long crm;
    private Especialidade especialidade;
    private Boolean ativo;

    public Medico(int medicoId, MedicoInsertRequestDTO dto) {
        super();
    }

    public Medico() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Long getCrm() {
        return crm;
    }

    public void setCrm(Long crm) {
        this.crm = crm;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
