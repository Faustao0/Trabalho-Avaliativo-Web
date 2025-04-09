package br.unipar.sistemahospitalws.enums;

public enum Especialidade {

    ORTOPEDIA("Ortopedia"),
    CARDIOLOGIA("Cardiologia"),
    GINECOLOGIA("Ginecologia"),
    DERMATOLOGIA("Dermatologia");

    private final String descricao;

    Especialidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static Especialidade fromDescricao(String descricao) {
        for (Especialidade e : values()) {
            if (e.descricao.equalsIgnoreCase(descricao)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Especialidade inválida: " + descricao);
    }
}