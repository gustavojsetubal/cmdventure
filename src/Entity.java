import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

abstract class Entity {
    // Função: Lidar com atributos da entidade

    protected HealthHandler healthHandler = new HealthHandler();
    protected DamageHandler damageHandler = new DamageHandler();
    protected ActionHandler actionHandler = new ActionHandler();
    protected StatusHandler statusHandler = new StatusHandler();

    protected String nome;
    protected int vidaAtual;
    protected int vidaMaxima;
    protected int baseAtk;
    protected boolean defesa = false;
    protected double baseDefesa = 0.5;
    protected Arma armaAtual = null;
    protected List<Status> statusList = new ArrayList<>();

    static Scanner input = new Scanner(System.in).useDelimiter("\n");

    // Construtores
    public Entity(String nome, Arma armaAtual) {
        this.nome = nome;
        // this.vidaMaxima = Herda de classe;
        // this.danoBase = Herda de classe;
        this.defesa = true;
        this.armaAtual = armaAtual;
    }


    // Exibição de cenário de entidade * EXTRAIR
    public void displayEntityScenario(){
        try {
            System.out.println(this.nome + " | " + this.vidaAtual + " / " + this.vidaMaxima + "HP " + this.statusList + " | " + this.baseAtk + " | " + this.armaAtual.getNome() + " (" + this.armaAtual.getRaridade() + "): " + this.armaAtual.getAtkExtra() + " ATK " );

        } catch (NullPointerException error){
            System.out.println(this.nome + " | " + this.vidaAtual + " / " + this.vidaMaxima + "HP " + this.statusList + " | " + this.baseAtk + " ATK " );
        }
    }

    // Seleção de ação
    abstract boolean defineAction(Entity oponente);

    // Classes auxiliares
    class HealthHandler{
        // Função: lidar com alterações na vida da entidade

        public Boolean handleDanoRecebido(int dmg){
            double totalModifierRes = 0; // Modificador de resistência total

            // Calcular modificador de resistência atual
            for (Status status : statusList){
                totalModifierRes += status.getModifierRes();
            }

            // Checar estado de defesa
            if (defesa){
                totalModifierRes += baseDefesa;
                System.out.println(nome + " se defendeu! Dano reduzido em " + baseDefesa * 100 + "%.");

                defesa = false; // Desativa defesa
            }

            // Limitar resistência a 100%
            if (totalModifierRes > 1){
                totalModifierRes = 1;
            }

            // [PROC-inicio-danoRecebido]
            for (Status status : statusList){
                statusHandler.printStatusMessage(status, "PROC-início-danoRecebido");
            }

            // Aplicar dano
            int danoCausado = (int) (dmg - (dmg * totalModifierRes));
            vidaAtual -= danoCausado;
            System.out.println(nome + " recebeu " + danoCausado + " de dano.");

            // Verificar estado da entidade
            if (vidaAtual <= 0) {
                System.out.println(nome + " foi derrotado!");
                return true; // Retorna true se estiver morto.
            } else {
                System.out.println(nome + " agora possui " + vidaAtual + " de vida.");
                return false; // Retorna false se estiver vivo.
            }
        }

        public void handleCura(int cura) {
            if (vidaAtual + cura > vidaMaxima) {
                vidaAtual = vidaMaxima;
            } else {
                vidaAtual += cura;
            }
            System.out.println(nome + " se curou!");
            System.out.println(nome + " agora possui " + vidaAtual + " de vida.");
        }
    }

    class DamageHandler{
        // Função: lidar com capacidade de causar dano da entidade

        public int handleDanoAtual(int flatAtk, boolean doesWeaponInfluence){
            int totalShiftAtk = 0; // Adição ao ataque atual, computado pré-multiplicadores
            double totalModifierAtk = 1; // Modificador multiplicativo de ataque total

            // Calcular aumentos aditivos ao ataque atual
            if (doesWeaponInfluence){ // Se a arma equipada fizer parte do ataque
                try { // Ataque-base da arma
                    totalShiftAtk += armaAtual.getAtkExtra();
                } catch (NullPointerException _){}
            }

            // Calcular modificador multiplicativo de ataque atual
            for (Status status : statusList){
                totalModifierAtk *= status.getModifierAtk();
            }

            // Retornar valor final de dano
            System.out.println(flatAtk);
            System.out.println((int) ((flatAtk + totalShiftAtk) * totalModifierAtk));
            return (int) ((flatAtk + totalShiftAtk) * totalModifierAtk);
        }
    }

