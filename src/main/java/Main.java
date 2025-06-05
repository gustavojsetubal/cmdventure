import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        GameHandler jogo = new GameHandler();

        jogo.iniciarJogo();

        try {
            Thread.sleep(5000); // pauses for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

