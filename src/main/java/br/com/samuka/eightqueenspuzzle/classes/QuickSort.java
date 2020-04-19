/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.samuka.eightqueenspuzzle.classes;

/**
 *
 * @author Samuel
 */
public class QuickSort {

    public static Integer[][] genotipo;
    
    public static void order(Integer[] vetor, int inicio, int fim) {
        if (inicio < fim) {
            int posicaoPivo = separar(vetor, inicio, fim);
            order(vetor, inicio, posicaoPivo - 1);
            order(vetor, posicaoPivo + 1, fim);
        }
    }

    private static int separar(Integer[] vetor, int inicio, int fim) {
        int pivo = vetor[inicio];
        Integer[] pivoGenotipo = genotipo[inicio];
        
        int i = inicio + 1, f = fim;
        while (i <= f) {
            if (vetor[i] <= pivo) {
                i++;
            } else if (pivo < vetor[f]) {
                f--;
            } else {
                int troca = vetor[i];
                vetor[i] = vetor[f];
                vetor[f] = troca;
                
                Integer[] trocaGenotipo = genotipo[i];
                genotipo[i] = genotipo[f];
                genotipo[f] = trocaGenotipo;
                
                i++;
                f--;
            }
        }
        vetor[inicio] = vetor[f];
        vetor[f] = pivo;
        
        genotipo[inicio] = genotipo[f];
        genotipo[f] = pivoGenotipo;
        
        return f;
    }

}