    class ActionHandler{
        // Função: lidar com as ações da entidade

        // Checagem de incapacidade de agir
        public boolean checkForIdle(){
            for (Status status : statusList){
                if (status.causaImobilizacao()){
                    statusHandler.printStatusMessage(status, "idle");
                    return true; // Retorna true se algum dos status da entidade impede a ação
                }
            }
            return false; // Retorna false se a entidade não tiver impedimentos pra agir
        }

        // Ação: Atacar
        public boolean atacar(Entity adversario){
            // Recebe o dano base + dano de arma base da entidade
            int dmg = damageHandler.handleDanoAtual(baseAtk, true);

            // Exibe a mensagem de ataque com a arma usada (quando possível)
            try {
                System.out.println(nome + " ataca " + adversario.nome + " com " + armaAtual.getNome());
            } catch (NullPointerException e){
                System.out.println(nome + " ataca " + adversario.nome);
            }

            // Computa o dano causado no adversário
            return adversario.healthHandler.handleDanoRecebido(dmg); // handleDanoRecebido retornará true caso o adversário morra, false caso ele continue vivo

        }

        // Ação: Defender
        public boolean setDefesa(Boolean estado){
            // Determina estado de defesa do personagem
            defesa = estado;

            // Exibe mensagem de estado de defesa, quando aplicável
            if (defesa){
                System.out.println(nome + " se prepara pro impacto!");
            }
            return false; // Função sempre retorna falso já que não pode determinar fim de combate.
        }

        // Ação: Curar
        public boolean curar(){
            // Cura quantidade base de 15% da vida ao personagem
            healthHandler.handleCura((int) (vidaMaxima * 0.15));
            return false; // Função sempre retorna falso já que não pode determinar fim de combate.
        }

        // Ação: Habilidade (Implementado apenas em classes que as possuem)
        /* public boolean habilidade(Boolean estado){
            // Definido por classe
            return false;
        }*/

    }

    class StatusHandler {
        // Função: organizar adição, ticking e proccing de status.

        // Criação do status
        public Status addStatus(String statusName){
            try {
                Class<?> aClass = Class.forName(statusName);

                if (Status.class.isAssignableFrom(aClass)){
                    Status addedStatus = (Status) aClass.getDeclaredConstructor().newInstance();
                    statusList.add(addedStatus);
                    return addedStatus;
                } else {
                    throw new IllegalArgumentException("[DEBUG] A classe indicada não é subclasse de Status");
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Status não encontrado / inválido.");
                e.printStackTrace();
            }
            return null;
        }

        // Lidar com mensagens de status
        public void printStatusMessage(Status targetStatus, String statusKey){
            Map<String, String> textoStatus = targetStatus.getTextoStatus();
            if(textoStatus.get(statusKey) != null) {
                System.out.println((textoStatus.get(statusKey)).replace("%nome%", nome));
            } else {
                System.out.println("[DEBUG] Chave de mensagem tem valor nulo.");
            }
        }

        // Tick de atributo
        /*
        public List<Status> toRemove = new ArrayList<>();
        public void tickStatus(Status status){
            // Etapa de efeito
            int dmgStatus = 0;

            // REMOVER - REFERENTE A HABILIDADE
            if (!toRemove.isEmpty()){
                habilidade(false);
                listaAtributos.removeAll(toRemove);
                toRemove.clear();
            }

            listaAtributos.removeAll(toRemove);
            for (StatusHandler atributo : listaAtributos){
                dmgStatus += atributo.getDmgStatus();
                atributo.tickStatus(this);
            }
            if (dmgStatus > 0){
                handleDanoRecebido(dmgStatus);
            }

            // Etapa de remoção
            status.addTurnosDecorridos();
            if (status.getTempoRestante() == 0){
                entidade.toRemove.add(this);
            }
        }*/
    }


    // Getters
    public Boolean estaVivo(){
        return vidaAtual > 0;
    }

    // Get & Set: Arma atual
    public Arma getArmaAtual() {
        return armaAtual;
    }

    public void setArmaAtual(Arma armaAtual) {
        this.armaAtual = armaAtual;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}


