/*
 * Copyright (C) 2020 Asconn
 *
 * This file is part of EightQueensPuzzle.
 * EightQueensPuzzle is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * EightQueensPuzzle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>
 */
package br.com.samuka.eightqueenspuzzle.classes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author 'Samuel José Eugênio - https://github.com/samuelgenio'
 */
public class Genetica {

    private final int QTD_CELULA = 8;

    /**
     * Quantidade de gerações.
     */
    private int qtdGeracao;

    /**
     * Quantidade de individuos da população.
     */
    private int qtdGenotipos;

    /**
     * Genotipos utilizados.
     */
    Integer[][] genotipos;

    /**
     * Fenótipos dos genotipos atuais.
     */
    Integer[] fenotipos;

    File file = new File("result.txt");

    FileWriter writer;

    private int geracaoAtual;

    private double qtdIndividuosMutacao;

    /**
     * Lista que armazenas rainhas que já se veem. Por exemplo:
     * <pre>
     * 0 0 1 0 0 0 0 0
     * 1 0 0 0 0 0 0 0
     * 0 0 0 1 0 0 0 0
     * 0 0 0 0 0 1 0 0
     * 0 0 0 0 0 0 1 0
     * 0 1 0 0 0 0 0 0
     * 0 0 0 0 1 0 0 0
     * 0 0 0 0 0 0 0 1
     * </pre> A Rainha da linha 1 vê a rainha da linha 4, quando for verificar a
     * linha 4 não será necessário efetuar o algoritmo que verifica se a mesma
     * tem visão de outra rainha, pois somente queremos a quantidade de rainhas
     * que não se veem. Controlador criado para percorrer o alvo da parte
     * superior em direção à inferior.
     *
     */
    private ArrayList<Integer> listQuenFounded;

    /**
     *
     * @param qtdGenotipos Quantidade de individuos que a população terá.
     * @param qtdGeracao Quantidade de gerações a serem processadas.
     * @param perMutacao Percentual de mutação de elementos a cada geração.
     */
    public Genetica(int qtdGenotipos, int qtdGeracao, double perMutacao) {
        this.qtdGenotipos = qtdGenotipos;
        this.qtdIndividuosMutacao = qtdGenotipos * perMutacao / 100;
        if (qtdGeracao != -1) {
            this.qtdGeracao = qtdGeracao;
        }
        try {
            writer = new FileWriter(file);
        } catch (IOException ex) {
        }

    }

    /**
     * Executa o processo da criação da geração.
     */
    public void execute() {

        generatePopulation();

        geracaoAtual = 0;

        boolean isResult = false;
        
        while (geracaoAtual < qtdGeracao - 1) {

            calculateFenotipos();

            int i = 0;
            while (i < fenotipos.length) {
                if (fenotipos[i] == 0) {
                    System.out.println("resposta encontrada na geração [" + (geracaoAtual + 1) + "]: " + i);
                    isResult = true;
                }
                i++;
            }

            if (isResult) {
                break;
            }

            nextGeneration();

            geracaoAtual++;
        }

        if (!isResult) {
            System.err.println("[" + (geracaoAtual + 1) + "] Gerações executadas, solução não encontrada!");
        }
        
        calculateFenotipos();

        int i = 0;

        try {

            String resultLine = "";
            String resultGenotipo = "";
            for (Integer[] genotipo : genotipos) {
                resultGenotipo += "["
                        + genotipo[0] + ","
                        + genotipo[1] + ","
                        + genotipo[2] + ","
                        + genotipo[3] + ","
                        + genotipo[4] + ","
                        + genotipo[5] + ","
                        + genotipo[6] + ","
                        + genotipo[7] + "]";
                resultLine += String.valueOf(fenotipos[i]);

                if (i + 1 < genotipos.length) {
                    resultLine += " - ";
                    resultGenotipo += " - ";
                }

                i++;
            }

            writer.write((geracaoAtual + 1) + "° Geração[G] -> ");
            writer.write(resultGenotipo + "\r\n");
            writer.write((geracaoAtual + 1) + "° Geração[F] -> ");
            writer.write(resultLine);
            writer.flush();
        } catch (IOException ex) {
        }

    }

