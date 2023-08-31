package compiladores.CustomPkg;

import compiladores.ExpRegBaseVisitor;
import compiladores.ExpRegParser;
import compiladores.ExpRegParser.Asignacion_ldContext;
import compiladores.ExpRegParser.Bloque_funcionContext;
import compiladores.ExpRegParser.Bloque_ifContext;
import compiladores.ExpRegParser.Bloque_instruccionesContext;
import compiladores.ExpRegParser.Bloque_whileContext;
import compiladores.ExpRegParser.C_elseifContext;
import compiladores.ExpRegParser.ComparacionContext;
import compiladores.ExpRegParser.Comparacion_sin_parentesisContext;
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
import compiladores.ExpRegParser.Retorno_funcionContext;
import compiladores.ExpRegParser.TerminoContext;
import compiladores.ExpRegParser.Variable_o_parametro_aisladaContext;

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
            System.out.println(/*"termino: "+*/sentencia);
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


    //--- para las funciones

    @Override
    public String visitBloque_funcion(Bloque_funcionContext ctx) {
        String variables=visit(ctx.declaracion_funcion());

        System.out.println("lbl e"+this.labelEIndex);
        this.labelEIndex++;
        if(variables.length()>0){
            String[] splitedVariables=variables.split(",");
            for(int i=0; i<splitedVariables.length;i++){
                System.out.println("POP "+splitedVariables[i]);
            }
        }

        visit(ctx.bloque_instrucciones());
        System.out.println("jmp <----tengo que ver a donde saltar");

        return "";
    }

    @Override
    public String visitBloque_instrucciones(Bloque_instruccionesContext ctx) {
        visit(ctx.instrucciones());

        return "";
    }

    @Override
    public String visitInstruccion(InstruccionContext ctx) {
        visitAllHijos(ctx);

        return "";
    }

    @Override
    public String visitRetorno_funcion(Retorno_funcionContext ctx) {
        String returnToReturn="PUSH ";
        if(ctx.expresion()!=null){
            returnToReturn+=visit(ctx.expresion());
        }
        System.out.println(returnToReturn);
        return returnToReturn;
    }

    @Override
    public String visitDeclaracion_y_asignacion_de_variable(Declaracion_y_asignacion_de_variableContext ctx) {
        //System.out.println("Declaracion y asignacion de una var");
        String sentenceToReturn="";
        String firstVariable=ctx.ID_NOMBRE_VAR_FUNC().getText();
        String equal= visit(ctx.asignacion_ld());
        //esto te da por ej x,y,u =2 ó x,y,u=3 x=3;
        String totalDeclarationSentence=(firstVariable+equal);
        String[] splitedTemp=totalDeclarationSentence.split("=");
        String[] variables = splitedTemp[0].split(",");
        String[] toAssigns=null;
        if(splitedTemp.length>1){
             toAssigns = splitedTemp[1].split(",");
        }
        
        if(toAssigns!=null && toAssigns.length>1){
            for(int i=0; i<variables.length;i++){
                sentenceToReturn+=variables[i]+"="+toAssigns[i]+"\n";
            }
        } else if(toAssigns!=null) {
            for(int i=0; i<variables.length;i++){
                sentenceToReturn+=variables[i]+"="+toAssigns[0]+"\n";
            }
        } else {
            for(int i=0; i<variables.length;i++){
                sentenceToReturn+=variables[i]+"\n";
            }
        }
        System.out.println( sentenceToReturn);
        return sentenceToReturn;
    }

    @Override
    public String visitAsignacion_ld(Asignacion_ldContext ctx) {
        String assiggsToReturn="";
        if(ctx.expresion()!=null){
            assiggsToReturn+="="+visit(ctx.expresion());
            assiggsToReturn+=visit(ctx.asignacion_ld());
        }
        else if(ctx.COMA()!=null && ctx.expresion()==null){
            assiggsToReturn+=ctx.COMA().getText();
            if(ctx.ID_NOMBRE_VAR_FUNC()!=null){
                assiggsToReturn+=ctx.ID_NOMBRE_VAR_FUNC().getText();
            } else if(ctx.NUMERO()!=null){
                assiggsToReturn+=ctx.NUMERO().getText();
            }
            assiggsToReturn+=visit(ctx.asignacion_ld());
        }
        return assiggsToReturn;
    }

    @Override
    public String visitDeclaracion_funcion(Declaracion_funcionContext ctx) {
        //vienen las variables separados por coma por ej .. ,z,y,x
        String tempParamDec=visit(ctx.parametros_declaracion());

        String sentenceToReturn="";
        if(visit(ctx.parametros_declaracion()).length()>0){
            sentenceToReturn=tempParamDec.substring(1);
        } 
        
        return sentenceToReturn;
    }

    @Override
    public String visitParametros_declaracion(Parametros_declaracionContext ctx) {
        String setenceToReturn="";
        if(ctx.getChildCount()==1){
            setenceToReturn+=visit(ctx.variable_o_parametro_aislada());
            return setenceToReturn;
        } else if(ctx.getChildCount()==3){
            setenceToReturn+=visit(ctx.parametros_declaracion());
            setenceToReturn+=visit(ctx.variable_o_parametro_aislada());
            return setenceToReturn;
        }
        return "";
    }

    @Override
    public String visitVariable_o_parametro_aislada(Variable_o_parametro_aisladaContext ctx) {
        return ","+ctx.ID_NOMBRE_VAR_FUNC().getText();
    }


    //WHILE
    @Override
    public String visitBloque_while(Bloque_whileContext ctx) {
        System.out.println("lbl e"+this.labelEIndex);
        
        System.out.println(visit(ctx.comparacion()));
        System.out.println("beqz t"+(this.variableTempIndex-1)+" to e"+(this.labelEIndex+1));

        if(ctx.bloque_instrucciones()!=null){
            visit(ctx.bloque_instrucciones());
        } else{
            visit(ctx.instruccion());
        }
        
        System.out.println("jpm e"+this.labelEIndex);
        this.labelEIndex++;
        System.out.println("lbl e"+this.labelEIndex);
        this.labelEIndex++;
        return "";
    }

    @Override
    public String visitComparacion(ComparacionContext ctx) {
        String tempVisitString=visit(ctx.comparacion_sin_parentesis());
        String sentenceToReturn="t"+this.variableTempIndex+"="+tempVisitString;
        this.variableTempIndex++;
        return sentenceToReturn;
    }

    @Override
    public String visitComparacion_sin_parentesis(Comparacion_sin_parentesisContext ctx) {

        String sentenceToReturn="";
        sentenceToReturn+=visit(ctx.expresion(0));
        sentenceToReturn+=ctx.comparadores().getText();
        sentenceToReturn+=visit(ctx.expresion(1));
        
        return sentenceToReturn;
    }



    //--bloques if
    @Override
    public String visitBloque_if(Bloque_ifContext ctx) {
        System.out.println("-----------------------comienza if----------------");
        //siempre va a haber un if por lo tanto este bloque se cumple
        int bloqueInstruccionesCounter=0;
        int instruccionCounter=0;
        int comparadoresCounter=0;


        System.out.println(visit(ctx.comparacion(comparadoresCounter)));
        comparadoresCounter++;
        System.out.println("beqz t"+(this.variableTempIndex-1)+" to e"+this.labelEIndex);
        if(ctx.bloque_instrucciones(0)!=null){
            visit(ctx.bloque_instrucciones(bloqueInstruccionesCounter));
            bloqueInstruccionesCounter++;
        } else {
            visit(ctx.instruccion(instruccionCounter));
            instruccionCounter++;
        }
        System.out.println("jmp e"+"<--VER EL NUMERO del ultimo label del if!");
        System.out.println("lbl e"+this.labelEIndex);
        this.labelEIndex++;



        //c_else if
        //quiere decir que existe algun else if a partir de ahora 
        int elseIfContextCounter=0;
        C_elseifContext elseIfContext=ctx.c_elseif(elseIfContextCounter);
        System.out.println("Comienza el elseif!!!");
        while(elseIfContext!=null){
            
            System.out.println(visit(ctx.comparacion(comparadoresCounter)));
            comparadoresCounter++;
            System.out.println("beqz t"+(this.variableTempIndex-1)+" to e"+this.labelEIndex);
            if(ctx.bloque_instrucciones(0)!=null){
                visit(ctx.bloque_instrucciones(bloqueInstruccionesCounter));
                bloqueInstruccionesCounter++;
            } else {
                visit(ctx.instruccion(instruccionCounter));
                instruccionCounter++;
            }
            System.out.println("jmp e"+"<--VER EL NUMERO del ultimo label del if!");
            System.out.println("lbl e"+this.labelEIndex);
            this.labelEIndex++;
            elseIfContextCounter++;
            elseIfContext=ctx.c_elseif(elseIfContextCounter);
        }
        System.out.println("Termina el elseif");

        if(ctx.c_else()!=null){
            if(ctx.bloque_instrucciones(0)!=null){
                visit(ctx.bloque_instrucciones(bloqueInstruccionesCounter));
                bloqueInstruccionesCounter++;
            } else {
                visit(ctx.instruccion(instruccionCounter));
                instruccionCounter++;
            }
            
        }
        System.out.println("lbl e"+this.labelEIndex);
        this.labelEIndex++;
        System.out.println("-----------------------termina if----------------");
        return "";
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


    

 
    /*@Override
    public String visitInstruccion(InstruccionContext ctx) {
        visitAllHijos(ctx);
        return null;
    }*/


    //--------------------------------------

    @Override
    public String toString() {
        return this.codigoIntermedio;
    }

}
