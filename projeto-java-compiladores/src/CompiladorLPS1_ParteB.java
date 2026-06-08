import java.util.ArrayList;
import java.util.List;

// ==========================================
// PARTE (b) - Analisador Sintático + ASA
// ==========================================

public class CompiladorLPS1_ParteB {

    // --- 1. Classes da Árvore Sintática Abstrata (ASA) ---
    static abstract class ASA {
        abstract void gerarCodigoC();
    }

    static class ASAProgram extends ASA {
        List<ASA> comandos;
        ASAProgram(List<ASA> comandos) { this.comandos = comandos; }
        @Override
        void gerarCodigoC() {
            for (ASA cmd : comandos) {
                cmd.gerarCodigoC();
            }
        }
    }

    static class ASAAssign extends ASA {
        char var; String value;
        ASAAssign(char var, String value) { this.var = var; this.value = value; }
        @Override
        void gerarCodigoC() {
            System.out.println("    " + var + " = " + value + ";");
        }
    }

    static class ASAGet extends ASA {
        char var;
        ASAGet(char var) { this.var = var; }
        @Override
        void gerarCodigoC() {
            System.out.println("    { gets(str); sscanf(str, \"%d\", &" + var + "); }");
        }
    }

    static class ASAOpBinaria extends ASA {
        char var; String val1, val2; String op;
        ASAOpBinaria(char var, String val1, String val2, String op) {
            this.var = var; this.val1 = val1; this.val2 = val2; this.op = op;
        }
        @Override
        void gerarCodigoC() {
            System.out.println("    " + var + " = " + val1 + " " + op + " " + val2 + ";");
        }
    }

    static class ASAPrint extends ASA {
        String value;
        ASAPrint(String value) { this.value = value; }
        @Override
        void gerarCodigoC() {
            System.out.println("    printf(\"%d\\n\", " + value + ");");
        }
    }

    static class ASAIf extends ASA {
        String var, op, val; ASA comando;
        ASAIf(String var, String op, String val, ASA comando) {
            this.var = var; this.op = op; this.val = val; this.comando = comando;
        }
        @Override
        void gerarCodigoC() {
            String opC = op.equals("#") ? "!=" : (op.equals("=") ? "==" : op);
            System.out.println("    if (" + var + " " + opC + " " + val + ") {");
            comando.gerarCodigoC();
            System.out.println("    }");
        }
    }

    static class ASAWhile extends ASA {
        String var, op, val; ASA comando;
        ASAWhile(String var, String op, String val, ASA comando) {
            this.var = var; this.op = op; this.val = val; this.comando = comando;
        }
        @Override
        void gerarCodigoC() {
            String opC = op.equals("#") ? "!=" : (op.equals("=") ? "==" : op);
            System.out.println("    while (" + var + " " + opC + " " + val + ") {");
            comando.gerarCodigoC();
            System.out.println("    }");
        }
    }

    static class ASAComposite extends ASA {
        List<ASA> comandos;
        ASAComposite(List<ASA> comandos) { this.comandos = comandos; }
        @Override
        void gerarCodigoC() {
            for (ASA cmd : comandos) {
                cmd.gerarCodigoC();
            }
        }
    }

    // --- 2. Analisador Léxico (Scanner) ---
    static class Lexer {
        String input; int pos = 0;
        Lexer(String input) { this.input = input; }
        
        char peek() {
            while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
                pos++;
            }
            if (pos >= input.length()) return '\0';
            return input.charAt(pos);
        }
        
