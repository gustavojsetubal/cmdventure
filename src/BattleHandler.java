import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class BattleHandler {
    // Função: Manejar todos os aspectos do sistema de combate
    static Random rng = new Random(System.currentTimeMillis());
    static Scanner input = new Scanner(System.in).useDelimiter("\n");

    public static Player jogador = Game.jogador;
    public static Entity alvo;
    public static ArrayList<Enemy> adversarios = new ArrayList<>();

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
        if (salaAtual % 5 == 0){
            adversarios.add(Boss.SpawnManager.generateMob(null, salaAtual));
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
            adversarios.add(Mob.SpawnManager.generateMob(null, salaAtual));
        }

        // Adiciona os adversários recém-gerados à ordem de turno
        ordemTurno.addAll(adversarios);

        emBatalha = true; // Inicia estado de combate
    }

    // Seleção de alvo
    public static Entity selecaoAlvo(ArrayList<? extends Entity> oponentes){
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

        alvo = oponentes.get(escolha - 1);
        return oponentes.get(escolha - 1); // Retorna -1 pra sair, 0 em diante pra escolha específica
    }

    // Exibição de cenário de entidade * EXTRAIR
    public static String displayEntityScenario(Entity... entities){
        for (Entity entity : entities){
            try {
                return entity.nome + " | " + entity.vidaAtual + " / " + entity.vidaMaxima + "HP " + entity.statusList + " | " + entity.baseAtk + " | " + entity.armaAtual.getNome() + " (" + entity.armaAtual.getRaridade() + "): " + entity.armaAtual.getAtkExtra() + " ATK ";

            } catch (NullPointerException error){
                return entity.nome + " | " + entity.vidaAtual + " / " + entity.vidaMaxima + "HP " + entity.statusList + " | " + entity.baseAtk + " ATK ";
            }
        }
        return "";
    }

    public enum Side {
        PLAYER, ENEMY
    }

    // Turno do combate
    public static void realizarTurno(Entity actor, Side target){
        // Reseta o estado de defesa da entidade
        actor.defesa = false;

        // Tick no cooldown de habilidades (se aplicável)


        // Ação do ator
        if (!actor.actionHandler.checkForIdle()){ // Se a entidade não estiver inativo (ou seja, retornar false)
            /*if (target == Side.PLAYER){ //
                actor.defineAction();
            } else if (target == Side.ENEMY){
                actor.defineAction();
            }*/

            actor.defineAction(); // Realizar ação. Se o oponente morrer, retorna true.
        }

        actor.statusHandler.tickStatus();
    }

    // Organização de turnos
    public static void battleLoop(){
        // Exibe a numeração de rodada atual
        System.out.println("Rodada: " + BattleHandler.rodadaAtual);
        System.out.println();
        System.out.println(displayEntityScenario(ordemTurno.toArray(new Entity[0])));

        while (jogador.estaVivo() && ordemTurno.size() > 1){
            for(Entity entity : ordemTurno){
                if (jogador.estaVivo()) {
                    if (entity.estaVivo()){
                        System.out.println("Turno: " + entity);
                        System.out.println();

                        if (entity != jogador){
                            realizarTurno(entity, Side.PLAYER);
                        } else {
                            realizarTurno(entity, Side.ENEMY);
                        }
                    }
                } else {
                    break;
                }
            }

            BattleHandler.adversarios.removeAll(BattleHandler.entidadesDerrotadas);
            BattleHandler.ordemTurno.removeAll(BattleHandler.entidadesDerrotadas);
        }

    }
}
