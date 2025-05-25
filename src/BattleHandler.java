import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class BattleHandler {
    // Função: Manejar todos os aspectos do sistema de combate
    static Random rng = new Random(System.currentTimeMillis());
    static Scanner input = new Scanner(System.in).useDelimiter("\n");

    public static Player jogador = Game.jogador;
    public static ArrayList<Entity> grupoJogador = new ArrayList<>(Arrays.asList(jogador));
    public static ArrayList<Entity> grupoAdversarios = new ArrayList<>();

    public static Entity alvo;

    protected static ArrayList<Entity> ordemTurno = new ArrayList<>(); // Lista de todas as entidades no combate, controla ordem de turno da batalha
    protected static ArrayList<Entity> entidadesDerrotadas = new ArrayList<>();

    private static int salaAtual = Game.getSalaAtual(); // Sala atual da masmorra
    private static int rodadaAtual = 1; // Rodada atual da batalha
    private static Boolean emBatalha = true; // Estado de combate

    public static void gerarSala(){
        // Limpa a ordem de turno pré-existente, adicionando apenas o jogador
        ordemTurno.clear();
        ordemTurno.add(jogador);

        // Gera um boss a cada 5 salas
        if (salaAtual % 1 == 0){
            grupoAdversarios.add(Boss.SpawnManager.generateMob(null, salaAtual));
        }

        // Geração de quantia aleatória de inimigos
        int genRNG = rng.nextInt(100);
        int genQTD = 1; // Quantidade de inimigos gerada (default = 1)

        if (genRNG > 50){ // 1 inimigo: 50% (100 - 50)
            genQTD = 1;
        } else if (genRNG > 20) { // 2 inimigos: 30% (50 - 20)
            genQTD = 2;
        } else if (genRNG > 5) { // 3 inimigos: 15% (20 - 5)
            genQTD = 3;
        } else if (genRNG > 1) { // 4 inimigos: 4% (5 - 1)
            genQTD = 4;
        } else if (genRNG <= 1) { // 5 inimigos: 1% (1)
            genQTD = 5;
        }

        for (int i = 1;i <= genQTD; i++){
            grupoAdversarios.add(Mob.SpawnManager.generateMob(null, salaAtual));
        }

        // Adiciona os adversários recém-gerados à ordem de turno
        ordemTurno.addAll(grupoAdversarios);

        emBatalha = true; // Inicia estado de combate
    }

    // Seleção de lado de oponente
    public static ArrayList<Entity> getOpposingTeam(Entity entity){
        if (entity.getSide().equals(Entity.Side.PLAYER) ){
            return grupoAdversarios;
        } else if (entity.getSide().equals(Entity.Side.ENEMY)){
            return grupoJogador;
        }

        return null;
    }

    // Seleção de alvo
    public static Entity selecaoAlvo(ArrayList<? extends Entity> oponentes, boolean random){
        if (!random){
            System.out.println("SELECIONE UM ALVO");
            int i = 1; // Contagem de iteração
            for (Entity oponente : oponentes){
                System.out.println(i + " -> " + oponente);
                i++;
            }
            System.out.println("ou insira 0 para retornar");

            // Validação de escolha de alvo
            int escolha = 0;
            boolean escolhaValida = false;
            while (!escolhaValida){
                try{
                    escolha = Integer.parseInt(input.next());
                    if (escolha >= 0 && escolha <= oponentes.size()){
                        escolhaValida = true;
                    } else {
                        System.out.println("Opção inválida.");
                    }
                } catch (NumberFormatException error){
                    System.out.println("Opção inválida.");
                }
            }

            alvo = oponentes.get(escolha - 1); // Retorna -1 para sair, 0 em diante para escolha específica
        } else if (random){
            Random rng = new Random(System.currentTimeMillis());
            if (oponentes.size() > 1){
                alvo = oponentes.get(rng.nextInt(0, (oponentes.size() - 1)));
            } else {
                alvo = oponentes.get(0);
            }

        }

        return alvo;
    }

    // Exibição de cenário de entidade * EXTRAIR
    public static void displayEntityScenario(Entity... entities){
        ArrayList<String> entityScenarios = new ArrayList<>();
        for (Entity entity : entities){
            try {
                System.out.println(entity.nome + " | " + entity.vidaAtual + " / " + entity.vidaMaxima + "HP " + entity.statusList + " | " + entity.baseAtk + " | " + entity.armaAtual.getNome() + " (" + entity.armaAtual.getRaridade() + "): " + entity.armaAtual.getAtkExtra() + " ATK ");

            } catch (NullPointerException error){
                System.out.println(entity.nome + " | " + entity.vidaAtual + " / " + entity.vidaMaxima + "HP " + entity.statusList + " | " + entity.baseAtk + " ATK ");
            }
        }
    }

    // Turno do combate
    public static void realizarTurno(Entity actor){
        // Reseta o estado de defesa da entidade
        actor.defesa = false;

        // Tick no cooldown de habilidades (se aplicável)


        // Ação do ator
        if (!actor.actionHandler.checkForIdle()){ // Se a entidade não estiver inativo (ou seja, retornar false)
            actor.defineAction(); // Realizar ação. Se o oponente morrer, retorna true.
        }

        actor.statusHandler.tickStatus();

        // Tick de habilidade (se possível)
        actor.tickSpecial("abilityHandler");
    }

    // Organização de turnos

    public static void battleLoop(){
        // Exibe a numeração de rodada atual
        System.out.println("Rodada: " + BattleHandler.rodadaAtual);
        System.out.println();

        while (jogador.estaVivo() && ordemTurno.size() > 1){
            for(Entity entity : ordemTurno){
                if (jogador.estaVivo()) {
                    if (entity.estaVivo()){
                        if(entity.equals(jogador)){
                            displayEntityScenario(ordemTurno.toArray(new Entity[0]));
                        }
                        System.out.println("Turno: " + entity);
                        System.out.println();

                        if (entity != jogador){
                            realizarTurno(entity);
                        } else {
                            realizarTurno(entity);
                        }

                    }
                } else {
                    break;
                }
            }

            BattleHandler.grupoAdversarios.removeAll(BattleHandler.entidadesDerrotadas);
            BattleHandler.ordemTurno.removeAll(BattleHandler.entidadesDerrotadas);
        }

    }
}
