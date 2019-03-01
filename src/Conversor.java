import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class Conversor {

    /**
     * Esse campo so vai ter algum conteudo depois do
     * metodo {@code relativoParaAbsoluto} ser invocado.
     */
    public static final ArrayList<OperacaoSVG> operacoesAbsolutas = new ArrayList<>();

    public static String relativoParaAbsoluto(String relativo) {
        return relativoParaAbsoluto(relativo, 0, 0);
    }

    //TODO: arrumar posicionamento [x,y]. No momento usar apenas x=0 e y=0
    public static String relativoParaAbsoluto(String relativo, int x, int y) {
        operacoesAbsolutas.clear();
        ArrayList<OperacaoSVG> o = pathParaOperacao(relativo);

        StringBuilder sb = new StringBuilder();
        float ultimoMX = x, ultimoMY = y;
        float xAtual = 0, yAtual = 0;

        for (OperacaoSVG operacao : o) {
            //TODO: adicionar casos para os comandos [l, h, v, q]
            switch (operacao.comando) {
                case "m":
                    ultimoMX += operacao.valores.get(0);
                    ultimoMY += operacao.valores.get(1);

//                    operacao.valores.set(0, ultimoMX);
//                    operacao.valores.set(1, ultimoMY);

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
                            //operacao.valores.set(i, auxX);
                            sb.append(auxX).append(" ");
                        } else {
                            float auxY = operacao.valores.get(i) + yAtual;
                            floats.add(auxY);
                            //operacao.valores.set(i, auxY);
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

        System.out.println("operações absolutas: " + operacoesAbsolutas);
        return sb.toString().toUpperCase();
    }

    public static ArrayList<OperacaoSVG> pathParaOperacao(String fonte) {
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

    private static class OperacaoSVG {

        String comando;
        ArrayList<Float> valores;

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
