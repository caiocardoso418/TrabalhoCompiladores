public class ParserA {

    private Lexer lexer;
    private Token current;

    private StringBuilder out =
            new StringBuilder();

    public ParserA(Lexer lexer) {

        this.lexer = lexer;
        current = lexer.nextToken();
    }

    private void eat(TokenType t) {

        if(current.type != t)
            throw new RuntimeException(
                    "Esperado "
                            + t
                            + " encontrado "
                            + current.type
            );

        current = lexer.nextToken();
    }

    public String parse() {

        header();

        while(current.type != TokenType.EOF)
            command();

        out.append("\nreturn 0;\n");
        out.append("}\n");

        return out.toString();
    }

    private void header() {

        out.append("#include <stdio.h>\n\n");
        out.append("int main(){\n\n");

        out.append(
                "int a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z;\n"
        );

        out.append(
                "char str[512];\n\n"
        );
    }

    private String value() {

        String v = current.lexeme;

        if(current.type == TokenType.VARIABLE)
            eat(TokenType.VARIABLE);

        else if(current.type == TokenType.NUMBER)
            eat(TokenType.NUMBER);

        else
            throw new RuntimeException(
                    "Valor esperado. Encontrado: "
                            + current.type
            );

        return v;
    }

    private void command() {

        switch(current.type) {

            case ASSIGN:
                assign();
                break;

            case GET:
                get();
                break;

            case PRINT:
                print();
                break;

            case ADD:
                add();
                break;

            case SUB:
                sub();
                break;

            case MULT:
                mult();
                break;

            case DIV:
                div();
                break;

            case MOD:
                mod();
                break;

            case IF:
                ifcmd();
                break;

            case WHILE:
                whilecmd();
                break;

            case LBRACE:
                composite();
                break;

            default:
                throw new RuntimeException(
                        "Comando inválido: "
                                + current.type
                );
        }
    }

    // = a b
    private void assign() {

        eat(TokenType.ASSIGN);

        String dest = value();
        String src = value();

        out.append(
                dest + " = " + src + ";\n"
        );
    }

    // G a
    private void get() {

        eat(TokenType.GET);

        String var = value();

        out.append("{\n");
        out.append("gets(str);\n");
        out.append(
                "sscanf(str,\"%d\",&"
                        + var
                        + ");\n"
        );
        out.append("}\n");
    }

    // P a
    private void print() {

        eat(TokenType.PRINT);

        String v = value();

        out.append(
                "printf(\"%d\\n\","
                        + v
                        + ");\n"
        );
    }

    // + a b c
    private void add() {

        eat(TokenType.ADD);

        String a = value();
        String b = value();
        String c = value();

        out.append(
                a + " = "
                        + b
                        + " + "
                        + c
                        + ";\n"
        );
    }

    // - a b c
    private void sub() {

        eat(TokenType.SUB);

        String a = value();
        String b = value();
        String c = value();

        out.append(
                a + " = "
                        + b
                        + " - "
                        + c
                        + ";\n"
        );
    }

    // * a b c
    private void mult() {

        eat(TokenType.MULT);

        String a = value();
        String b = value();
        String c = value();

        out.append(
                a + " = "
                        + b
                        + " * "
                        + c
                        + ";\n"
        );
    }

    // / a b c
    private void div() {

        eat(TokenType.DIV);

        String a = value();
        String b = value();
        String c = value();

        out.append(
                a + " = "
                        + b
                        + " / "
                        + c
                        + ";\n"
        );
    }

    // % a b c
    private void mod() {

        eat(TokenType.MOD);

        String a = value();
        String b = value();
        String c = value();

        out.append(
                a + " = "
                        + b
                        + " % "
                        + c
                        + ";\n"
        );
    }

    private String comparison() {

        String left = value();

        String op;

        if(current.type == TokenType.LESS) {

            op = "<";
            eat(TokenType.LESS);

        } else if(current.type == TokenType.ASSIGN) {

            op = "==";
            eat(TokenType.ASSIGN);

        } else if(current.type == TokenType.NOTEQUAL) {

            op = "!=";
            eat(TokenType.NOTEQUAL);

        } else {

            throw new RuntimeException(
                    "Operador de comparação esperado"
            );
        }

        String right = value();

        return left + " " + op + " " + right;
    }

    // I a < b P a
    private void ifcmd() {

        eat(TokenType.IF);

        String cond = comparison();

        out.append(
                "if(" + cond + "){\n"
        );

        command();

        out.append("}\n");
    }

    // W a < b { ... }
    private void whilecmd() {

        eat(TokenType.WHILE);

        String cond = comparison();

        out.append(
                "while(" + cond + "){\n"
        );

        command();

        out.append("}\n");
    }

    // { comandos }
    private void composite() {

        eat(TokenType.LBRACE);

        while(current.type != TokenType.RBRACE) {
            command();
        }

        eat(TokenType.RBRACE);
    }
}