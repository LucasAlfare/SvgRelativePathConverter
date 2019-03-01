import java.util.ArrayList;

public class Conversor {

    public static String relativoParaAbsoluto(String relativo) {
        return relativoParaAbsoluto(relativo, 0, 0);
    }

    public static String relativoParaAbsoluto(String relativo, int posX, int posY) {
        ArrayList<OperacaoSVG> o = pathParaOperacao(relativo);

        StringBuilder sb = new StringBuilder();
        float xAtual = 0, yAtual = 0;
        for (OperacaoSVG operacao : o) {
            if (operacao.comando.equals("m")) {
                xAtual = operacao.valores.get(0);
                yAtual = operacao.valores.get(1);

                sb.append("m ")
                        .append(xAtual + posX)
                        .append(" ")
                        .append(yAtual + posY)
                        .append(" ");
            } else {
                sb.append(operacao.comando).append(" ");
                for (int i = 0; i < operacao.valores.size(); i++) {
                    if (i % 2 == 0) {
                        sb.append(operacao.valores.get(i) + xAtual).append(" ");
                    } else {
                        sb.append(operacao.valores.get(i) + yAtual).append(" ");
                    }
                }

                if (operacao.valores.size() >= 2) {
                    xAtual = operacao.valores.get(operacao.valores.size() - 2) + xAtual;
                    yAtual = operacao.valores.get(operacao.valores.size() - 1) + yAtual;
                }
            }
        }

        return sb.toString().toUpperCase();
    }

    public static ArrayList<OperacaoSVG> pathParaOperacao(String fonte) {
        ArrayList<OperacaoSVG> root = new ArrayList<>();
        String[] info = formatada(fonte).split("\\|");
        for (String s : info) {
            if (!s.isEmpty()) {
                String[] elementosOperacao = s.split(" ");
                ArrayList<Float> floats;

                switch (elementosOperacao[0]) {
                    case "m":

                        floats = new ArrayList<>();
                        floats.add(Float.parseFloat(elementosOperacao[1]));
                        floats.add(Float.parseFloat(elementosOperacao[2]));
                        root.add(new OperacaoSVG("m", floats));
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

                            root.add(new OperacaoSVG("c", floats));
                        }
                        break;
                    case "z":

                        floats = new ArrayList<>();
                        root.add(new OperacaoSVG("z", floats));
                        break;
                }
            }
        }

        return root;
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
