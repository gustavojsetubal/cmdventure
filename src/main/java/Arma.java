import java.util.Map;

public class Arma {
    public final String nome;
    private final int atkExtra;
    private final String raridade; // 1 = comum, 2 = incomum, 3 = raro, 4 = épico, 5 = lendário
    private final int boostRaridade;

    public Arma(String nome, int atkExtra, String raridade, int boostRaridade) {
        this.nome = nome;
        this.atkExtra = atkExtra;
        this.raridade = raridade;
        this.boostRaridade = boostRaridade;
    }

    // Getters
    public int getAtkExtra() {
        return atkExtra;

    }

    public String getRaridade(){
        return raridade;
    }

    public int getBoostRaridade() {
        return boostRaridade;
    }

    public String getNome() {
        return nome;
    }
}
