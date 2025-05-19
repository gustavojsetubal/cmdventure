import java.util.*;

abstract class Status {
    // Função: delegar atributos de um efeito de status.

    protected String nome; // Nome do status
    private int duracao; // Duração do status, caso aplicável
    private double shiftVida; // Alteração aditiva por turno à vida causada pelo status todo: Shift Vida tem que aplicar
    private double modifierAtk; // Alteração multiplicativa de ataque causado pelo status
    private double modifierRes; // Alteração multiplicativa de resistência causado pelo status
    private int turnosDecorridos = -1; // Turnos desde que status foi aplicado. Começa em -1 pra normalizar o valor após o turno finalizar
    private boolean causaImobilizacao; // Define se o status imobiliza a entidade
    private Map<String, String> textoStatus = new HashMap<>(); // Define as mensagens utilizadas pelo status

    private static ArrayList<String> statusBank = new ArrayList<>(Arrays.asList( // Lista dos nomes de classes de status disponíveis
            "StatusFuria",
            "StatusFrenesi",
            "StatusEvasao"
    ));

    // Construtores
    public Status(String nome, int duracao, double shiftVida, double modifierAtk, double modifierRes, boolean causaImobilizacao, String... textoStatus) {
        this.nome = nome;
        this.duracao = duracao;
        this.shiftVida = shiftVida;
        this.modifierAtk = modifierAtk;
        this.modifierRes = modifierRes;
        this.causaImobilizacao = causaImobilizacao;
        for (String status : textoStatus){
            String[] splitStatus = status.split(" \\|\\| ");
            this.textoStatus.put(
                    splitStatus[0], // Key
                    splitStatus[1]  // Value
            );
        }
    }

    // Getters
    public static ArrayList<String> getStatusBank() {
        return statusBank;
    }

    public Map<String, String> getTextoStatus() {
        return textoStatus;
    }

    public double getShiftVida() {
        return shiftVida;
    }

    public double getModifierAtk() {
        return modifierAtk;
    }

    public double getModifierRes() {
        return modifierRes;
    }

    public int getTempoRestante() {
        return duracao - turnosDecorridos;
    }

    public boolean causaImobilizacao() {
        return causaImobilizacao;
    }

    // Setters
    public void addTurnosDecorridos() {
        this.turnosDecorridos++;
    }

}

// TIPOS DE STATUS
// Fúria: toma 20% de dano, mas causa 2x dano no próximo ataque
class StatusFuria extends Status{
    public StatusFuria() {
        super(
                "Fúria",
                1,
                -0.2,
                2,
                0,
                false,
                "inicio-efeito || [+Fúria] %nome% se enfurece!",
                            "fim-efeito || [-Fúria] A raiva de %nome% se esvaiu..."

        );
    }
}

// Evasão: Entidade desvia de 1 ataque
class StatusEvasao extends Status{
    public StatusEvasao() {
        super(
                "Evasão",
                1,
                0,
                0,
                1,
                false,
                "inicio-efeito || [+Evasão] %nome% se prepara pra desviar!",
                "fim-efeito || [-Evasão] %nome% abaixa a guarda...",
                "PROC-inicio-danoRecebido || [+Evasão] %nome% se esquivou!"

        );
    }
}

// Frenesi: Prepara um ataque que dá o dobro de dano em 1 turno
class StatusFrenesi extends Status{
    public StatusFrenesi() {
        super(
                "Frenesi",
                1,
                0,
                0,
                0,
                true,
                "inicio-efeito || [+Frenesi] %nome% começou a preparar um grande ataque...",
                "idle || [Frenesi] %nome% está se preparando para atacar...",
                "fim-efeito || [-Frenesi] " + "A fúria de %nome% te alcançou!"

        );
    }
}





