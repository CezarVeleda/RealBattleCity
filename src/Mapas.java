public class Mapas {

    // NOTA PARA APRESENTAÇÃO (Evitando Números Mágicos):
    // Em vez de usar "5" ou "4" para a base, injetamos a constante Bloco.BASE diretamente
    // na matriz. Isso garante que o jogo nunca vai quebrar caso o valor da variável mude.

    // NOTA PARA APRESENTAÇÃO (Level Design e Balanceamento):
    // Foram adicionados blocos de AÇO (2) na coluna central do topo para evitar que o
    // inimigo que nasce no meio tenha linha de tiro direta ("Sniper") para destruir a base.

    public static int[][] getMapaFase1() {
        return new int[][]{
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,1,1,0,1,1,0,2,0,1,1,0,1,1,0}, // <-- ESCUDO ANTI-SNIPER AQUI (Aço no meio!)
                {0,1,1,0,1,1,0,5,5,1,1,0,1,1,0}, // Árvore (5) para camuflagem
                {0,1,1,0,1,1,0,5,5,1,1,0,1,1,0}, // Árvore (5) para camuflagem
                {0,1,1,0,1,1,0,1,0,1,1,0,1,1,0},
                {0,0,0,0,0,0,0,2,0,0,0,0,0,0,0}, // <-- Outro escudo de Aço no meio da tela
                {0,1,1,0,2,2,0,0,0,2,2,0,1,1,0},
                {0,1,1,0,2,2,0,0,0,2,2,0,1,1,0},
                {0,0,0,3,3,3,0,0,0,0,0,0,0,0,0}, // Água (3) para bloquear o tanque
                {1,1,0,3,3,3,0,0,0,1,1,1,0,1,1}, // Água (3) para bloquear o tanque
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,1,1,1,0,0,0,0,0,0},
                // A base agora usa a variável oficial da sua classe Bloco
                {0,0,0,0,0,0,1,Bloco.BASE,1,0,0,0,0,0,0}
        };
    }

    // MAPA 2 (REFORMULADO): Layout de Arena Dinâmica.
    // Focado em corredores abertos para a Inteligência Artificial não ficar presa no topo,
    // usando Água no centro para forçar os inimigos a atacarem pelas laterais.
    public static int[][] getMapaFase2() {
        return new int[][]{
                // Linha 0: Área de Spawn limpa
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                // Linha 1 e 2: Escudo anti-sniper no meio, blocos nas pontas
                {0,1,1,0,0,5,0,2,0,5,0,0,1,1,0},
                {0,1,1,0,0,5,0,2,0,5,0,0,1,1,0},
                // Linha 3: Corredor expresso horizontal (Evita que fiquem presos)
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                // Linha 4 e 5: Lago central de Água (3) forçando o desvio, com barricadas de Aço (2) nas bordas
                {2,2,0,1,1,0,3,3,3,0,1,1,0,2,2},
                {2,2,0,1,1,0,3,3,3,0,1,1,0,2,2},
                // Linha 6: Corredor expresso horizontal
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                // Linha 7 e 8: Defesas de Aço no meio do mapa, mato nas bordas
                {0,5,5,0,2,2,0,1,0,2,2,0,5,5,0},
                {0,5,5,0,2,2,0,1,0,2,2,0,5,5,0},
                // Linha 9: Corredor expresso horizontal
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                // Linha 10 e 11: Mato (5) perto do Player para camuflagem inicial
                {1,1,0,5,5,0,0,2,0,0,5,5,0,1,1},
                {1,1,0,5,5,0,1,1,1,0,5,5,0,1,1},
                // Linha 12: Base
                {0,0,0,0,0,0,1,Bloco.BASE,1,0,0,0,0,0,0}
        };
    }

    // MAPA 3: Rio central dividindo o cenário e trincheiras de mato
    public static int[][] getMapaFase3() {
        return new int[][]{
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,1,1,1,1,0,0,2,0,0,1,1,1,1,0}, // Escudo anti-sniper
                {0,1,2,2,1,0,3,3,3,0,1,2,2,1,0}, // Rio de Água (3) cortando a passagem
                {0,1,2,2,1,0,3,3,3,0,1,2,2,1,0}, // Rio de Água (3) cortando a passagem
                {0,1,1,1,1,0,0,0,0,0,1,1,1,1,0},
                {0,0,0,0,0,0,2,2,2,0,0,0,0,0,0},
                {0,1,1,0,1,0,2,2,2,0,1,0,1,1,0},
                {0,1,1,0,1,0,5,5,5,0,1,0,1,1,0}, // Trincheira de mato (5)
                {0,1,1,0,1,1,1,5,1,1,1,0,1,1,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,1,1,0,2,2,3,3,3,2,2,0,1,1,1}, // Fosso de Água (3) perto da base
                {0,0,0,0,0,0,1,1,1,0,0,0,0,0,0},
                {0,0,0,0,0,0,1,Bloco.BASE,1,0,0,0,0,0,0}
        };
    }
}