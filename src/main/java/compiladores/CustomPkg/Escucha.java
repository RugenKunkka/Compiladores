package compiladores.CustomPkg;

import java.time.temporal.ValueRange;
import java.util.ArrayList;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import compiladores.ExpRegBaseListener;
import compiladores.ExpRegParser.AsignacionContext;
import compiladores.ExpRegParser.Asignacion_ldContext;
import compiladores.ExpRegParser.Bloque_instruccionesContext;
import compiladores.ExpRegParser.Comparacion_sin_parentesisContext;
import compiladores.ExpRegParser.Declaracion_funcionContext;
import compiladores.ExpRegParser.Declaracion_matrizContext;
import compiladores.ExpRegParser.Declaracion_y_asignacion_de_variableContext;
import compiladores.ExpRegParser.ExpresionContext;
import compiladores.ExpRegParser.FactorContext;
//import compiladores.ExpRegParser;
import compiladores.ExpRegParser.InstruccionContext;
import compiladores.ExpRegParser.Llamada_funcionContext;
import compiladores.ExpRegParser.Parametros_declaracionContext;
import compiladores.ExpRegParser.Parametros_para_llamadaContext;
import compiladores.ExpRegParser.ProgramaContext;
import compiladores.ExpRegParser.TerminoContext;
import compiladores.ExpRegParser.Variable_o_parametro_aisladaContext;


//con ctrl + espacio tenes el shortcut para hacer override o implementaciones
public class Escucha extends ExpRegBaseListener{

    private TablaSimbolos tablaSimbolos; // Instancia de TablaSimbolos
    private ArrayList<Identificador> identificadoresTemp;
    int bufferTempParametrosParaLlamada;

    public Escucha() {
        this.tablaSimbolos = TablaSimbolos.getInstance(); // Obtener la instancia única
        this.identificadoresTemp=new ArrayList<Identificador>();
    }

    @Override
    public void exitDeclaracion_matriz(Declaracion_matrizContext ctx) {
        Variable tempVariable = new Variable(ctx.declaracion_matriz_ld().ID_NOMBRE_VAR_FUNC().getText(),ctx.tipo_variable().getStart().getType());
        this.tablaSimbolos.agregarId(tempVariable);

    }

    @Override
    public void exitDeclaracion_y_asignacion_de_variable(Declaracion_y_asignacion_de_variableContext ctx) {
        String nombrePrimerVariable = ctx.ID_NOMBRE_VAR_FUNC().getText();
        int tipoVariable=ctx.tipo_variable().getStart().getType();

        boolean fueInicializada=ctx.asignacion_ld().getChildCount()>0?true:false;

        Variable tempVariable = new Variable(nombrePrimerVariable,tipoVariable,fueInicializada);
        ArrayList<Variable> variables= new ArrayList<Variable>();
        variables.add(tempVariable);

        Asignacion_ldContext tempAsignacion_ld=ctx.asignacion_ld();
        //este while es para meter por ej en el contexto del if las variables que tiene como parámetro
        while(tempAsignacion_ld!=null && tempAsignacion_ld.COMA()!=null){
            variables.add(new Variable(tempAsignacion_ld.ID_NOMBRE_VAR_FUNC().getText(), tipoVariable,fueInicializada));
            tempAsignacion_ld=tempAsignacion_ld.asignacion_ld();
        }

        String aImprimir="";
        for(Variable variable : variables ){
            this.tablaSimbolos.agregarId(variable);
            aImprimir= aImprimir + variable.toString()+", ";
        }
        
    }

    @Override
    public void enterBloque_instrucciones(Bloque_instruccionesContext ctx) {
        //system.out.println("Contexto nuevo!!!");
        this.tablaSimbolos.crearContexto();
        for(Identificador identificador: this.identificadoresTemp){
            this.tablaSimbolos.addArgumentoFuncionMasCercana(identificador.getTipoDato());
            this.tablaSimbolos.agregarId(identificador);
        }
        this.identificadoresTemp.clear();
    }
    @Override
    public void exitBloque_instrucciones(Bloque_instruccionesContext ctx) {
        this.tablaSimbolos.eliminarContexto();
    }

    @Override
    public void exitDeclaracion_funcion(Declaracion_funcionContext ctx) {
        Funcion funcion= new Funcion(ctx.ID_NOMBRE_VAR_FUNC().getText(),ctx.tipo_variable().getStart().getType());
        //system.out.println("FUNCION: "+funcion.toString());

    }
    @Override
    public void enterParametros_declaracion(Parametros_declaracionContext ctx) {
        RuleContext parentContext = ctx.getParent();
        if(parentContext instanceof Declaracion_funcionContext){
            Declaracion_funcionContext declaracionFuncionContext = (Declaracion_funcionContext) parentContext;
            Funcion funcion = new Funcion(declaracionFuncionContext.ID_NOMBRE_VAR_FUNC().getText(), declaracionFuncionContext.tipo_variable().getStart().getType());
            Token idToken = declaracionFuncionContext.ID_NOMBRE_VAR_FUNC().getSymbol();
            String idTexto = idToken.getText();
            //system.out.println("EL PADRE ES!!!:"+ funcion.toString());
            this.tablaSimbolos.agregarId(funcion);
        }
        
    }

    

