public class Testes {

    public static void main(String[] args) {
        //converte um path relativo que tenha sido passado via parâmetro
        if (args.length != 0) {
            String s = args[0];
            System.out.println("Relativo: " + s);
            System.out.println("Absoluto: " + new Conversor().relativoParaAbsoluto(s));
        }
    }
}
