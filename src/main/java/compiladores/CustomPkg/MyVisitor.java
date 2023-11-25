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
import compiladores.ExpRegParser.Factor_con_parentesisContext;
import compiladores.ExpRegParser.InstruccionContext;
import compiladores.ExpRegParser.Instruccion_matrizContext;
import compiladores.ExpRegParser.InstruccionesContext;
import compiladores.ExpRegParser.Instrucciones_del_forContext;
import compiladores.ExpRegParser.Llamada_funcionContext;
import compiladores.ExpRegParser.Operadores_de_menor_ordenContext;
import compiladores.ExpRegParser.Operadores_mayor_ordenContext;
import compiladores.ExpRegParser.Parametros_declaracionContext;
import compiladores.ExpRegParser.Parametros_para_llamadaContext;
import compiladores.ExpRegParser.ProgramaContext;
import compiladores.ExpRegParser.Retorno_funcionContext;
import compiladores.ExpRegParser.TerminoContext;
import compiladores.ExpRegParser.Variable_o_parametro_aisladaContext;

import java.util.ArrayList;

import org.antlr.v4.runtime.ParserRuleContext;
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
           
       }   // para el caso int z=2+3+4;
       else if(ctx.getChildCount()==3){
        
           //si expresion es recursivo (tiene varios nodos hijos de expresion)
           if(ctx.expresion()!=null && ctx.expresion().expresion()==null){
                //si termino es recursivo!!
                if(ctx.termino().termino()!=null){
                    if(ctx.termino().termino().factor().factor_con_parentesis()!=null &&
                    ctx.expresion()!=null && ctx.expresion().getChildCount()==1 &&
                    ctx.expresion().termino().getChildCount()==1){
                        int indiceTemp=this.variableTempIndex;
                        //te fijas si no es recursivo
                        if(ctx.expresion().expresion()==null){
                            //te fijas si es un producto o no
                            if(ctx.expresion().getChildCount()==1){
                                indiceTemp=this.variableTempIndex;
                                sentencia+="t"+this.variableTempIndex+"=";
                                this.variableTempIndex++;
                                sentencia+=visit(ctx.expresion());
                            } else{
                                sentencia+="t"+this.variableTempIndex+"="+visit(ctx.expresion());
                                sentencia+="\n";
                                this.variableTempIndex++;
                            }
                            
                        }
                        sentencia+=visit(ctx.termino());
                        //sentencia+="\n";
                        //t3=t0+t2
                        //this.variableTempIndex++;
                        sentencia+="t"+this.variableTempIndex+"=";
                        sentencia+="t"+indiceTemp+ctx.operadores_de_menor_orden().getText()+"t"+(this.variableTempIndex-1);
                        //sentencia+="t"+this.variableTempIndex+"=t"+indiceTemp;
                        //sentencia+=ctx.operadores_de_menor_orden().getText()+"t"+(this.variableTempIndex-1);
                        sentencia+="\n";
                    } 
                    //x=2+2*3; es para cachar este caso
                    else if(ctx.expresion().getChildCount()==1 &&
                        ctx.expresion().termino().getChildCount()==1 &&
                        ctx.termino().getChildCount()==3
                    ){
                        int indexTemp=this.variableTempIndex;
                        sentencia+="t"+this.variableTempIndex+"="+visit(ctx.expresion());
                        sentencia+="\n";
                        this.variableTempIndex++;
                        sentencia+=visit(ctx.termino());

                        sentencia+="t"+this.variableTempIndex+"=";
                        sentencia+="t"+indexTemp;
                        sentencia+=ctx.operadores_de_menor_orden().getText();
                        sentencia+="t"+(this.variableTempIndex-1);
                        sentencia+="\n";
                    }
                    else {
                        int indiceTemp=this.variableTempIndex;
                        //te fijas si no es recursivo
                        if(ctx.expresion().expresion()==null){
                            //te fijas si es un producto o no
                            if(ctx.expresion().getChildCount()==1){
                                sentencia+=visit(ctx.expresion());
                                indiceTemp=this.variableTempIndex-1;
                            } else{
                                sentencia+="t"+this.variableTempIndex+"="+visit(ctx.expresion());
                                sentencia+="\n";
                                this.variableTempIndex++;
                            }
                            
                        }
                        sentencia+=visit(ctx.termino());
                        //sentencia+="\n";
                        
                        sentencia+="t"+this.variableTempIndex+"=t"+indiceTemp;
                        sentencia+=ctx.operadores_de_menor_orden().getText()+"t"+(this.variableTempIndex-1);
                        //sentencia+="t";
                        sentencia+="\n";
                    }
                } else {
                    //si esta solo el termino
                    //si es producto..
                    if(ctx.expresion().expresion()==null && ctx.expresion().termino()!=null && ctx.expresion().termino().operadores_mayor_orden()!=null){
                        //tengo que ver si el lazo izquierdo esta solo o no 
                        //si no está solo... 
                        if(ctx.expresion().termino().getChildCount()==3){
                            //me fijo si los 2 lados tienen mas de 1 miembro o termino
                            if(ctx.expresion().termino().getChildCount()==3 &&
                            ctx.termino().factor().factor_con_parentesis()!=null && 
                            ctx.termino().factor().factor_con_parentesis().expresion()!=null &&
                            ctx.termino().factor().factor_con_parentesis().expresion().getChildCount()==3
                            ){
                                sentencia+=visit(ctx.expresion());
                                int indexTemp=this.variableTempIndex-1;
                                sentencia+=visit(ctx.termino());
                                sentencia+="t"+this.variableTempIndex+"=";
                                sentencia+="t"+indexTemp;
                                sentencia+=ctx.operadores_de_menor_orden().getText();
                                sentencia+="t"+(this.variableTempIndex-1);
                                sentencia+="\n";
                                /*int indexTemp=this.variableTempIndex-1;
                                sentencia+="taa"+this.variableTempIndex+"=";
                                sentencia+="t"+indexTemp;
                                sentencia+=ctx.operadores_de_menor_orden().getText()+visit(ctx.termino())+"\n";*/
                            } else {
                                sentencia+=visit(ctx.expresion());
                                int indexTemp=this.variableTempIndex-1;
                                sentencia+="t"+this.variableTempIndex+"=";
                                sentencia+="t"+indexTemp;
                                sentencia+=ctx.operadores_de_menor_orden().getText()+visit(ctx.termino())+"\n";
                            }
                            

                        }
                        else {
                            sentencia+=visit(ctx.expresion());
                            sentencia+="t"+this.variableTempIndex+"=";
                            sentencia+=ctx.operadores_de_menor_orden().getText()+visit(ctx.termino())+"\n";
                        }
                    } else {
                        ParserRuleContext parentExpresion=ctx.getParent();
                        //en caso de que exista un parentesis ==> 2+(3*4)
                        if(ctx.termino().factor().factor_con_parentesis()!=null){
                            int tempIndex=this.variableTempIndex;
                            sentencia+="t"+this.variableTempIndex+"=";
                            sentencia+=visit(ctx.expresion())+"\n";
                            this.variableTempIndex++;
                            sentencia+=visit(ctx.termino());
                            sentencia+="t"+this.variableTempIndex+"=";
                            sentencia+="t"+ tempIndex+ctx.operadores_de_menor_orden().getText() +"t"+(this.variableTempIndex-1);
                            
                            sentencia+="\n";

                            //sentencia+=
                            //sentencia+=ctx.operadores_de_menor_orden().getText();
                        } else if(parentExpresion instanceof ExpresionContext && ((ExpresionContext)parentExpresion).termino().factor().factor_con_parentesis()!=null){
                            //x=2+2+(b+c);
                            //System.out.println("aaaa==> "+ctx.getText());
                            sentencia+="t"+this.variableTempIndex+"=";
                            sentencia+=visit(ctx.expresion());
                            sentencia+=visit(ctx.operadores_de_menor_orden());
                            sentencia+=visit(ctx.termino());
                            sentencia+="\n";
                        }
                        else {
                            //System.out.println("bbbb==> "+ctx.getText());
                            sentencia+="t"+this.variableTempIndex+"=";
                            sentencia+=visit(ctx.expresion())+ctx.operadores_de_menor_orden().getText();
                            sentencia+=visit(ctx.termino());
                            sentencia+="\n";
                        }
                        
                    }
                    
                }
                
                this.variableTempIndex++;
           }  
           //si expresion no es recursivo ( no tiene tantos nodos hijos de expresion)    
           else if(ctx.expresion().expresion()!=null){//ok
                //si termino es recursivo?? oseaa 2+3+4
                if(ctx.termino().termino()!=null){
                    sentencia+=visit(ctx.expresion());
                    int indexTemp=this.variableTempIndex-1;
                    sentencia+=visit(ctx.termino());
                    sentencia+="t"+this.variableTempIndex+"=";
                    sentencia+="t"+indexTemp+ctx.operadores_de_menor_orden().getText()+"t"+(this.variableTempIndex-1);
                    sentencia+="\n";
                    this.variableTempIndex++;
                }
                else if(ctx.expresion()!=null && ctx.expresion().getChildCount()==3
                && ctx.termino().factor().factor_con_parentesis()!=null &&
                ctx.termino().factor().factor_con_parentesis().expresion().getChildCount()==3
                ){
                    
                    sentencia+=visit(ctx.expresion());
                    int tempIndex=this.variableTempIndex-1;
                    sentencia+=visit(ctx.termino());

                    sentencia+="t"+this.variableTempIndex+"=";
                    sentencia+="t"+tempIndex;
                    sentencia+=ctx.operadores_de_menor_orden().getText();
                    sentencia+="t"+(this.variableTempIndex-1);
                    sentencia+="\n";
                    this.variableTempIndex++;
                }
                else 
                {
                    sentencia+=visit(ctx.expresion());
                    sentencia+="t"+this.variableTempIndex+"=";
                    sentencia+="t"+(this.variableTempIndex-1);
                    sentencia+=visit(ctx.operadores_de_menor_orden());
                    sentencia+=visit(ctx.termino())+"\n";
                    this.variableTempIndex++;
                }
           }
        
       } 
        return sentencia;
    }

    @Override
    public String visitTermino(TerminoContext ctx) {
        String sentencia="";
        
        if(ctx.getChildCount()==1){
            sentencia+= visit(ctx.factor());
        } else if(ctx.getChildCount()==3) {
            //es recursivo???
            if(ctx.termino().termino()!=null){
                sentencia+=visit(ctx.termino());
                sentencia+="t"+(this.variableTempIndex)+"=";
                sentencia+="t"+(this.variableTempIndex-1);
                sentencia+=visit(ctx.operadores_mayor_orden());
                sentencia+=visit(ctx.factor())+"\n";
                this.variableTempIndex++;
                //falta poner en el else if si no hay recursividad de lado izquierdo
            } else if(ctx.getChildCount()==3 && ctx.factor().factor_con_parentesis()!=null
            ){
                //t0=2 ok
                sentencia+="\n";
                sentencia+=visit(ctx.factor());

                sentencia+="t"+this.variableTempIndex+"=";
                sentencia+=visit(ctx.termino());
                sentencia+=visit(ctx.operadores_mayor_orden());
                sentencia+="t"+(this.variableTempIndex-1);
                sentencia+="\n";
                sentencia+="\n";
                this.variableTempIndex++;
            }
            else if(ctx.factor().factor_con_parentesis()!=null){
                //System.out.println("aaa:"+ctx.getText());
                sentencia+="t"+this.variableTempIndex+"=";
                this.variableTempIndex++;
                sentencia+=visit(ctx.termino());
                int tempIndex=this.variableTempIndex-1;
                sentencia+="\n";
                //sentencia+=visit(ctx.operadores_mayor_orden());
                sentencia+=visit(ctx.factor());
                sentencia+="t"+this.variableTempIndex+"=";
                sentencia+="t"+tempIndex+ctx.operadores_mayor_orden().getText()+"t"+(this.variableTempIndex-1);
                sentencia+="\n";
                this.variableTempIndex++;
            }
            //caso x=2+(b+c)*3;
            else if(ctx.termino().factor().factor_con_parentesis()!=null){
                sentencia+="\n";
                sentencia+=visit(ctx.termino());
                //OJO con las sumas y restas al tempIndex
                /*sentencia+="t"+(this.variableTempIndex+1)+"=";
                sentencia+="t"+this.variableTempIndex+ctx.operadores_mayor_orden().getText()+visit(ctx.factor())+"\n";*/
                sentencia+="t"+(this.variableTempIndex)+"=";
                sentencia+="t"+(this.variableTempIndex-1)+ctx.operadores_mayor_orden().getText()+visit(ctx.factor())+"\n";
                this.variableTempIndex++;

            }
            else {
                sentencia+="t"+this.variableTempIndex+"=";
                sentencia+=visit(ctx.termino());
                sentencia+=visit(ctx.operadores_mayor_orden());
                sentencia+=visit(ctx.factor())+"\n";
                this.variableTempIndex++;
            }
        }
        return sentencia;
        
    }

    @Override
    public String visitFactor(FactorContext ctx) {
        if(ctx.factor_con_parentesis()==null){
            return ctx.getText();
        }
        else{
            return visit(ctx.factor_con_parentesis());
        }
        
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
        String returnToReturn="";
        if(ctx.getChildCount()>1){
            //para productoria sirve
            if(ctx.expresion()!=null && ctx.expresion().termino().getChildCount()==3){
                returnToReturn+=visit(ctx.expresion());
                returnToReturn+="PUSH t"+(this.variableTempIndex-1)+"\n";
            }  else {
                returnToReturn+="PUSH ";
                returnToReturn+=visit(ctx.expresion());
                returnToReturn+="\n";
            }
        } else {
            //returnToReturn+="return \n";
        }
        //return returnToReturn;
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
                    String[] splitedVariables=splitedSentence[0].split(",");
                    if(splitedSentence.length>1){
                        String value= splitedSentence[1];
                        for(int i=0; i<splitedVariables.length;i++){
                            sentenceToReturn+=splitedVariables[i]+"="+value+"\n";
                        }
                    } else {
                        for(int i=0; i<splitedVariables.length;i++){
                            sentenceToReturn+=splitedVariables[i]+"\n";
                        }
                    }
                    
                } else {
                    
                    if(ctx.asignacion_ld()!=null && ctx.asignacion_ld().expresion()!=null && ctx.asignacion_ld().expresion().termino().getChildCount()>1){
                        sentenceToReturn+=visitAsignacionLdResult;
                        sentenceToReturn+=ctx.ID_NOMBRE_VAR_FUNC().getText()+"=t"+(this.variableTempIndex-1)+"\n";
                    } else {
                        sentenceToReturn+=ctx.ID_NOMBRE_VAR_FUNC().getText()+"="+visitAsignacionLdResult;//no se si va este. lo puse por si acaso
                    }
                    
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
        /*sentenceToReturn+=visit(ctx.expresion());
        sentenceToReturn+=ctx.ID_NOMBRE_VAR_FUNC()+"=t"+(this.variableTempIndex-1)+"\n";*/
        if(ctx.expresion().getChildCount()==3){
            sentenceToReturn+=visit(ctx.expresion());
            sentenceToReturn+=ctx.ID_NOMBRE_VAR_FUNC()+"=t"+(this.variableTempIndex-1)+"\n";
        } else if(ctx.expresion()!=null && ctx.expresion().termino().getChildCount()==1){
            sentenceToReturn+=ctx.ID_NOMBRE_VAR_FUNC()+"="+visit(ctx.expresion())+"\n";
        } else {
            sentenceToReturn+=visit(ctx.expresion());
            sentenceToReturn+=ctx.ID_NOMBRE_VAR_FUNC()+"=t"+(this.variableTempIndex-1)+"\n";
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
        bloqueIfToReturn+=("jmp e"+"<--ReplaceIfLabel")+"\n";
        bloqueIfToReturn+=("lbl e"+this.labelEIndex)+"\n";
        this.labelEIndex++;



        //c_else if
        //quiere decir que existe algun else if a partir de ahora 
        int elseIfContextCounter=0;
        C_elseifContext elseIfContext=ctx.c_elseif(elseIfContextCounter);
        while(elseIfContext!=null){
            //bloqueIfToReturn+=("entramos al elseif!!!")+"\n";
            bloqueIfToReturn+=(visit(ctx.comparacion(comparadoresCounter)))+"\n";
            comparadoresCounter++;
            bloqueIfToReturn+=("beqz t"+(this.variableTempIndex-1)+" to e"+this.labelEIndex)+"\n";
            if(ctx.bloque_instrucciones(bloqueInstruccionesCounter)!=null){
                //bloqueIfToReturn+=("entramos al bloque!!!")+"\n";
                bloqueIfToReturn+=visit(ctx.bloque_instrucciones(bloqueInstruccionesCounter));
                bloqueInstruccionesCounter++;
            } else {
                bloqueIfToReturn+=visit(ctx.instruccion(instruccionCounter));
                instruccionCounter++;
            }
            bloqueIfToReturn+=("jmp e"+"<--ReplaceIfLabel")+"\n";
            bloqueIfToReturn+=("lbl e"+this.labelEIndex)+"\n";
            this.labelEIndex++;
            elseIfContextCounter++;
            elseIfContext=ctx.c_elseif(elseIfContextCounter);
        }
        //bloqueIfToReturn+=("Termina el elseif")+"\n";

        if(ctx.c_else()!=null){
            if(ctx.bloque_instrucciones(bloqueInstruccionesCounter)!=null){
                bloqueIfToReturn+=visit(ctx.bloque_instrucciones(bloqueInstruccionesCounter));
                bloqueInstruccionesCounter++;
            } else {
                bloqueIfToReturn+=visit(ctx.instruccion(instruccionCounter));
                instruccionCounter++;
            }
        }
        bloqueIfToReturn+=("lbl e"+this.labelEIndex)+"\n";
        bloqueIfToReturn=bloqueIfToReturn.replaceAll("<--ReplaceIfLabel",  Integer.toString(this.labelEIndex));
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

        bloqueForToReturn+=("beqz t"+(this.variableTempIndex-1)+" to e"+"<-----ReplaceForLabel")+"\n";
        
        bloqueForToReturn+=visit(ctx.bloque_instrucciones())+"\n";
        bloqueForToReturn+=(visit(ctx.actualizacion_del_for()))+"\n";
        bloqueForToReturn+=("jmp e"+this.labelEIndex)+"\n";
        this.labelEIndex++;
        bloqueForToReturn+=("lbl e"+this.labelEIndex)+"\n";
        bloqueForToReturn=bloqueForToReturn.replace("<-----ReplaceForLabel", Integer.toString(this.labelEIndex));

        
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

    //llamadas a funcion 
    @Override
    public String visitParametros_para_llamada(Parametros_para_llamadaContext ctx) {
        String sentenceToReturn="";
        if(ctx.getChildCount()==1){
            sentenceToReturn+="PUSH "+visit(ctx.expresion());
            sentenceToReturn+="\n";
        } else {
            sentenceToReturn+=visit(ctx.parametros_para_llamada());
            sentenceToReturn+="PUSH "+visit(ctx.expresion());
            sentenceToReturn+="\n";
        }
        
        return sentenceToReturn;
    }

    @Override
    public String visitLlamada_funcion(Llamada_funcionContext ctx) {
        String sentenceToReturn="";

        sentenceToReturn+=visit(ctx.parametros_para_llamada());
        sentenceToReturn+="call "+ctx.ID_NOMBRE_VAR_FUNC().getText()+"\n";
        //si tiene dato para devolver???
        sentenceToReturn+="POP "+"\n";

        return sentenceToReturn;
    }
//finnn

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

    @Override
    public String visitFactor_con_parentesis(Factor_con_parentesisContext ctx) {
        return visit(ctx.expresion());
    }

    //--------------------------------------

    @Override
    public String toString() {
        return this.codigoIntermedio;
    }

}