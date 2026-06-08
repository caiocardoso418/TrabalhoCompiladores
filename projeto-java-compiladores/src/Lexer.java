
public class Lexer {

    private String source;
    private int pos;

    public Lexer(String source) {
        this.source = source;
        this.pos = 0;
    }

    private char current() {

        if(pos >= source.length())
            return '\0';

        return source.charAt(pos);
    }

    private void advance() {
        pos++;
    }

    public Token nextToken() {

        while(Character.isWhitespace(current()))
            advance();

        char c = current();

        if(c == '\0')
            return new Token(TokenType.EOF,"");

        advance();

        switch(c) {

            case '=':
                return new Token(TokenType.ASSIGN,"=");

            case '+':
                return new Token(TokenType.ADD,"+");

            case '-':
                return new Token(TokenType.SUB,"-");

            case '*':
                return new Token(TokenType.MULT,"*");

            case '/':
                return new Token(TokenType.DIV,"/");

            case '%':
                return new Token(TokenType.MOD,"%");

            case '<':
                return new Token(TokenType.LESS,"<");

            case '#':
                return new Token(TokenType.NOTEQUAL,"#");

            case '{':
                return new Token(TokenType.LBRACE,"{");

            case '}':
                return new Token(TokenType.RBRACE,"}");

            case 'G':
                return new Token(TokenType.GET,"G");

            case 'P':
                return new Token(TokenType.PRINT,"P");

            case 'I':
                return new Token(TokenType.IF,"I");

            case 'W':
                return new Token(TokenType.WHILE,"W");
        }

        if(Character.isLowerCase(c))
            return new Token(TokenType.VARIABLE,String.valueOf(c));

        if(Character.isDigit(c))
            return new Token(TokenType.NUMBER,String.valueOf(c));

        throw new RuntimeException(
            "Caractere inválido: " + c
        );
    }
}