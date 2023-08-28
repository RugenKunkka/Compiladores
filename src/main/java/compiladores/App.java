package compiladores;

//import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import compiladores.CustomPkg.Escucha;

// Las diferentes entradas se explicaran oportunamente
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, Compilador!!!");
        // create a CharStream that reads from file
        CharStream input = CharStreams.fromFileName("input/entrada.txt");

        // create a lexer that feeds off of input CharStream
        ExpRegLexer lexer = new ExpRegLexer(input);
        
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // create a parser that feeds off the tokens buffer
        ExpRegParser parser = new ExpRegParser(tokens);
                
        // create Listener
        ExpRegBaseListener escucha = new Escucha();

        // Conecto el objeto con Listeners al parser
        //stefanoo descomentar!!
        parser.addParseListener(escucha);

        // Solicito al parser que comience indicando una regla gramatical
        // En este caso la regla es el simbolo inicial
        ParseTree tree =  parser.programa();


        //System.out.println(tree.toStringTree(parser));
        
        // Conectamos el visitor
        // Caminante visitor = new Caminante();
        // visitor.visit(tree);
        // System.out.println(visitor);
        // System.out.println(visitor.getErrorNodes());
        // Imprime el arbol obtenido
        // System.out.println(tree.toStringTree(parser));
        // System.out.println(escucha);

        //TablaSimbolos.getInstance().toPrint();
        
        
    }
}
