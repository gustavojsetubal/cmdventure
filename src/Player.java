import java.util.Random;

abstract class Player extends Entity {
    // Função: atribuir o aspecto de jogabilidade ao jogador
    public AbilityHandler abilityHandler;

    public Player(String nome, Arma armaAtual) {
        super(nome, armaAtual);
        equipe = Side.PLAYER;
    }

    // Helper: Seleção de alvo
    Entity promptTarget(){
        return BattleHandler.selecaoAlvo(BattleHandler.grupoAdversarios, false);
    }

    // Helper: Ação Especial
    @Override
    void tickSpecial(String tipo) {
        if (tipo.equals("abilityHandler")){
            abilityHandler.tickCooldownHabilidade();
        }
    }

    public boolean defineAction(){
        System.out.println("Escolha sua ação:");

        // Seleção de ação
        int escolha = 0;
        Boolean escolhaValida = false;
        while (!escolhaValida){
            System.out.println("1) Atacar");
            System.out.println("2) Defender");
            System.out.println("3) Curar");
            System.out.println("4) Habilidade");
            try{
                escolha = Integer.parseInt(input.next());
                if (escolha == 1 || escolha == 2 || escolha == 3 || escolha == 4){
                    escolhaValida = true;
                } else {
                    System.out.println("Opção inválida.");
                }
            } catch (NumberFormatException error){
                System.out.println("Opção inválida.");
            }
        }

        System.out.println("\n\n\n");
        switch (escolha){
            case 1:
                return actionHandler.atacar(promptTarget());
            case 2:
                return actionHandler.setDefesa(true);
            case 3:
                return actionHandler.curar();
            case 4:
                return abilityHandler.usarHabilidade(true);
            default:
                System.out.println("Opção inválida.");
        }

        return false;
    }
}

// Classes
class Guerreiro extends Player {
    // Atributos da Classe
    public Guerreiro(String nome, Arma armaAtual) {
        super(nome, armaAtual);
        this.vidaMaxima = 500;
        this.vidaAtual = vidaMaxima;
        this.baseAtk = 125;
        this.abilityHandler = new AbilityHandler();
    }

    // Fúria: habilidade ativa da Classe
    class AbilityHandler implements Entity.AbilityHandler {
        protected int cooldownHabilidadeAtual = 0;
        protected static int cooldownHabilidade = 2;

        protected Status statusHabilidade = null;
        public boolean usarHabilidade(boolean estado) {
            if (estado){ // true indica que a habilidade está sendo ativada
                if (cooldownHabilidadeAtual > 0) { // Se o cooldown estiver ativo
                    System.out.println("A habilidade falhou!");
                    return false;
                }
                cooldownHabilidadeAtual = cooldownHabilidade + 1; // Soma-se 1 para remover o turno de uso da habilidade da equação
                statusHabilidade = statusHandler.addStatus("StatusFuria");

            } else if (!estado){ // false indica que a habilidade está sendo desativada
                statusHabilidade = null;
            }
            return false; // Sempre retorna false pois não pode matar oponentes
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

}

class Ladino extends Player {
    // Atributos da Classe
    public Ladino(String nome, Arma armaAtual) {
        super(nome, armaAtual);
        this.vidaMaxima = 300;
        this.vidaAtual = vidaMaxima;
        this.baseAtk = 175;
        this.abilityHandler = new AbilityHandler();
    }

    // Evasão: habilidade ativa da Classe
    class AbilityHandler implements Entity.AbilityHandler {
        protected int cooldownHabilidadeAtual = 0;
        protected static int cooldownHabilidade = 1;

        protected Status statusHabilidade = null;
        public boolean usarHabilidade(boolean estado) {
            if (estado){ // true indica que a habilidade está sendo ativada
                if (cooldownHabilidadeAtual > 0) { // Se o cooldown estiver ativo
                    System.out.println("A habilidade falhou!");
                    return false;
                }
                cooldownHabilidadeAtual = cooldownHabilidade + 1; // Soma-se 1 para remover o turno de uso da habilidade da equação
                statusHabilidade = statusHandler.addStatus("StatusEvasao");
            } else if (!estado){ // false indica que a habilidade está sendo desativada
                statusHabilidade = null;
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

}

class Mago extends Player {
    // Atributos da Classe
    public Mago(String nome, Arma armaAtual) {
        super(nome, armaAtual);
        this.vidaMaxima = 200;
        this.vidaAtual = vidaMaxima;
        this.baseAtk = 250;
        this.abilityHandler = new AbilityHandler();
    }

    // Grimório: habilidade ativa da Classe
    class AbilityHandler implements Entity.AbilityHandler {
        static Random rng = new Random(System.currentTimeMillis());

        protected int cooldownHabilidadeAtual = 0;
        protected static int cooldownHabilidade = 2;

        protected Status statusHabilidade = null;
        public boolean usarHabilidade(boolean estado) {
            if (cooldownHabilidadeAtual > 0) {
                System.out.println("A habilidade falhou!");
            } else {
                cooldownHabilidadeAtual = cooldownHabilidade + 1; // Soma-se 1 para remover o turno de uso da habilidade da equação
                System.out.println("[Grimório] " + nome + " prepara uma magia poderosa...");

                int action = 0;
                action = rng.nextInt(100);

                // Cura: 33%
                // Dano: 33%
                // Backfire: 33%

                if (action >= 66){
                    System.out.println("[Grimório] " + nome + " dispara uma bola de fogo!");
                    return promptTarget().healthHandler.handleDanoRecebido(damageHandler.handleDanoAtual(baseAtk, false));
                } else if (action >= 33){
                    System.out.println("[Grimório] " + nome + " cura suas feridas!");
                    healthHandler.handleCura((int) ((int) vidaMaxima * 0.5));
                } else if (action < 33){
                    System.out.println("[Grimório] " + nome + " errou o feitiço!");
                    healthHandler.handleDanoRecebido((int) (vidaMaxima * 0.2));
                }
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

}