    private void nextGeneration() {

        QuickSort.genotipo = genotipos;

        int i = 0;

        try {

            String resultLine = "";
            String genotipoLine = "";

            for (Integer[] genotipo : genotipos) {
                genotipoLine += "[" + genotipo[0] + ", "
                        + genotipo[1] + ", "
                        + genotipo[2] + ", "
                        + genotipo[3] + ", "
                        + genotipo[4] + ", "
                        + genotipo[5] + ", "
                        + genotipo[6] + ", "
                        + genotipo[7] + "]";
                resultLine += fenotipos[i];

                if (i + 1 < genotipos.length) {
                    resultLine += " - ";
                    genotipoLine += " - ";
                }

                i++;
            }

            writer.write((geracaoAtual + 1) + "º Geração[G] -> ");
            writer.write(genotipoLine + "\r\n");
            writer.write((geracaoAtual + 1) + "º Geração[F] -> ");
            writer.write(resultLine + "\r\n");
            writer.flush();
        } catch (IOException ex) {
        }

        QuickSort.order(fenotipos, 0, fenotipos.length - 1);

        genotipos = QuickSort.genotipo;

        Integer[][] nextGeneration = new Integer[qtdGenotipos / 2][QTD_CELULA];

        getGenotiposElitismo(nextGeneration);

        genotipos = new Integer[genotipos.length][QTD_CELULA];

        double qtdMutacaoAtual = qtdIndividuosMutacao;

        i = 0;
        int indexGenotiposAdded = 0;
        while (i < nextGeneration.length) {

            boolean isMutacao = false;

            if (qtdMutacaoAtual > 0) {

                if (qtdMutacaoAtual < 1) {
                    isMutacao = Math.random() * 1 < qtdMutacaoAtual;
                    qtdMutacaoAtual = isMutacao ? - 1 : -0;
                } else {
                    isMutacao = true;
                    qtdMutacaoAtual--;
                }

            }

            int indexCut = (int) (Math.random() * 8);

            indexCut = (indexCut == 0) ? 1 : indexCut;
            Integer[][] sons = getSon(nextGeneration[i], nextGeneration[i + 1], isMutacao, indexCut);

            genotipos[indexGenotiposAdded++] = nextGeneration[i];
            genotipos[indexGenotiposAdded++] = nextGeneration[i + 1];
            genotipos[indexGenotiposAdded++] = sons[0];
            genotipos[indexGenotiposAdded++] = sons[1];

            i = i + 2;
        }
    }

    /**
     * Produz a próxima geração.
     *
     * @param ancient1 Ancestral 1
     * @param ancient2 Ancestral 2
     * @param isMutacao Indica se os filhos sofreram mutação.
     * @param localCut Posição de corte para obtenção de apenas uma parte do
     * elemento.
     * @return Integer[][] com os dois filhos gerados
     */
    private Integer[][] getSon(Integer[] ancient1, Integer[] ancient2, boolean isMutacao, int localCut) {

        Integer[] son1 = new Integer[QTD_CELULA];
        Integer[] son2 = new Integer[QTD_CELULA];

        int j = 0;
        int indexInsert1 = j;
        int indexInsert2 = j;
        while (j < ancient1.length) {

            if (j == localCut) {
                Integer[] ancientTroca = ancient1.clone();
                ancient1 = ancient2.clone();
                ancient2 = ancientTroca.clone();
            }

            int genotipoSon1 = -1;
            int genotipoSon2 = -1;

            if (j >= localCut) {

                //filho 1
                boolean exists = exists(ancient1[indexInsert1], son1);
                indexInsert1 = exists ? -1 : j;
                while (exists) {
                    indexInsert1++;
                    exists = exists(ancient1[indexInsert1], son1);
                }

                genotipoSon1 = ancient1[indexInsert1];

                //filho 2
                exists = exists(ancient2[indexInsert2], son2);
                indexInsert2 = exists ? -1 : j;
                while (exists) {
                    indexInsert2++;
                    exists = exists(ancient2[indexInsert2], son2);
                }

                genotipoSon2 = ancient2[indexInsert2];

            } else {
                genotipoSon1 = ancient1[j];
                genotipoSon2 = ancient2[j];
                indexInsert1 = ++indexInsert2;

            }

            son1[j] = genotipoSon1;
            son2[j] = genotipoSon2;

            j++;

        }

        if (isMutacao) {

            int index = new Random().nextInt(son1.length - 1) + 1;

            if (Math.random() * 1 > 0.5) {
                son1[index] = son1[index] > 0 ? 0 : 1;
            } else {
                son2[index] = son2[index] > 0 ? 0 : 1;
            }
        }

        return new Integer[][]{son1, son2};
    }

