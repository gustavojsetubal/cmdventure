import java.util.Random;

public class Arma {
    static Random rng = new Random(System.currentTimeMillis());

    public final String nome;
    private final int atkExtra;
    private final int raridade; // 1 = comum, 2 = incomum, 3 = raro, 4 = épico, 5 = lendário

    public Arma(String nome, int atkExtra, int raridade) {
        this.nome = nome;
        this.atkExtra = atkExtra;
        this.raridade = raridade;
    }

    // Probabilidades de raridade
    // Comum: 50% (100 - 50)
    // Incomum: 30% (50 - 20)
    // Raro: 15% (20 - 5)
    // Épico: 4% (5 - 1)
    // Lendário: 1% (1)

    // Geração
    static int genAtkExtra;
    static int genRaridade;
    public static Arma gerarArma(int salaAtual) {
        int genRaridadeRng = rng.nextInt(100);
        if (genRaridadeRng > 50){
            genRaridade = 1;
        } else if (genRaridadeRng > 20) {
            genRaridade = 2;
        } else if (genRaridadeRng > 5) {
            genRaridade = 3;
        } else if (genRaridadeRng > 1) {
            genRaridade = 4;
        } else if (genRaridadeRng <= 1) {
            genRaridade = 5;
        }

        genAtkExtra = (int) Math.round(5 + (0.15 * rng.nextInt(salaAtual)) + (genRaridade * Game.jogador.baseAtk * 0.1));

        return new Arma(NameHandler.generateWeapon(), genAtkExtra, genRaridade);

    }

    // Getters
    public int getAtkExtra() {
        return atkExtra;

    }

    public String getRaridade(){
        if (raridade == 1) {
            return "Comum";
        } else if (raridade == 2){
            return "Incomum";
        } else if (raridade == 3){
            return "Raro";
        } else if (raridade == 4){
            return "Épico";
        } else {
            return "Lendário";
        }
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return nome + " (" + getRaridade() + ") | +" + getAtkExtra() + " dano";
    }
}
