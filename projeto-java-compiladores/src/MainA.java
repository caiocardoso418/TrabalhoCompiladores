import java.nio.file.*;

public class MainA {

    public static void main(String[] args)
            throws Exception {

        String source =
            Files.readString(
                Path.of("../entrada.lps1")
            );

        Lexer lexer =
            new Lexer(source);

        ParserA parser =
            new ParserA(lexer);

        String codigoC =
            parser.parse();

        System.out.println(codigoC);
    }
}