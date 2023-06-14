import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classloader.getResourceAsStream(args[0]);
        CharStream charStream = CharStreams.fromStream(inputStream);

        ImpLexer lexer = new ImpLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        ImpParser parser = new ImpParser(tokenStream);

        ParseTree tree = parser.prog();

        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.err.println("Syntax errors found, program terminated.");

            System.exit(1);
        }

        IntImp interpreter = new IntImp(new Conf());
        interpreter.visit(tree);
    }
}