    /**
     *
     * @param number
     * @param arrFind
     * @return
     */
    private boolean exists(int number, Integer[] arrFind) {

        boolean retorno = false;

        int count = 0;
        while (count < QTD_CELULA) {

            if (arrFind[count] == null) {
                break;
            }

            if (arrFind[count] == number) {
                retorno = true;
                break;
            }
            count++;
        }

        return retorno;
    }

    /**
     * Obtém somente os genotipos mais fortes.
     */
    private void getGenotiposElitismo(Integer[][] nextGeneration) {

        int half = genotipos.length / 2;

        int count = 0;
        while (half > 0) {
            nextGeneration[count] = genotipos[--half];
            count++;
        }
    }

    /**
     * Calcula os fenotipos dos genotipos. O fenotipo é a quantidade de rainhas
     * que não se veem. Quanto maior o número mais próximo da solução se está.
     */
    private void calculateFenotipos() {

        fenotipos = new Integer[genotipos.length];

        int i = 0;
        for (Integer[] genotipo : genotipos) {

            int fenotipo = 0;
            int j = 0;

            listQuenFounded = new ArrayList<>();

            while (j < genotipo.length) {

                fenotipo += seeAnotherQueen(j, i) ? 1 : 0;

                j++;
            }

            fenotipos[i] = fenotipo;
            i++;
        }
    }

    /**
     * Retorna true caso a rainha vê outra.
     *
     * @param index
     * @param indexGenotipoVez
     * @return
     */
    private boolean seeAnotherQueen(int index, int indexGenotipoVez) {
        boolean retorno = false;

        int posQueenSee = -1;

        int location = 1;
        boolean oneOrMoreFound = false;
        for (int i = index; i < genotipos[indexGenotipoVez].length; i++) {
            if (i == index) {
                continue;
            }

            int fLeft = genotipos[indexGenotipoVez][index] + location;
            int fRight = genotipos[indexGenotipoVez][index] - location;

            boolean executed = false;

            if (fLeft <= genotipos[indexGenotipoVez].length) {
                retorno = genotipos[indexGenotipoVez][i] == fLeft;
                posQueenSee = genotipos[indexGenotipoVez][i];
                executed = true;
            }

            if (fRight >= 0 && retorno != true) {
                retorno = genotipos[indexGenotipoVez][i] == fRight;
                posQueenSee = genotipos[indexGenotipoVez][i];
                executed = true;
            }

            if (!executed) {
                break;
            }

            if (retorno) {
                retorno = false;
                oneOrMoreFound = true;
                if (!listQuenFounded.contains(genotipos[indexGenotipoVez][index])) {
                    listQuenFounded.add(genotipos[indexGenotipoVez][index]);
                }
                if (!listQuenFounded.contains(posQueenSee)) {
                    listQuenFounded.add(posQueenSee);
                }
            }
            location++;
        }

        return oneOrMoreFound || listQuenFounded.contains(genotipos[indexGenotipoVez][index]);
    }

    /**
     * Cria a população inicial aleatória.
     */
    private void generatePopulation() {

        genotipos = new Integer[qtdGenotipos][QTD_CELULA];

        try {
            int i = 0;
            while (i < qtdGenotipos) {

                Vector vec = new Vector<Integer>();
                vec.add(1);
                vec.add(2);
                vec.add(3);
                vec.add(4);
                vec.add(5);
                vec.add(6);
                vec.add(7);
                vec.add(8);
                genotipos[i] = getGenotipo(vec);
                i++;
            }
        } catch (ParseException e) {
        }
    }

    /**
     * Obtém um genotipo
     *
     * @param vec Vector com a quantidade de genotipos
     * @return
     * @throws ParseException
     */
    private Integer[] getGenotipo(Vector vec) throws ParseException {

        Integer[] genotipo = new Integer[QTD_CELULA];
        int cont = 0;
        while (cont < QTD_CELULA) {
            int index = (int) (Math.random() * vec.size());
            try {
                genotipo[cont] = (int) vec.remove(index);
            } catch (Exception e) {
                System.err.println(vec.size());
                System.exit(0);
            }
            cont++;
        }

        return genotipo;

    }

}
