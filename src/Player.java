import java.util.Random;

abstract class Player extends Entity {
    // Função: atribuir o aspecto de jogabilidade ao jogador
    protected AbilityHandler abilityHandler;

    public Player(String nome, Arma armaAtual) {
        super(nome, armaAtual);
    }

    abstract class AbilityHandler{
        // Função: lidar com a habilidade da classe

        protected int cooldownHabilidadeAtual;
        protected static int cooldownHabilidade;
        protected abstract boolean usarHabilidade(boolean estado);
        public abstract boolean tickCooldownHabilidade();
    }

    public boolean defineAction(Entity oponente){
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
                return actionHandler.atacar(oponente);
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
    }


    // Fúria: habilidade ativa da Classe
    AbilityHandler abilityHandler = new AbilityHandler();
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
                cooldownHabilidade = 1;
                statusHabilidade = statusHandler.addStatus("StatusFuria");
                statusHandler.printStatusMessage(statusHabilidade, "inicio-efeito");
            } else if (!estado){ // false indica que a habilidade está sendo desativada
                statusHandler.printStatusMessage(statusHabilidade, "fim-efeito");
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

class Ladino extends Player {
    // Atributos da Classe
    public Ladino(String nome, Arma armaAtual) {
        super(nome, armaAtual);
        this.vidaMaxima = 300;
        this.vidaAtual = vidaMaxima;
        this.baseAtk = 175;
    }


    // Evasão: habilidade ativa da Classe
    AbilityHandler abilityHandler = new AbilityHandler();
    class AbilityHandler{
        protected int cooldownHabilidadeAtual = 0;
        protected static int cooldownHabilidade = 3;

        protected Status statusHabilidade = null;
        public boolean usarHabilidade(boolean estado) {
            if (estado){ // true indica que a habilidade está sendo ativada
                if (cooldownHabilidade > 0) { // Se o cooldown estiver ativo
                    System.out.println("A habilidade falhou!");
                    return false;
                }
                cooldownHabilidade = 3;
                statusHabilidade = statusHandler.addStatus("StatusEvasao");
                statusHandler.printStatusMessage(statusHabilidade, "inicio-efeito");
            } else if (!estado){ // false indica que a habilidade está sendo desativada
                statusHandler.printStatusMessage(statusHabilidade, "fim-efeito");
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
    }


    // Grimório: habilidade ativa da Classe
    AbilityHandler abilityHandler = new AbilityHandler();
    class AbilityHandler{
        static Random rng = new Random(System.currentTimeMillis());

        protected int cooldownHabilidadeAtual = 0;
        protected static int cooldownHabilidade = 3;

        protected Status statusHabilidade = null;
        public boolean usarHabilidade(Boolean estado) {
            if (cooldownHabilidade > 0) {
                System.out.println("A habilidade falhou!");
            } else {
                cooldownHabilidade = 2;
                System.out.println("[Grimório] " + nome + " prepara uma magia poderosa...");

                int action = 0;
                action = rng.nextInt(100);

                // Cura: 33%
                // Dano: 33%
                // Backfire: 33%

                if (action >= 66){
                    System.out.println("[Grimório] " + nome + " dispara uma bola de fogo!");
                    return GameHandler.adversario.healthHandler.handleDanoRecebido(damageHandler.handleDanoAtual(baseAtk, false));
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


