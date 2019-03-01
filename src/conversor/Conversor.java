package conversor;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author lucas sousa
 */
@SuppressWarnings("WeakerAccess")
public class Conversor {

    /**
     * Campo usado para armazenar o caminho passado
     */
    public String pathRelativo;

    /**
     * Esse campo so vai ter algum conteudo depois do
     * metodo {@code relativoParaAbsoluto} ser invocado.
     */
    public final ArrayList<OperacaoSVG> operacoesAbsolutas = new ArrayList<>();

    public String relativoParaAbsoluto(String relativo) {
        return relativoParaAbsoluto(relativo, 0, 0);
    }

    /**
     * Converte um path de valores relativos para valores absolutos.
     * <p>
     * Um valor absoluto sempre vai ser ele mesmo somado ao seu respectivo
     * ponto anterior. Por exemplo, se iniciamos um path com {@code x},
     * o proximo valor absoluto {@code a} sera {@code a+x}.
     * <p>
     * Portanto, tomando como exemplo o caminho relativo
     * "m 1 2 c 1 2 3 4 5 6 z" seu equivalente absoluto sera
     * "M 1 2 C 2 4 4 6 6 8 Z". Vale lembrar que cada ponto respectivo ao
     * eixo X sera somado ao mesmo ponto anterior do eixo X, o que tambem
     * vale para o eixo Y.
     * <p>
     * Alem disso e importante frizar que, caso haja mais de um comando
     * {@code m} o proximo comando {@code m} nao sera a somado ao ponto
     * anterior, que no caso poderia ser o ponto final de uma linha ou
     * curva, mas sim sera somado aos pontos do comando {@code m} anterior,
     * o que faz dos pontos do comando {@code m} serem "pontos de inicio"
     * para os comandos seguintes.
     * Por exemplo:
     * <p>
     * - Relativo: "m 1 2 c 1 2 3 4 5 6 z m 3 4 c 1 2 3 4 5 6 z"
     * - Absoluto: "M 1 2 c 2 4 4 6 6 8 Z M 4 6 C 5 8 7 10 9 12 Z"
     * <p>
     * Assim, basicamente a conversao se da pela iteracao em uma lista de
     * operacoes pre-adicionadas fazendo com que seja possivel atualizar os
     * valores de cada ponto.
     *
     * @param relativo uma String que contem o path relativo a ser convertido
     * @param x        obsoleto por enquanto
     * @param y        obsoleto por enquanto
     * @return uma String com os valores relativos convertidos para absolutos
     */
    //TODO: arrumar posicionamento [x,y]. No momento usar apenas x=0 e y=0
    public String relativoParaAbsoluto(String relativo, int x, int y) {
        pathRelativo = relativo;
        operacoesAbsolutas.clear();
        ArrayList<OperacaoSVG> o = pathParaOperacao(pathRelativo);

        StringBuilder sb = new StringBuilder();
        float ultimoMX = x, ultimoMY = y;
        float xAtual = 0, yAtual = 0;

        for (OperacaoSVG operacao : o) {
            //TODO: adicionar casos para os comandos [l, h, v, q]
            switch (operacao.comando) {
                case "m":
                    ultimoMX += operacao.valores.get(0);
                    ultimoMY += operacao.valores.get(1);

                    xAtual = ultimoMX;
                    yAtual = ultimoMY;

                    sb.append("m ")
                            .append(ultimoMX)
                            .append(" ")
                            .append(ultimoMY)
                            .append(" ");

                    operacoesAbsolutas.add(new OperacaoSVG("M", new ArrayList<>(Arrays.asList(ultimoMX, ultimoMY))));
                    break;
                case "c":
                    sb.append(operacao.comando).append(" ");
                    operacao.comando = operacao.comando.toUpperCase();
                    ArrayList<Float> floats = new ArrayList<>();
                    for (int i = 0; i < operacao.valores.size(); i++) {
                        if (i % 2 == 0) {
                            float auxX = operacao.valores.get(i) + xAtual;
                            floats.add(auxX);
                            sb.append(auxX).append(" ");
                        } else {
                            float auxY = operacao.valores.get(i) + yAtual;
                            floats.add(auxY);
                            sb.append(auxY).append(" ");
                        }
                    }

                    xAtual = operacao.valores.get(operacao.valores.size() - 2) + xAtual;
                    yAtual = operacao.valores.get(operacao.valores.size() - 1) + yAtual;

                    operacoesAbsolutas.add(new OperacaoSVG("C", floats));
                    break;
                case "z":
                    operacao.comando = operacao.comando.toUpperCase();
                    sb.append("z ");
                    operacoesAbsolutas.add(new OperacaoSVG("Z", new ArrayList<>()));
                    break;
            }
        }

        return sb.toString().toUpperCase();
    }

    public ArrayList<OperacaoSVG> pathParaOperacao(String fonte) {
        ArrayList<OperacaoSVG> operacoes = new ArrayList<>();
        String[] info = formatada(fonte).split("\\|");
        for (String s : info) {
            if (!s.isEmpty()) {
                String[] elementosOperacao = s.split(" ");
                ArrayList<Float> floats;

                //TODO: adicionar casos para os comandos [l, h, v, q]
                switch (elementosOperacao[0]) {
                    case "m":
                        floats = new ArrayList<>();
                        floats.add(Float.parseFloat(elementosOperacao[1]));
                        floats.add(Float.parseFloat(elementosOperacao[2]));
                        operacoes.add(new OperacaoSVG("m", floats));
                        break;
                    case "c":
                        for (int i = 1; i < elementosOperacao.length; i += 6) {
                            floats = new ArrayList<>();
                            floats.add(Float.parseFloat(elementosOperacao[i]));
                            floats.add(Float.parseFloat(elementosOperacao[i + 1]));
                            floats.add(Float.parseFloat(elementosOperacao[i + 2]));
                            floats.add(Float.parseFloat(elementosOperacao[i + 3]));
                            floats.add(Float.parseFloat(elementosOperacao[i + 4]));
                            floats.add(Float.parseFloat(elementosOperacao[i + 5]));

                            operacoes.add(new OperacaoSVG("c", floats));
                        }
                        break;
                    case "z":
                        floats = new ArrayList<>();
                        operacoes.add(new OperacaoSVG("z", floats));
                        break;
                }
            }
        }

        return operacoes;
    }

    public static String formatada(String input) {
        String aux = input.toLowerCase().replaceAll("-", " -").replaceAll(",", " ");

        StringBuilder s = new StringBuilder();
        for (int i = aux.length() - 1; i >= 0; i--) {
            char c = aux.charAt(i);
            if (Character.isAlphabetic(c)) {
                s.append(" ").append(c).append("|");
            } else {
                s.append(c);
            }
        }

        return s.reverse().toString().replaceAll("\\s{2,}", " ").trim();
    }

    public class OperacaoSVG {

        public String comando;
        public ArrayList<Float> valores;

        OperacaoSVG(String comando, ArrayList<Float> valores) {
            this.comando = comando;
            this.valores = valores;
        }

        @Override
        public String toString() {
            return comando + (valores.isEmpty() ? "" : ": " + valores);
        }
    }
}
