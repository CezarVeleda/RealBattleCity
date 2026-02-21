public class Main {
    public static void main(String[] args) {
        // Agora o Main apenas chama o Menu Principal primeiro!
        // O jogo só será carregado quando clicarem no botão "Jogar".
        MenuPrincipal menu = new MenuPrincipal();
        menu.setVisible(true);
    }
}