    @Override
    public void exitVariable_o_parametro_aislada(Variable_o_parametro_aisladaContext ctx) {
        Variable variableAislada= new Variable(ctx.ID_NOMBRE_VAR_FUNC().getText(),ctx.tipo_variable().getStart().getType());
        this.identificadoresTemp.add(variableAislada);
        //system.out.println("VarAislada: "+variableAislada.toString());
    }

    

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        // TODO Auto-generated method stub
        super.enterEveryRule(ctx);
    }

    @Override
    public void enterInstruccion(InstruccionContext ctx) {
        // TODO Auto-generated method stub
        super.enterInstruccion(ctx);
    }

    @Override
    public void enterPrograma(ProgramaContext ctx) {
        // TODO Auto-generated method stub
        super.enterPrograma(ctx);
    }
    
    @Override
    public void exitPrograma(ProgramaContext ctx) {
        // TODO Auto-generated method stub
        //super.exitPrograma(ctx);
        //system.out.println("TERMINO EL PROGRAMAAA???!!!");
        //this.tablaSimbolos.toPrint();
        this.tablaSimbolos.saveTablaSimbolos();
    }

    

    @Override
    public void visitTerminal(TerminalNode node) {
        // TODO Auto-generated method stub
        super.visitTerminal(node);
    }

    public TablaSimbolos getTablaSimbolos() {
        return tablaSimbolos;
    }
    
    //checkeo de usos
    @Override
    public void exitInstruccion(InstruccionContext ctx) {
        /*ExpresionContext expresion= ctx.expresion();
        if(expresion!=null){
            TerminoContext termino=expresion.termino();
            if(termino!=null){
                FactorContext factor=termino.factor();
                if(factor.ID_NOMBRE_VAR_FUNC()!=null){
                    //busca si existe la variable que se esta usando en el contexto
                    tablaSimbolos.buscarId(factor.ID_NOMBRE_VAR_FUNC().getText());
                }
            }
        }*/
    }

    @Override
    public void exitAsignacion(AsignacionContext ctx) {
        
        if(ctx.ID_NOMBRE_VAR_FUNC()!=null){
            if( this.tablaSimbolos.buscarId(ctx.ID_NOMBRE_VAR_FUNC().getText())==null){
                System.out.println("No se encuentra declarada la variable: "+ctx.ID_NOMBRE_VAR_FUNC().getText());
            }
        }
    }

    @Override
    public void exitComparacion_sin_parentesis(Comparacion_sin_parentesisContext ctx) {
        
    }

    //siempre que uses una variable para algo distinto que sea declaracion va a terminar en un factor por lo tanto
    //en esta funcion ya podes verificar si existe o no cualquier varbiale
    //menos las funciones
    @Override
    public void exitFactor(FactorContext ctx) {
        if(ctx.ID_NOMBRE_VAR_FUNC()!=null)
        {
            String nombreVariable=ctx.ID_NOMBRE_VAR_FUNC().getText();
            Identificador existId=this.tablaSimbolos.buscarId(nombreVariable);
            if(existId==null){
                System.out.println("No se encuentra declarada la variable: "+nombreVariable);
            } 
            //si la variable existe, voy y busco para setearla como que está siendo usada
            else if (existId!=null){
                existId.setUsada(true);
            }
        }
    }

    @Override
    public void exitLlamada_funcion(Llamada_funcionContext ctx) {
        String nombreFuncion=ctx.ID_NOMBRE_VAR_FUNC().getText();
        Funcion funcionEncontrada = (Funcion)this.tablaSimbolos.buscarId(nombreFuncion);
        if(funcionEncontrada==null){
            System.out.println("No se encuentra declarada la funcion "+ctx.getText());
        } else {
            if(this.bufferTempParametrosParaLlamada>funcionEncontrada.getArgumentos().size()){
                System.out.println("Se está llamando a la funcion <-- "+funcionEncontrada.getID()+" --> con parámetros de más");
            } else if(this.bufferTempParametrosParaLlamada<funcionEncontrada.getArgumentos().size()){
                System.out.println("Se está llamando a la funcion <-- "+funcionEncontrada.getID()+" --> con parámetros de menos");
            }
        }
        this.bufferTempParametrosParaLlamada=0;

    }

    @Override
    public void enterParametros_para_llamada(Parametros_para_llamadaContext ctx) {
        this.bufferTempParametrosParaLlamada++;
    }
    

    

}
