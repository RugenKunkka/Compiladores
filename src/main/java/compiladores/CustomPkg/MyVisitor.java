package compiladores.CustomPkg;

import compiladores.ExpRegBaseVisitor;
import compiladores.ExpRegParser;
import compiladores.ExpRegParser.Asignacion_ldContext;
import compiladores.ExpRegParser.Bloque_funcionContext;
import compiladores.ExpRegParser.Bloque_instruccionesContext;
import compiladores.ExpRegParser.Declaracion_funcionContext;
import compiladores.ExpRegParser.Declaracion_y_asignacion_de_variableContext;
import compiladores.ExpRegParser.ExpresionContext;
import compiladores.ExpRegParser.FactorContext;
import compiladores.ExpRegParser.InstruccionContext;
import compiladores.ExpRegParser.InstruccionesContext;
import compiladores.ExpRegParser.Operadores_de_menor_ordenContext;
import compiladores.ExpRegParser.Operadores_mayor_ordenContext;
import compiladores.ExpRegParser.Parametros_declaracionContext;
import compiladores.ExpRegParser.ProgramaContext;
import compiladores.ExpRegParser.TerminoContext;

import java.util.ArrayList;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
//es para poder caminar y encontrar la info que necesitamos qy evamos a ir viendo


public class MyVisitor extends ExpRegBaseVisitor<String> {
    
    String codigoIntermedio="";
    String codigoIntermedioTemp="";
    
    ArrayList<ErrorNode> errores;

    int labelEIndex=0;
    int variableTempIndex=0;

    TablaSimbolos tablaSimbolos;

    public MyVisitor(){
        this.errores=new ArrayList<>();
        
        this.tablaSimbolos=TablaSimbolos.getInstance();
        System.out.println("INICIALIZAMOS LE VISITOR!!");
    }

    //este metodoes el que nos va a permitir recorrer el árbol
    @Override 
    public String visit(ParseTree tree) {
        return super.visit(tree);
    }
    //comienzo stefano

    @Override
    public String visitExpresion(ExpresionContext ctx) {
        if(ctx.expresion()!=null && ctx.termino()!=null){
            String sentencia="";
            sentencia+=visit(ctx.expresion());
            sentencia+=visit(ctx.operadores_de_menor_orden());
            sentencia+=visit(ctx.termino());
            sentencia="t"+this.variableTempIndex+"="+sentencia;
            this.variableTempIndex++;
            System.out.println(sentencia);
            return ("t"+(this.variableTempIndex-1));
            
        }
        if(ctx.getChildCount()==1){
            String sentencia="";
            sentencia+=visit(ctx.termino());
            //System.out.println(sentencia);
            return sentencia;
        }
        
        return null;
    }

    @Override
    public String visitTermino(TerminoContext ctx) {
        if(ctx.getChildCount()==3){
            String sentencia="";
            sentencia+=visit(ctx.termino());
            sentencia+=visit(ctx.operadores_mayor_orden());
            sentencia+=visit(ctx.factor());
            sentencia="t"+this.variableTempIndex+"="+sentencia;
            this.variableTempIndex++;
            System.out.println("termino: "+sentencia);
            return ("t"+(this.variableTempIndex-1));
        } else if(ctx.getChildCount()==1 && ctx.factor()!=null){
            return visit(ctx.factor());
        }
        return "";
        
    }

    @Override
    public String visitFactor(FactorContext ctx) {
        return ctx.getText();
    }
    @Override
    public String visitOperadores_de_menor_orden(Operadores_de_menor_ordenContext ctx) {
        return ctx.getText();
    }
    @Override
    public String visitOperadores_mayor_orden(Operadores_mayor_ordenContext ctx) {
        // TODO Auto-generated method stub
        return ctx.getText();
    }








    //fin stefano


    @Override
    public String visitPrograma(ExpRegParser.ProgramaContext ctx) {
        visitAllHijos(ctx);
        return null;
    }

    //esta funcion es para poder visitar todos los hijos ya que ANTLR no lo hace!!
    public String visitAllHijos(RuleContext ctx) {
        for (int hijo = 0; hijo < ctx.getChildCount(); hijo++) {
        
            visit(ctx.getChild(hijo));
        }
        return codigoIntermedioTemp;
    }


    @Override
    public String visitBloque_instrucciones(Bloque_instruccionesContext ctx) {
        this.visitAllHijos(ctx);
        return null;
    }


    @Override
    public String visitParametros_declaracion(Parametros_declaracionContext ctx) {
        //System.out.println("POP "+ctx.variable_o_parametro_aislada().ID_NOMBRE_VAR_FUNC());
        return null;
    }    
    @Override
    public String visitInstruccion(InstruccionContext ctx) {
        visitAllHijos(ctx);
        return null;
    }


    //--------------------------------------

    @Override
    public String toString() {
        return this.codigoIntermedio;
    }

}
