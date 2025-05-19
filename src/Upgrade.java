import java.util.Random;

public class Upgrade {
    static Random rng = new Random(System.currentTimeMillis());

    private final String nome;
    private final int curaVida;
    private final int aumentoVida;
    private final int aumentoDano;

    public Upgrade(String nome, int aumentoVida, int aumentoDano, int curaVida) {
        this.nome = nome;
        this.aumentoVida = aumentoVida;
        this.aumentoDano = aumentoDano;
        this.curaVida = curaVida;
    }

    // Geração
    static int genAumentoVida;
    static int genAumentoDano;
    public static Upgrade gerarItem(int sala) {
        int tipo = rng.nextInt(4); // 0 = Vida, 1 = Dano, 2 = Cura
        int atributo = (int) (5 + rng.nextInt(20) * rng.nextInt(sala));

        if (tipo > 2) {
            atributo *= (int) (Game.jogador.vidaMaxima * 0.1);
            return new Upgrade(NameHandler.generateUpgrade(), 0 , 0, atributo);
        } else if (tipo > 1) {
            return new Upgrade(NameHandler.generateUpgrade(), 0 , atributo, 0);
        } else if (tipo > 0){
            return new Upgrade(NameHandler.generateUpgrade(), atributo , 0 , 0);
        }

        // Backup
        return new Upgrade(NameHandler.generateUpgrade(), atributo , 0 , 0);
    }

    public String mostrarUpgrade(){
        if (aumentoDano != 0){
            return " | +" + aumentoDano + " dano base";
        } else if (aumentoVida != 0){
            return " | +" + aumentoVida + " vida base";
        } else if (curaVida != 0){
            return " | +" + curaVida + " de regeneração";
        }

        return "Upgrade inválido.";
    }

    public void escolhaUpgrade (Entity jogador){
        jogador.baseAtk += aumentoDano;
        jogador.vidaMaxima += aumentoVida;
        jogador.vidaAtual += curaVida;
    }

    @Override
    public String toString() {
        return nome;
    }
}
