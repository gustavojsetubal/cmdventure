import java.util.Map;
import java.util.Random;

public abstract class Enemy extends Entity {
    // Função: tratar da IA de um inimigo

    static Random rng = new Random(System.currentTimeMillis());
    public Enemy(String nome, Arma armaAtual) {
        super(nome, armaAtual);
    }

    // Banco de ações e suas chances
    Map<String, Double> actionBank = Map.ofEntries(
            Map.entry("atacar", 0.6),
            Map.entry("defender", 0.3),
            Map.entry("curar", 0.1)
    );

    // Gera ação aleatória dentre disponíveis
    protected String generateAction() {
        // Valor total de pesos (não precisa resultar em 1)
        float totalPool = 0;
        for (Double actionChance : this.actionBank.values()){
            totalPool += actionChance;
        }

        // Sorteia ação dentre banco
        double r = rng.nextDouble(0, totalPool);
        for (Map.Entry<String, Double> action : actionBank.entrySet()){
            if (r < action.getValue()){
                return action.getKey();
            }
            r -= action.getValue();
        }
        return "atacar";
    }

    abstract boolean defineAction(Entity oponente);
}


class Mob extends Enemy {
    // Função: delegar atributos e capacidades de inimigos normais

    // Construtor simples, sem atributos
    public Mob(String nome, Arma armaAtual, int vidaMaxima, int atkBase) {
        super(nome, armaAtual);
        this.vidaMaxima = vidaMaxima;
        this.vidaAtual = vidaMaxima;
        this.baseAtk = atkBase;
        this.defesa = false;
    }

    // Seleciona ação com base no valor gerado aleatóriamente
    public boolean defineAction(Entity oponente){
        switch(generateAction()) {
            case "atacar":
                return actionHandler.atacar(oponente);

            case "defender":
                return actionHandler.setDefesa(true);

            case "curar":
                return actionHandler.curar();
        }
        return actionHandler.atacar(oponente);
    }

    static class SpawnManager {
        // Função: lidar com a geração de inimigos normais

        // Valores de vida e ataque gerados aleatóriamente
        static int genVidaMaxima;
        static int genAtkBase;

        // Gera multiplicador de atributos aleatório
        static float genMultiplier(){
            return (float) rng.nextDouble(0.1);
        }

        // Cria um mob com nome e atributos aleatórios
        public static Mob generateMob(Arma armaAtual, int salaAtual) {
            genVidaMaxima = (int) Math.round(1000 * (genMultiplier() * salaAtual));
            genAtkBase = (int) Math.round(750 * (genMultiplier() * salaAtual));

            return new Mob(NameHandler.generateMonster(), null, genVidaMaxima, genAtkBase);
        }
    }
}


class Boss extends Enemy {
    // Função: delegar atributos e capacidades de inimigos normais
    protected AbilityHandler abilityHandler;

    class AbilityHandler{
        protected int cooldownHabilidadeAtual = 0;
        protected static int cooldownHabilidade = 1;

        protected Status statusHabilidade = null;
        public boolean usarHabilidade(boolean estado) {
            if (estado){ // true indica que a habilidade está sendo ativada
                if (cooldownHabilidade > 0) { // Se o cooldown estiver ativo
                    System.out.println("A habilidade falhou!");
                    return false;
                }
                cooldownHabilidade = 4;
                statusHabilidade = statusHandler.addStatus("StatusFrenesi");
                statusHandler.printStatusMessage(statusHabilidade, "inicio-efeito");
            } else if (!estado){ // false indica que a habilidade está sendo desativada
                statusHandler.printStatusMessage(statusHabilidade, "fim-efeito");
                statusHabilidade = null;
                return GameHandler.jogador.healthHandler.handleDanoRecebido(damageHandler.handleDanoAtual(baseAtk * 2, false));
            }
            return false;
        }

        public boolean tickCooldownHabilidade(){
            cooldownHabilidadeAtual--;
            if (cooldownHabilidadeAtual <= 0){
                cooldownHabilidadeAtual = 0;
                return true; // Retorna true quando habilidade está disponível
            }
            return false; // Retorna falso quando habilidade continua indisponível
        }

    }

    // Alteração no banco de ação
    Map<String, Double> actionBank = Map.ofEntries(
            Map.entry("atacar", 0.5),
            Map.entry("defender", 0.3),
            Map.entry("curar", 0.1),
            Map.entry("habilidade", 0.1)
    );


    // Construtor simples, sem atributos
    public Boss(String nome, Arma armaAtual, int vidaMaxima, int atkBase) {
        super(nome, armaAtual);
        this.vidaMaxima = vidaMaxima;
        this.vidaAtual = vidaMaxima;
        this.baseAtk = atkBase;
        this.defesa = false;
    }

    // Seleciona ação com base no valor gerado aleatóriamente
    public boolean defineAction(Entity oponente){
        switch(generateAction()) {
            case "atacar":
                return actionHandler.atacar(oponente);

            case "defender":
                return actionHandler.setDefesa(true);

            case "curar":
                return actionHandler.curar();

            case "habilidade":
                return abilityHandler.usarHabilidade(true);
        }
        return actionHandler.atacar(oponente);
    }

    static class SpawnManager {
        // Função: lidar com a geração de inimigos boss

        // Valores de vida e ataque gerados aleatóriamente
        static int genVidaMaxima;
        static int genAtkBase;

        // Gera multiplicador de atributos aleatório
        static float genMultiplier(){
            return (float) rng.nextDouble(0.1);
        }

        // Cria um boss com nome e atributos aleatórios
        public static Boss generateMob(Arma armaAtual, int salaAtual) {
            genVidaMaxima = (int) Math.round(1000 * (genMultiplier() * salaAtual) * 2);
            genAtkBase = (int) Math.round(750 * (genMultiplier() * salaAtual) * 1.25);

            return new Boss(NameHandler.generateMonster() + " [BOSS]", null, genVidaMaxima, genAtkBase);
        }
    }
}
