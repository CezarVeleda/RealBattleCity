import java.util.Random;

public class Inimigo_Rapido extends Tanque {

    // Gerador de números aleatórios para o inimigo escolher direções
    private Random random = new Random();

    public Inimigo_Rapido(int x, int y){
        super(x, y, 1, 3);
    }

    // NOTA PARA APRESENTAÇÃO (Polimorfismo e IA):
    // Sobrescrevemos o run() da classe Tanque. O Player usa o teclado, 
    // mas o Inimigo possui "vontade própria" na sua Thread.
    @Override
    public void run() {
        while(ligado) {
            // Tenta se mover. Se retornar false (bateu na parede)...
            if (!mover()) {
                escolherNovaDirecao(); // ...ele escolhe outro caminho!
            } else {
                // Mesmo com caminho livre, ele tem 2% de chance de virar do nada (ficar imprevisível)
                if (random.nextInt(100) < 2) {
                    escolherNovaDirecao();
                }
            }

            try {
                Thread.sleep(50);
            } catch(InterruptedException erro) {
                erro.printStackTrace();
            }
        }
    }

    // Metodo privado exclusivo da IA
    private void escolherNovaDirecao() {
        int escolha = random.nextInt(4); // Sorteia de 0 a 3

        switch (escolha) {
            case 0: set_direcao(Direcao.CIMA); break;
            case 1: set_direcao(Direcao.BAIXO); break;
            case 2: set_direcao(Direcao.ESQUERDA); break;
            case 3: set_direcao(Direcao.DIREITA); break;
        }
    }
}