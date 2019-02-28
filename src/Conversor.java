import java.util.ArrayList;

public class Conversor {

    public static String relativoParaAbsoluto(String relativo) {
        return relativoParaAbsoluto(relativo, 0, 0);
    }

    public static String relativoParaAbsoluto(String relativo, int posX, int posY) {
        ArrayList<OperacaoSVG> o = pathParaOperacao(relativo);

        StringBuilder sb = new StringBuilder();
        float xInicialAtual = 0, yInicialAtual = 0;
        for (OperacaoSVG operacao : o) {
            if (operacao.comando.equals("m")) {
                xInicialAtual = operacao.valores.get(0);
                yInicialAtual = operacao.valores.get(1);

                sb.append("m ")
                        .append(xInicialAtual + posX)
                        .append(" ")
                        .append(yInicialAtual + posY)
                        .append(" ");
            } else {
                sb.append(operacao.comando).append(" ");
                for (int i = 0; i < operacao.valores.size(); i++) {
                    if (i % 2 == 0) {
                        sb.append(operacao.valores.get(i) + xInicialAtual).append(" ");
                    } else {
                        sb.append(operacao.valores.get(i) + yInicialAtual).append(" ");
                    }
                }
            }
        }

        return sb.toString().toUpperCase().replaceAll("Z", "z");
    }

    public static ArrayList<OperacaoSVG> pathParaOperacao(String fonte) {
        ArrayList<OperacaoSVG> root = new ArrayList<>();
        String[] info = formatada(fonte).split("\\|");
        for (String s : info) {
            if (!s.isEmpty()) {
                String[] nums = s.split(" ");
                ArrayList<Float> floats = new ArrayList<>();
                for (int i = 1; i < nums.length; i++) {
                    floats.add(Float.parseFloat(nums[i]));
                }
                root.add(new OperacaoSVG(nums[0], floats));
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
