package compiladores.CustomPkg;

import compiladores.ExpRegBaseVisitor;
import compiladores.ExpRegParser;
import compiladores.ExpRegParser.Actualizacion_del_forContext;
import compiladores.ExpRegParser.AsignacionContext;
import compiladores.ExpRegParser.Asignacion_ldContext;
import compiladores.ExpRegParser.Asignacion_matrizContext;
import compiladores.ExpRegParser.Bloque_forContext;
import compiladores.ExpRegParser.Bloque_funcionContext;
import compiladores.ExpRegParser.Bloque_ifContext;
import compiladores.ExpRegParser.Bloque_instruccionesContext;
import compiladores.ExpRegParser.Bloque_whileContext;
import compiladores.ExpRegParser.C_elseifContext;
import compiladores.ExpRegParser.ComparacionContext;
import compiladores.ExpRegParser.Comparacion_sin_parentesisContext;
import compiladores.ExpRegParser.Declaracion_funcionContext;
import compiladores.ExpRegParser.Declaracion_matrizContext;
import compiladores.ExpRegParser.Declaracion_matriz_ldContext;
import compiladores.ExpRegParser.Declaracion_y_asignacion_de_variableContext;
import compiladores.ExpRegParser.ExpresionContext;
import compiladores.ExpRegParser.FactorContext;
import compiladores.ExpRegParser.InstruccionContext;
import compiladores.ExpRegParser.Instruccion_matrizContext;
import compiladores.ExpRegParser.InstruccionesContext;
import compiladores.ExpRegParser.Instrucciones_del_forContext;
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
        String sentencia="";
        

        //para el caso int y=0;
       if(ctx.getChildCount()==1){
           sentencia+=visit(ctx.termino());
       } 
           // para el caso int z=2+3+4;
       else if(ctx.getChildCount()==3 &&  ctx.expresion().expresion()==null){
           //genera el t0=2+3
           //OK!!!
           sentencia+="t"+this.variableTempIndex+"="+visit(ctx.expresion())+visit(ctx.operadores_de_menor_orden())+visit(ctx.termino())+"\n";
           this.variableTempIndex++;
       } 
       //genera el t1=t0+4 ==> del caso int z=2+3+4;
       else if(ctx.getChildCount()==3 && ctx.expresion().expresion()!=null){
           sentencia+=visit(ctx.expresion());
           sentencia+="t"+this.variableTempIndex+"="+"t"+(this.variableTempIndex-1)+visit(ctx.operadores_de_menor_orden())+visit(ctx.termino())+"\n";
           //System.out.println(sentencia);
           this.variableTempIndex++;
       }
       
        return sentencia;
    }

    @Override
    public String visitTermino(TerminoContext ctx) {
        String sentencia="";
        
        if(ctx.getChildCount()==1){
            sentencia+= visit(ctx.factor());
        }

        return sentencia;
        
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
        String bloqueFuncionToReturn="";
        bloqueFuncionToReturn+=("--------------------BLOQUE FUNCION "+"--------------------")+"\n";
        String variables=visit(ctx.declaracion_funcion());

        bloqueFuncionToReturn+="lbl e"+this.labelEIndex+"\n";
        this.labelEIndex++;
        if(variables.length()>0){
            String[] splitedVariables=variables.split(",");
            for(int i=0; i<splitedVariables.length;i++){
                bloqueFuncionToReturn+="POP "+splitedVariables[i]+"\n";
            }
        }

        bloqueFuncionToReturn+=visit(ctx.bloque_instrucciones());
        bloqueFuncionToReturn+="return\n";

        return bloqueFuncionToReturn;
    }

    @Override
    public String visitBloque_instrucciones(Bloque_instruccionesContext ctx) {
        String sentencesToReturn="";
        sentencesToReturn+=visit(ctx.instrucciones());
        
        return sentencesToReturn;
    }

    @Override
    public String visitInstruccion(InstruccionContext ctx) {
        String instructionToReturn="";
        for (int hijo = 0; hijo < ctx.getChildCount(); hijo++) {
            String stringTempVisit=visit(ctx.getChild(hijo));
            if(stringTempVisit!=null && !stringTempVisit.contains("null")){
                instructionToReturn+=stringTempVisit;
            }
        }
        //visitAllHijos(ctx);
        
        //System.out.println(instructionToReturn);
        return instructionToReturn;
    }

    @Override
    public String visitInstrucciones(InstruccionesContext ctx) {
        String sentencesToReturn="";
        for (int hijo = 0; hijo < ctx.getChildCount(); hijo++) {
            String stringTempVisit=visit(ctx.getChild(hijo));
            if(stringTempVisit!=null && !stringTempVisit.contains("null")){
                sentencesToReturn+=stringTempVisit;
            }
        }
        
        return sentencesToReturn;
    }

    @Override
    public String visitRetorno_funcion(Retorno_funcionContext ctx) {
        String returnToReturn="PUSH ";
        if(ctx.expresion()!=null){
            returnToReturn+=visit(ctx.expresion());
        }
        return returnToReturn;
    }

    @Override
    public String visitDeclaracion_y_asignacion_de_variable(Declaracion_y_asignacion_de_variableContext ctx) {
        
        String sentenceToReturn="";

        String visitAsignacionLdResult=visit(ctx.asignacion_ld());
        if(ctx.asignacion_ld().expresion()!=null && ctx.asignacion_ld().expresion().expresion()!=null){

            sentenceToReturn+=visitAsignacionLdResult;
            sentenceToReturn+=ctx.ID_NOMBRE_VAR_FUNC().getText()+"=t"+(this.variableTempIndex-1)+"\n";
        }
        else{
            if(visitAsignacionLdResult.length()==0){
                sentenceToReturn+=ctx.ID_NOMBRE_VAR_FUNC().getText()+visitAsignacionLdResult+"\n";
            } else {
                if(visitAsignacionLdResult.contains(",")){
                    String tempSentence=ctx.ID_NOMBRE_VAR_FUNC().getText()+visitAsignacionLdResult;
                    String[] splitedSentence=tempSentence.split("=");
                    String value= splitedSentence[1];
                    String[] splitedVariables=splitedSentence[0].split(",");

                    for(int i=0; i<splitedVariables.length;i++){
                        sentenceToReturn+=splitedVariables[i]+"="+value+"\n";
                    }
                } else {
                    sentenceToReturn+=ctx.ID_NOMBRE_VAR_FUNC().getText()+"="+visitAsignacionLdResult;
                }
                
            }
        }
        
        
        
        return sentenceToReturn;
    }

    @Override
    public String visitAsignacion_ld(Asignacion_ldContext ctx) {
        String assiggnsToReturn="";
       
        if(ctx.getChildCount()==3){
            if(ctx.expresion()!=null){
                assiggnsToReturn+=visit(ctx.expresion())+"\n";
            } else if(ctx.COMA()!=null){
                assiggnsToReturn+=ctx.getText();
                /*/
                if(ctx.asignacion_ld().asignacion_ld()!=null){
                    assiggnsToReturn+=visit(ctx.asignacion_ld());
                    System.out.println( ctx.getText());
                } else {
                    assiggnsToReturn+="";
                }*/
            }
            
        }

        return assiggnsToReturn;
    }

    @Override
    public String visitAsignacion(AsignacionContext ctx) {
        String sentenceToReturn="";
        
        if(ctx.expresion().getChildCount()==3){
            sentenceToReturn+=visit(ctx.expresion());
            sentenceToReturn+=ctx.ID_NOMBRE_VAR_FUNC()+"=t"+this.variableTempIndex+"\n";
        } else {
            sentenceToReturn+=ctx.ID_NOMBRE_VAR_FUNC()+"="+visit(ctx.expresion())+"\n";
        }
        
        
        return sentenceToReturn;
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
        String sentenceWhileToReturn="";
        sentenceWhileToReturn+=("--------------------BLOQUE WHILE--------------------")+"\n";
        
        sentenceWhileToReturn+="lbl e"+this.labelEIndex+"\n";
        sentenceWhileToReturn+=visit(ctx.comparacion());
        sentenceWhileToReturn+=("beqz t"+(this.variableTempIndex-1)+" to e"+(this.labelEIndex+1)+"\n");

        if(ctx.bloque_instrucciones()!=null){
            sentenceWhileToReturn+=visit(ctx.bloque_instrucciones());
        } else{
            sentenceWhileToReturn+=visit(ctx.instruccion());
        }
        
        sentenceWhileToReturn+="jpm e"+this.labelEIndex+"\n";
        this.labelEIndex++;
        sentenceWhileToReturn+="lbl e"+this.labelEIndex+"\n";
        this.labelEIndex++;
        
        return sentenceWhileToReturn;
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
        sentenceToReturn+="\n";
        return sentenceToReturn;
    }



    //--bloques if
    @Override
    public String visitBloque_if(Bloque_ifContext ctx) {
        String bloqueIfToReturn="";
        bloqueIfToReturn+=("--------------------BLOQUE IF--------------------")+"\n";
        //siempre va a haber un if por lo tanto este bloque se cumple
        int bloqueInstruccionesCounter=0;
        int instruccionCounter=0;
        int comparadoresCounter=0;

        bloqueIfToReturn+=visit(ctx.comparacion(comparadoresCounter));
        comparadoresCounter++;
        bloqueIfToReturn+=("beqz t"+(this.variableTempIndex-1)+" to e"+this.labelEIndex)+"\n";
        if(ctx.bloque_instrucciones(0)!=null){
            bloqueIfToReturn+=visit(ctx.bloque_instrucciones(bloqueInstruccionesCounter));
            bloqueInstruccionesCounter++;
        } else {
            bloqueIfToReturn+=visit(ctx.instruccion(instruccionCounter));
            instruccionCounter++;
        }
        bloqueIfToReturn+=("jmp e"+"<--VER EL NUMERO del ultimo label del if!")+"\n";
        bloqueIfToReturn+=("lbl e"+this.labelEIndex)+"\n";
        this.labelEIndex++;



        //c_else if
        //quiere decir que existe algun else if a partir de ahora 
        int elseIfContextCounter=0;
        C_elseifContext elseIfContext=ctx.c_elseif(elseIfContextCounter);
        bloqueIfToReturn+=("Comienza el elseif!!!")+"\n";
        while(elseIfContext!=null){
            
            bloqueIfToReturn+=(visit(ctx.comparacion(comparadoresCounter)))+"\n";
            comparadoresCounter++;
            bloqueIfToReturn+=("beqz t"+(this.variableTempIndex-1)+" to e"+this.labelEIndex)+"\n";
            if(ctx.bloque_instrucciones(0)!=null){
                visit(ctx.bloque_instrucciones(bloqueInstruccionesCounter));
                bloqueInstruccionesCounter++;
            } else {
                visit(ctx.instruccion(instruccionCounter));
                instruccionCounter++;
            }
            bloqueIfToReturn+=("jmp e"+"<--VER EL NUMERO del ultimo label del if!")+"\n";
            bloqueIfToReturn+=("lbl e"+this.labelEIndex)+"\n";
            this.labelEIndex++;
            elseIfContextCounter++;
            elseIfContext=ctx.c_elseif(elseIfContextCounter);
        }
        bloqueIfToReturn+=("Termina el elseif")+"\n";

        if(ctx.c_else()!=null){
            if(ctx.bloque_instrucciones(0)!=null){
                visit(ctx.bloque_instrucciones(bloqueInstruccionesCounter));
                bloqueInstruccionesCounter++;
            } else {
                visit(ctx.instruccion(instruccionCounter));
                instruccionCounter++;
            }
            
        }
        bloqueIfToReturn+=("lbl e"+this.labelEIndex)+"\n";
        this.labelEIndex++;
        bloqueIfToReturn+=("-----------------------termina if----------------")+"\n";
        return bloqueIfToReturn;
    }

    //bloque for!!
    @Override
    public String visitBloque_for(Bloque_forContext ctx) {
        String bloqueForToReturn="";
        bloqueForToReturn+=("--------------------BLOQUE FOR--------------------")+"\n";
        
        bloqueForToReturn+=visit(ctx.instruccion(0));
        bloqueForToReturn+=("lbl e"+this.labelEIndex)+"\n";

        String tempVisitString=visit(ctx.comparacion_sin_parentesis());
        String sentenceToReturn="t"+this.variableTempIndex+"="+tempVisitString;
        this.variableTempIndex++;
        bloqueForToReturn+=sentenceToReturn;//+"\n"

        bloqueForToReturn+=("beqz t"+(this.variableTempIndex-1)+" to e"+"<----- Va el ultimo lbl de este for!!")+"\n";
        
        bloqueForToReturn+=visit(ctx.bloque_instrucciones())+"\n";
        bloqueForToReturn+=(visit(ctx.actualizacion_del_for()))+"\n";
        bloqueForToReturn+=("jmp e"+this.labelEIndex)+"\n";
        this.labelEIndex++;
        bloqueForToReturn+=("lbl e"+this.labelEIndex)+"\n";

        
        return bloqueForToReturn;
    }

    @Override
    public String visitActualizacion_del_for(Actualizacion_del_forContext ctx) {
        String sentenceToReturn="";
        sentenceToReturn=ctx.ID_NOMBRE_VAR_FUNC().getText()+"=1+"+ctx.ID_NOMBRE_VAR_FUNC().getText();

        return sentenceToReturn;
    }

    //matriz
    @Override
    public String visitInstruccion_matriz(Instruccion_matrizContext ctx) {
        String sentenceToReturn="";
        if(ctx.declaracion_matriz()!=null){
            sentenceToReturn=visit(ctx.declaracion_matriz());
        } else if(ctx.declaracion_matriz_ld()!=null){
            sentenceToReturn=visit(ctx.declaracion_matriz_ld());
        } else{
            sentenceToReturn=visit(ctx.asignacion_matriz());
        }
        
        return sentenceToReturn;
    }

    @Override
    public String visitDeclaracion_matriz(Declaracion_matrizContext ctx) {
        String stringToReturn="";
        stringToReturn=visit(ctx.declaracion_matriz_ld());
        return stringToReturn;
    }

    @Override
    public String visitDeclaracion_matriz_ld(Declaracion_matriz_ldContext ctx) {
        String sentenceToReturn="";
        sentenceToReturn=ctx.getText();
        return sentenceToReturn;
    }

    @Override
    public String visitAsignacion_matriz(Asignacion_matrizContext ctx) {
        String sentenceToReturn="";
        sentenceToReturn+=visit(ctx.declaracion_matriz_ld());
        sentenceToReturn+=ctx.IGUAL().getText();
        sentenceToReturn+=visit(ctx.expresion());
        return sentenceToReturn;
    }

    //fin stefano


    @Override
    public String visitPrograma(ExpRegParser.ProgramaContext ctx) {
        String programa="";
        for (int hijo = 0; hijo < ctx.getChildCount(); hijo++) {
        
            programa+=visit(ctx.getChild(hijo));
        }
        System.out.println("-----------------PROGRAMA-----------------");
        System.out.println(programa);
        return "";
    }

    //esta funcion es para poder visitar todos los hijos ya que ANTLR no lo hace!!
    public String visitAllHijos(RuleContext ctx) {
        for (int hijo = 0; hijo < ctx.getChildCount(); hijo++) {
        
            visit(ctx.getChild(hijo));
        }
        return codigoIntermedioTemp;
    }


    //--------------------------------------

    @Override
    public String toString() {
        return this.codigoIntermedio;
    }

}
