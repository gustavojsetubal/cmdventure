import java.util.*;

abstract class Status {
    // Função: delegar atributos e ações especiais de um efeito de status.

    protected Status self = this;
    protected String nome; // Nome do status
    private int duracao; // Duração do status, caso aplicável
    private double shiftVida; // Alteração aditiva por turno à vida causada pelo status todo: Shift Vida tem que aplicar
    private double modifierAtk; // Alteração multiplicativa de ataque causado pelo status
    private double modifierRes; // Alteração multiplicativa de resistência causado pelo status
    private int turnosDecorridos = -1; // Turnos desde que status foi aplicado. Começa em -1 pra normalizar o valor após o turno finalizar
    private boolean causaImobilizacao; // Define se o status imobiliza a entidade
    private Map<String, String> textoStatus = new HashMap<>(); // Define as mensagens utilizadas pelo status
    protected ArrayList<String> triggers; // Define os atributos especiais do status

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

    public abstract boolean statusTrigger(Entity user, String trigger);

    public Map<String, String> getTextoStatus() {
        return textoStatus;
    }

    public ArrayList<String> getTriggers() {
        return triggers;
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
    @Override
    public String toString() {
        return "Fúria";
    }

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
        // Status deve aplicar shiftVida após [inicio-efeito]
        triggers = new ArrayList<>();
        triggers.add("TriggerOnStart");
    }

    public boolean statusTrigger(Entity user, String trigger){
        if (trigger.equals("TriggerOnStart")){
            // Causa dano no receptor do status (shiftVida negativo)
            return (user.healthHandler.handleDanoRecebido((int) -((self.getShiftVida()) * user.vidaAtual)));
        }
        return false;
    }
}

// Evasão: Entidade desvia de 1 ataque
class StatusEvasao extends Status{
    @Override
    public String toString() {
        return "Evasão";
    }

    public StatusEvasao() {
        super(
                "Evasão",
                2,
                0,
                0,
                1,
                false,
                "inicio-efeito || [+Evasão] %nome% se prepara pra desviar!",
                "fim-efeito || [-Evasão] %nome% abaixa a guarda...",
                "PROC-inicio-danoRecebido || [Evasão] %nome% se esquivou!"

        );
        // Status deve ser removido após [PROC]
        triggers = new ArrayList<>();
        triggers.add("RemoveAfterProc");
    }

    public boolean statusTrigger(Entity user, String trigger){
        /*if (trigger.equals("RemoveAfterProc")){
            // Status é passivo, não tem ativação direta para este trigger
            // Funcionamento em: [Entity.HealthHandler.handleDanoRecebido()]
        }*/
        return false;
    }
}

// Frenesi: Prepara um ataque que dá o dobro de dano em 1 turno
class StatusFrenesi extends Status{
    @Override
    public String toString() {
        return "Frenesi";
    }

    public StatusFrenesi() {
        super(
                "Frenesi",
                1,
                0,
                2,
                0,
                true,
                "inicio-efeito || [+Frenesi] %nome% começou a preparar um grande ataque...",
                "idle || [Frenesi] %nome% está se preparando para atacar...",
                "fim-efeito || [-Frenesi] " + "A fúria de %nome% te alcançou!"

        );

        // Status deve ativar após [fim-efeito]
        triggers = new ArrayList<>();
        triggers.add("TriggerOnEnd");
    }

    public boolean statusTrigger(Entity user, String trigger){
        if (trigger.equals("TriggerOnEnd")){
            // Realiza um ataque com o dobro de dano num alvo aleatório (pré-aplicado pelo próprio Status
            return user.actionHandler.atacar(BattleHandler.selecaoAlvo(BattleHandler.getOpposingTeam(user), true));
        }
        return false;
    }
}





