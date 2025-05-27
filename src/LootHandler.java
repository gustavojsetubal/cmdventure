import java.util.*;

// Interface helper: indica que todos os items que a implementam são considerados tipos de loot
interface Loot{
    String getTipo();
};

public class LootHandler {
    // Função: gerenciar a geração de loot no pós-batalha

    static Random rng = new Random(System.currentTimeMillis());

    // Pool para geração de Raridade
    static final Map<String, Double> lootPool = Map.ofEntries(
            Map.entry("Blessing", 0.8),
            Map.entry("Arma", 0.2)
    );

    // Geração de Loot aleatório
    static ArrayList<Loot> generateLoot(int salaAtual, int qtd){
        ArrayList<Loot> loot = new ArrayList<>();
        for(int i = 1; i <= qtd; i++){
            switch(generateFromPool(lootPool)){
                case "Blessing":
                    loot.add(BlessingHandler.generateBlessing(salaAtual, 1));
                    break;

                case "Arma":
                    loot.add(ArmaHandler.generateArma(salaAtual));
                    break;

                default:
                    System.out.println("[DEBUG] Ocorreu um erro em generateLoot");
            }
        }

        return loot;
    }

    // Pool para geração de Raridade
    static final Map<String, Double> rarityPool = Map.ofEntries(
            Map.entry("Comum", 0.5),
            Map.entry("Incomum", 0.30),
            Map.entry("Raro", 0.15),
            Map.entry("Épico", 0.04),
            Map.entry("Lendário", 0.01)
    );

    // Gera aleatório num pool de String
    protected static String generateFromPool(Map<String, Double> pool) {
        // Valor total de pesos (não precisa resultar em 1)
        float totalPool = 0;
        for (Double actionChance : pool.values()){
            totalPool += actionChance;
        }

        // Sorteia ação dentre banco
        double r = rng.nextDouble(0, totalPool);
        for (Map.Entry<String, Double> action : pool.entrySet()){
            if (r < action.getValue()){
                return action.getKey();
            }
            r -= action.getValue();
        }
        return null;
    }

    // Tipos de Loot
    public static class BlessingHandler implements Loot{
        // Função: delinear o funcionamento do tipo de loot "blessing"
        private final String nome;
        private final int shiftVida;
        private final int shiftVidaMaxima;
        private final int shiftDano;

        @Override
        public String getTipo() {
            return "Blessing";
        }

        static final Map<String, Double> blessingPool = Map.ofEntries(
                Map.entry("shiftVida", 0.33),
                Map.entry("shiftVidaMaxima", 0.33),
                Map.entry("shiftDano", 0.33)
        );

        public BlessingHandler(String nome, Map<String, Integer> atributos) {
            this.nome = nome;

            this.shiftVida = Objects.requireNonNullElse(atributos.get("shiftVida"), 0);
            this.shiftVidaMaxima = Objects.requireNonNullElse(atributos.get("shiftVidaMaxima"), 0);
            this.shiftDano = Objects.requireNonNullElse(atributos.get("shiftDano"), 0);

        }

        public static BlessingHandler generateBlessing(int salaAtual, int rolls){
            Map<String, Integer> atributos = new HashMap<>();
            for (int i = 1; i <= rolls; i++){
                int valueRoll = (int) (5 + rng.nextInt(20) * rng.nextInt(salaAtual)); // todo: balancear
                String statRoll = generateFromPool(blessingPool);
                atributos.put(statRoll, valueRoll);
            }

            return new BlessingHandler(NameHandler.generateBlessing(), atributos);
        }

        public static void applyBlessing (Entity entity, BlessingHandler blessing){
            entity.baseAtk += blessing.shiftDano;
            entity.vidaMaxima += blessing.shiftVidaMaxima;

            if (entity.vidaAtual <= entity.vidaMaxima * 1.25){ // Sistema de Sobrevida // todo: implementar um sistema melhor, que lide com a sobrevida adequadamente
                if (entity.vidaAtual + blessing.shiftVida <= entity.vidaMaxima * 1.25){
                    entity.vidaAtual += blessing.shiftVida;
                } else {
                    entity.vidaAtual = (int) (entity.vidaMaxima * 1.25);
                }
            }
        }

        @Override
        public String toString() {
            String toString = nome;

            if (this.shiftVida != 0){
                toString = toString.join(" | ", toString, String.valueOf(shiftVida) + " de cura");
            }

            if (this.shiftVidaMaxima != 0){
                toString = toString.join(" | ", toString, String.valueOf(shiftVidaMaxima) + " de vida máxima");
            }

            if (this.shiftDano != 0){
                toString = toString.join(" | ", toString, String.valueOf(shiftDano) + " de dano base");
            }

            return toString;
        }

    }

    public static class ArmaHandler implements Loot{
        // Responsável apenas pela arma em formato de Loot.
        public final String nome;
        private final int atkExtra;
        private final String raridade; // 1 = comum, 2 = incomum, 3 = raro, 4 = épico, 5 = lendário
        private final int boostRaridade;

        @Override
        public String getTipo() {
            return "Arma";
        }

        public ArmaHandler(String nome, int atkExtra, Map<String, Integer> raridade) {
            this.nome = nome;
            this.atkExtra = atkExtra;

            Map.Entry<String, Integer> entry = raridade.entrySet().iterator().next(); // Pega o item do singletonMap
            this.raridade = entry.getKey();
            this.boostRaridade = entry.getValue();
        }

        // Geração
        static int genAtkExtra;
        static Map<String, Integer> genRaridade;
        public static ArmaHandler generateArma(int salaAtual) {
            String raridadeGerada = generateFromPool(rarityPool);
            switch(raridadeGerada){
                case "Comum":
                    genRaridade = Collections.singletonMap(raridadeGerada, 1);
                    break;
                case "Incomum":
                    genRaridade = Collections.singletonMap(raridadeGerada, 2);
                    break;
                case "Raro":
                    genRaridade = Collections.singletonMap(raridadeGerada, 3);
                    break;
                case "Épico":
                    genRaridade = Collections.singletonMap(raridadeGerada, 4);
                    break;
                case "Lendário":
                    genRaridade = Collections.singletonMap(raridadeGerada, 5);
                    break;

                default:
                    genRaridade = Collections.singletonMap(raridadeGerada, 1);
            }

            genAtkExtra = (int) Math.round(5 + (0.15 * rng.nextInt(salaAtual)) + (genRaridade.get(raridadeGerada) * GameHandler.jogador.baseAtk * 0.1));

            return new ArmaHandler(NameHandler.generateWeapon(), genAtkExtra, genRaridade);
        }

        public static void pickArma (Entity entity, ArmaHandler arma){
            entity.setArmaAtual(new Arma(arma.nome, arma.atkExtra, arma.raridade, arma.boostRaridade));
        }

        @Override
        public String toString() {
            return nome + " (" + raridade + ") | +" + atkExtra + " dano";
        }
    }
}
