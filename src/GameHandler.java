import java.util.ArrayList;
import java.util.Scanner;

public class GameHandler {
    public static Player jogador;

    private static Turno turnoAtual;
    private static int salaAtual;
    boolean gameRunning = true;

    Scanner input = new Scanner(System.in).useDelimiter("\n");

    // Utilidades
    private void pressEnterToContinue()
    {
        System.out.println("\nPressione ENTER pra continuar...");
        try
        {
            input.next();
        }
        catch(Exception _)
        {}
    }

    // Construtor
    public GameHandler() {
        this.salaAtual = 1;
    }


    // Inicia o loop de jogo
    public void iniciarJogo() {
        System.out.println("-= cmdventure =- \n");
        System.out.println("-= Insira um nome =-");
        String playerName = input.next();
        System.out.println("-= Escolha sua classe =-");

        // Seleção de classe
        int escolha = 0;
        boolean escolhaValida = false;
        while (!escolhaValida){
            System.out.println("1) Guerreiro");
            System.out.println("2) Ladino");
            System.out.println("3) Mago");
            try{
                escolha = Integer.parseInt(input.next());
                if (escolha == 1 || escolha == 2 || escolha == 3){
                    escolhaValida = true;
                } else {
                    System.out.println("Opção inválida.");
                }
            } catch (NumberFormatException error){
                System.out.println("Opção inválida.");
            }
        }

        switch (escolha){
            case 1:
                jogador = new Guerreiro(playerName, null);
                break;
            case 2:
                jogador = new Ladino(playerName, null);
                break;
            case 3:
                jogador = new Mago(playerName, null);
                break;
            default:
                System.out.println("Opção inválida.");
        }

        startGameLoop();
    }


    public void startGameLoop(){
        boolean gameRunning = true;
        while (gameRunning == true){
            if (iniciarSala()){ // Inicia uma sala, recebe o resultado do combate
                // Caso true, recompensa o jogador e volta ao loop
                System.out.println("Voce derrotou a sala!");
                System.out.println("\nEscolha sua recompensa e vá pra próxima.");

                // Gerar loot
                ArrayList<Loot> lootGerado = LootHandler.generateLoot(salaAtual, 3);

                // Seleção de loot
                int i = 1; // Contagem de iteração
                for (Loot loot : lootGerado){
                    System.out.println(i + " -> " + loot);
                    i++;
                }

                // Validação de escolha
                int escolha = 0;
                boolean escolhaValida = false;
                while (!escolhaValida){
                    try{
                        escolha = Integer.parseInt(input.next());
                        if (escolha >= 0 && escolha <= lootGerado.size()){
                            escolhaValida = true;
                        } else {
                            System.out.println("Opção inválida.");
                        }
                    } catch (NumberFormatException error){
                        System.out.println("Opção inválida.");
                    }
                }

                // Tratamento da escolha
                Loot lootEscolhido = lootGerado.get(escolha - 1);
                switch (lootEscolhido.getTipo()){
                    case "Blessing":
                        LootHandler.BlessingHandler.applyBlessing(jogador, (LootHandler.BlessingHandler) lootEscolhido);
                        break;

                    case "Arma":
                        LootHandler.ArmaHandler.pickArma(jogador, (LootHandler.ArmaHandler) lootEscolhido);
                        break;

                    default:
                        System.out.println("[DEBUG] Ocorreu um erro no tratamento do loot escolhido");
                }

                // Próxima sala se inicia com o reset do loop while
                salaAtual += 1;

            } else {
                // Caso false, finalize o loop e encerre o jogo
                gameRunning = false;
                System.out.println("\n\n-= Fim de jogo =-");
                System.out.println("Você sobreviveu " + salaAtual + " salas dentro da masmorra.");
            }

        }
    }

    // Inicia uma sala e passa responsabilidade para BattleHandler
    public boolean iniciarSala(){
        // Gera a sala com seus inimigos
        BattleHandler.gerarSala();

        // Inicia o loop de combate, retorna resultado da batalha
        return (BattleHandler.battleLoop());
    }

    // Getters
    public static Turno getTurnoAtual() {
        return turnoAtual;
    }

    public static int getSalaAtual() {
        return salaAtual;
    }

}
