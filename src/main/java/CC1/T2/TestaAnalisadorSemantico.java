package CC1.T2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class TestaAnalisadorSemantico {

    private final static int TOTAL_CASOS_TESTE = 22;
    // Especifique abaixo qual caso de teste deseja
    // testar. Utilize 0 para testar todos
    private final static int CASO_A_SER_TESTADO = 0;

    // TODO: Trocar o caminho da sua máquina
    private final static String CAMINHO_CASOS_TESTE = "/home/felipe/intelliJProjects/CC1-T2/src/main/java/CC1/T2/casosDeTeste";


    public static void main(String[] args) throws Exception {
        File diretorioCasosTeste = new File(CAMINHO_CASOS_TESTE + "/entrada");
        File[] casosTeste = diretorioCasosTeste.listFiles();

        File diretorioSaida = new File(CAMINHO_CASOS_TESTE + "/saida");
        File[] casosTesteSaida = diretorioSaida.listFiles();

        int min = 1, max = TOTAL_CASOS_TESTE;
        if (CASO_A_SER_TESTADO >= min && CASO_A_SER_TESTADO <= max) {
            min = CASO_A_SER_TESTADO;
            max = CASO_A_SER_TESTADO;
        }
        System.out.println("---------------- Grupo -----------------");
        System.out.println(LuazinhaParser.grupo);
        System.out.println("----------------------------------------");
        for (int i = 0; i < casosTeste.length; i++) {
            Saida.clear();
            String nomeArquivo = casosTeste[i].getName();
            //InputStream casoDeTesteEntrada = TestaAnalisadorSemantico.class.getResourceAsStream("casosDeTeste/entrada/" + nomeArquivo);
            ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(casosTeste[i]));
            LuazinhaLexer lexer = new LuazinhaLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LuazinhaParser parser = new LuazinhaParser(tokens);

            LuazinhaParser.ProgramaContext arvore = parser.programa();
            AnalisadorSemantico analisadorSemantico = new AnalisadorSemantico();
            analisadorSemantico.visitPrograma(arvore);

            InputStream casoDeTesteSaida = new FileInputStream(casosTesteSaida[i]);
            comparar(nomeArquivo, casoDeTesteSaida, Saida.getTexto());
        }
    }

    private static void comparar(String nomeArquivo, InputStream saidaCorreta, String saidaObtida) throws Exception {
//        System.out.println("Obtida--------\n"+saidaObtida);
//        System.out.println("Correta--------\n"+saidaCorreta);
        InputStreamReader isr = new InputStreamReader(saidaCorreta);
        StringReader sr = new StringReader(saidaObtida);
        boolean diferente = false;
        int charFr = -1;
        int charSr = -1;
        while ((charFr = isr.read()) != -1 & (charSr = sr.read()) != -1) {
            if (charFr != charSr) {
                diferente = true;
                System.out.println("ERRO:" + nomeArquivo);
                System.out.println("=================== Saída obtida ==================");
                System.out.println(Saida.getTexto());
                System.out.println("===================================================");
                break;
            }
        }
        if (!diferente && (charFr != -1 || charSr != -1)) {
            diferente = true;
            System.out.println("ERRO:" + nomeArquivo);
            System.out.println("=================== Saída obtida ==================");
            System.out.println(Saida.getTexto());
            System.out.println("===================================================");
        }

        if (!diferente) {
            System.out.println("OK:" + nomeArquivo);
        }

    }
}