# Problema das 8 Rainhas

Implementação de um aplicativo para solucionar o problema das 8 Rainhas.

# O que é?

Consiste em posicionar 8 Rainhas sobre um tabuleiro de xadrez de forma que as rainhas não possam se ver.

# Solução

Inicialmente são gerados diversas possíveis soluções para o problema. As soluções então são ordenadas para identificar as melhores.

Através da implementação de um algoritmo ''Genético Elitista'' são selecionados os melhores individuos e executado um cruzamento entre a seleção. Processo é executado até a geração máxima ser alcançada, ou até achar uma solução.

**A fim de execução de testes é possível indicar um percentual de indivíduos para sofrerem mutação.**

**Exemplo de execução:**

* **@param qtdGenotipos** Quantidade de indivíduos que a população terá.
* **@param qtdGeracao** Quantidade de gerações a serem processadas.
* **@param perMutacao** Percentual de mutação de elementos a cada geração.

`Genetica genetica = new Genetica(52, 1000, 0);`

`genetica.execute();`
