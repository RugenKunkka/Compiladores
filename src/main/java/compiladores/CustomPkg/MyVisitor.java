package compiladores.CustomPkg;

import compiladores.ExpRegBaseVisitor;
import compiladores.ExpRegParser;
import compiladores.ExpRegParser.FactorContext;
import compiladores.ExpRegParser.InstruccionContext;
import compiladores.ExpRegParser.InstruccionesContext;
import compiladores.ExpRegParser.ProgramaContext;

import java.util.ArrayList;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
//es para poder caminar y encontrar la info que necesitamos qy evamos a ir viendo


public class MyVisitor extends ExpRegBaseVisitor<String> {
    String texto;
    Integer indent;//controla para que salga como un arbolito bien en base al nivel de profundidad respecto de la raiz del árbol
    ArrayList<ErrorNode> errores;

    TablaSimbolos tablaSimbolos;

    public MyVisitor(){
        this.errores=new ArrayList<>();
        initString();
        this.tablaSimbolos=TablaSimbolos.getInstance();
        System.out.println("INICIALIZAMOS LE VISITOR!!");
    }
    //me interesa en esta parte buscar variables que estoy usando y no fueron declaradas
    @Override
    public String visitFactor(FactorContext ctx) {
        
        TerminalNode nodoIdNombreVarFun=ctx.ID_NOMBRE_VAR_FUNC();
        if(nodoIdNombreVarFun!=null){
            String idVariable = nodoIdNombreVarFun.getText();
            System.out.println(idVariable);
            if(tablaSimbolos.buscarId(idVariable)==null){
                System.out.println("La variable '"+idVariable+"'No se encuentra declarada");
            }
        }
        
        return null;
    }


    @Override
    public String visitPrograma(ExpRegParser.ProgramaContext ctx) {
        //texto += " -<(prog) " + ctx.getText() +">- \n";
        //texto += " -<(prog) " + ctx.getStart() + " <-> " + ctx.getStop() + ">- \n";
        texto += " -<(prog) {" + ctx.getStart().getText() + " <-> " + ctx.getStop().getText() + "} >- \n";
        texto += " -<(prog) {" + ctx.getChildCount() + " hijos -> ";
        addTextoNodo(ctx, "programa");
        visitAllHijos(ctx);
        // texto += "} >- \n";
        return texto;
    }

    @Override
    public String visitInstrucciones(ExpRegParser.InstruccionesContext ctx) {
        addTextoNodo(ctx, "instrucciones");
        visitAllHijos(ctx.getRuleContext());
        return null;
    }

    @Override
    public String visitInstruccion(InstruccionContext ctx) {
        addTextoNodo(ctx, "instruccion");
        visitAllHijos(ctx.getRuleContext());
        return null;
    }

    //esta funcion es para poder visitar todos los hijos ya que ANTLR no lo hace!!
    public String visitAllHijos(RuleContext ctx) {
        incrementarIndentacion();
        for (int hijo = 0; hijo < ctx.getChildCount(); hijo++) {
            addTextoNuevoNodo();
            visit(ctx.getChild(hijo));
        }
        decrementarIndentacion();
        return texto;
    }

    //este metodoes el que nos va a permitir recorrer el árbol
    @Override 
    public String visit(ParseTree tree) {
        // TODO Auto-generated method stub
        return super.visit(tree);
    }
    
    private void initString(){
        texto="\nMiVisitor\n |\n +---> ";
        this.indent= -1;
    }

    private void incrementarIndentacion(){
        this.indent ++;
    }

    private void decrementarIndentacion(){
        this.indent --;
    }

    private void addTextoNodo(RuleContext ctx, String nombre){
        //getChildCount() nos dice cuantos nodos hijos tiene cada contexto/nodo
        texto+="("+nombre+")"+ctx.getChildCount()+"Hijos\n";

    }

    private void addTextoHoja(String nombre){
        texto+="token ["+nombre+"]\n";

    }

    private void addTextoNuevoNodo(){
        texto +="       "+"   |   ".repeat(this.indent)+"   +---> ";
    }

    @Override
    public String toString() {
        return this.texto;
    }

}