        char next() {
            char c = peek();
            if (c != '\0') pos++;
            return c;
        }
    }

    // --- 3. Analisador Sintático (Parser) ---
    static class Parser {
        Lexer lexer;
        Parser(Lexer lexer) { this.lexer = lexer; }

        ASAProgram parseProgram() {
            List<ASA> comandos = new ArrayList<>();
            while (lexer.peek() != '\0') {
                comandos.add(parseCommand());
            }
            return new ASAProgram(comandos);
        }

        ASA parseCommand() {
            char c = lexer.next();
            switch (c) {
                case '=': return parseAssign();
                case 'G': return parseGet();
                case '+': return parseOp("+");
                case '-': return parseOp("-");
                case '*': return parseOp("*");
                case '/': return parseOp("/");
                case '%': return parseOp("%");
                case 'P': return parsePrint();
                case 'I': return parseIf();
                case 'W': return parseWhile();
                case '{': return parseComposite();
                default: throw new RuntimeException("Comando inexistente");
            }
        }

        char parseVariable() {
            char c = lexer.next();
            if (c >= 'a' && c <= 'z') return c;
            throw new RuntimeException("Variável esperada");
        }

        String parseValue() {
            char c = lexer.next();
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                return String.valueOf(c);
            }
            throw new RuntimeException("Número ou Variável esperada");
        }

        ASA parseAssign() {
            char var = parseVariable();
            String value = parseValue();
            return new ASAAssign(var, value);
        }

        ASA parseGet() {
            char var = parseVariable();
            return new ASAGet(var);
        }

        ASA parseOp(String op) {
            char var = parseVariable();
            String val1 = parseValue();
            String val2 = parseValue();
            return new ASAOpBinaria(var, val1, val2, op);
        }

        ASA parsePrint() {
            String value = parseValue();
            return new ASAPrint(value);
        }

        String[] parseComparison() {
            String var = String.valueOf(parseVariable());
            char opC = lexer.next();
            if (opC != '=' && opC != '<' && opC != '#') {
                throw new RuntimeException("Operador esperado");
            }
            String val = parseValue();
            return new String[]{var, String.valueOf(opC), val};
        }

        ASA parseIf() {
            String[] comp = parseComparison();
            ASA cmd = parseCommand();
            return new ASAIf(comp[0], comp[1], comp[2], cmd);
        }

        ASA parseWhile() {
            String[] comp = parseComparison();
            ASA cmd = parseCommand();
            return new ASAWhile(comp[0], comp[1], comp[2], cmd);
        }

        ASA parseComposite() {
            List<ASA> cmds = new ArrayList<>();
            while (lexer.peek() != '}') {
                if (lexer.peek() == '\0') throw new RuntimeException("Faltando fecha chaves }");
                cmds.add(parseCommand());
            }
            lexer.next(); // Consome o '}'
            return new ASAComposite(cmds);
        }
    }

    // --- 4. Main e Execução lendo o arquivo entrada.lps1 ---
    public static void main(String[] args) {
        String codigoLPS1 = "";

        try {
            // Carrega o conteúdo do arquivo entrada.lps1 diretamente da pasta
            codigoLPS1 = java.nio.file.Files.readString(java.nio.file.Paths.get("../entrada.lps1"));
        } catch (java.io.IOException e) {
            System.out.println("Erro ao abrir o arquivo 'entrada.lps1'. Verifique se ele está na pasta 'src'.");
            return;
        }

        System.out.println("/* === CÓDIGO FONTE LPS1 === */\n" + codigoLPS1 + "\n");
        System.out.println("/* === CÓDIGO C GERADO PELA ASA === */");

        Lexer lexer = new Lexer(codigoLPS1);
        Parser parser = new Parser(lexer);

        try {
            ASAProgram ast = parser.parseProgram();

            // Cabeçalho C fixo
            System.out.println("#include <stdio.h>");
            System.out.println("int main() {");
            System.out.println("    int a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z;");
            System.out.println("    char str[512]; // auxiliar na leitura com G\n");

            // Aciona a geração de código pela raiz da Árvore (ASA)
            ast.gerarCodigoC();

            System.out.println("\n    gets(str);");
            System.out.println("    return 0;\n}");

        } catch (Exception e) {
            // Exibe exatamente a mensagem de erro capturada pelo Parser
            System.out.println("\nErro na compilação: " + e.getMessage());
        }
    }
}