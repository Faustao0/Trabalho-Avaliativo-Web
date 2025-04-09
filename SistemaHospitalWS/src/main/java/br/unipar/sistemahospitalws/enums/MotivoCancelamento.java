package br.unipar.sistemahospitalws.enums;

public enum MotivoCancelamento {

    PACIENTE_DESISTIU("Paciente desistiu"),
    MEDICO_CANCELOU("Médico cancelou"),
    OUTROS("Outros");

    private final String descricao;

    MotivoCancelamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static MotivoCancelamento fromDescricao(String descricao) {
        for (MotivoCancelamento motivo : values()) {
            if (motivo.descricao.equalsIgnoreCase(descricao)) {
                return motivo;
            }
        }
        throw new IllegalArgumentException("Motivo de cancelamento inválido: " + descricao);
    }
}
