package compiladores.CustomPkg;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static final int CODIGO_TIPO_INT=11;

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
            if (ctx.tipo_variable().getText().equals("int")) {
                if(ctx.getText().contains("=")){
                    String ladoDerecho=ctx.getText().split("=")[1];
                    if(ladoDerecho.contains(".")){
                        System.out.println("Warning: a la variable <<"+ctx.ID_NOMBRE_VAR_FUNC().getText()+">> Se le esta queriendo asignar un numero decimal siendo que ésta es del tipo INT==> "+ladoDerecho);
                    }
                }
            }else if(ctx.tipo_variable().getText().equals("double") || ctx.tipo_variable().getText().equals("float")){
                if(ctx.getText().contains("=")){
                    String ladoDerecho=ctx.getText().split("=")[1];
                    if(!ladoDerecho.contains(".")){
                        System.out.println("Warning: a la variable <<"+variable.getID()+">> Se le esta queriendo asignar un numero entero siendo que ésta es del tipo "+ctx.tipo_variable().getText()+" en la sentencia ==> "+ctx.getText());
                    }
                }
            }
        }
        
    }

    @Override
    public void enterBloque_instrucciones(Bloque_instruccionesContext ctx) {
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
        funcion.setInicializada(true);

    }
    @Override
    public void enterParametros_declaracion(Parametros_declaracionContext ctx) {
        RuleContext parentContext = ctx.getParent();
        if(parentContext instanceof Declaracion_funcionContext){
            Declaracion_funcionContext declaracionFuncionContext = (Declaracion_funcionContext) parentContext;
            Funcion funcion = new Funcion(declaracionFuncionContext.ID_NOMBRE_VAR_FUNC().getText(), declaracionFuncionContext.tipo_variable().getStart().getType());//obtengo el nombre de la funcion y el tipo de valor que va a retornar la funcion XD 
            funcion.setInicializada(true);
            Token idToken = declaracionFuncionContext.ID_NOMBRE_VAR_FUNC().getSymbol();
            this.tablaSimbolos.agregarId(funcion);
        }
        
    }

    

    @Override
    public void exitVariable_o_parametro_aislada(Variable_o_parametro_aisladaContext ctx) {
        Variable variableAislada= new Variable(ctx.ID_NOMBRE_VAR_FUNC().getText(),ctx.tipo_variable().getStart().getType());
        this.identificadoresTemp.add(variableAislada);
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
           if( ctx.ID_NOMBRE_VAR_FUNC()!=null && this.tablaSimbolos.buscarId(ctx.ID_NOMBRE_VAR_FUNC().getText())==null){
                System.out.println("Warning: No se encuentra declarada la variable: "+ctx.ID_NOMBRE_VAR_FUNC().getText());
            } else{//quiere decir que la encontre a la fariable
                //tenes que ver si al lado tenes una lista de numeros,
                //tenés que ver si al lado tenes una lista de variables
                //tenés que ver si al lado tenés una lista de variables y //números
                //tenés que ver si al lado tenés una función
                //en base a eso, si son variables o funciones, los buscas dentro de la tabla de símbolos y listo, comparas el tipo
                if(ctx.getText().contains("=")){
                    String idVariable=ctx.getText().split("=")[0];
                    if(this.tablaSimbolos.buscarId(idVariable)!=null){
                        this.tablaSimbolos.buscarId(idVariable).setInicializada(true);
                    }
                    Identificador identificador=this.tablaSimbolos.buscarId(ctx.ID_NOMBRE_VAR_FUNC().getText());
                    //si es del tipo entero la variable 
                    if( identificador!=null && 
                        identificador.getTipoDato()==CODIGO_TIPO_INT
                    ){
                        //tiro un print si alguna de las variables o numeros de la derecha son decimales
                        String ladoDerechoOperadoresReemplazadosPorMenos=ctx.getText().split("=")[1].replace("+", "-").replace("*","-").replace("/","-");
                        String[] terminos=ladoDerechoOperadoresReemplazadosPorMenos.split("-");
                        boolean hayAlgunNumeroOTipoDecimal=false;
                        for(int i=0;i<terminos.length;i++){
                            //aca vos sabes que puede ser una llamada a funcion o tiene una letra
                            if(verificarSiTieneLetraOGuion(terminos[i])){
                                String variableOFuncion=terminos[i].split("\\(")[0];
                                Identificador identificador2=this.tablaSimbolos.buscarId(variableOFuncion);
                                
                                if(this.tablaSimbolos.buscarId(variableOFuncion)!=null && !this.tablaSimbolos.buscarId(variableOFuncion).getInicializada()){
                                    System.out.println("Warning: Se está intentando utilizar la variable <<"+variableOFuncion+">> que no ha sido inicializada hasta el momento de ejecutar la instruccion==> "+ctx.getText());
                                }
                                if(identificador2!=null && identificador2.getTipoDato()!=CODIGO_TIPO_INT){
                                    hayAlgunNumeroOTipoDecimal=true;
                                    System.out.println("Warning: La variable <<"+idVariable+">> es del tipo int y se le esta asignando numeros o variables decimales en la sentencia==> "+ctx.getText());
                                    //break;
                                }
                            }
                            else {//sabes que si no tiene ni una letra va a ser un número entonces
                                float numero= Float.parseFloat(terminos[i]);
                                if(tieneDecimales(numero)){
                                    hayAlgunNumeroOTipoDecimal=true;
                                    System.out.println("Warning: La variable <<"+idVariable+">> es del tipo int y se le esta asignando numeros o variables decimales en la sentencia==> "+ctx.getText());
                                    //break;
                                }
                            }
                            if(hayAlgunNumeroOTipoDecimal){
                                break;
                            }

                        }
                    }
                }
            }
        }
    }

    private static boolean tieneDecimales(double numero){
        return numero != (int) numero;
    }

    private boolean verificarSiTieneLetraOGuion(String palabra){
        String regex = ".*[a-zA-Z_].*";

        // Crea un objeto Pattern y un objeto Matcher
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(palabra);

        // Verifica si la cadena contiene letras
        if (matcher.matches()) {
            return true;
        } else {
            return false;
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
                System.out.println("Warning: No se encuentra declarada la variable: "+nombreVariable);
            } 
            //si la variable existe, voy y busco para setearla como que está siendo usada
            else if (existId!=null){
                existId.setUsada(true);
            }
        }
    }
//detecto si estoy llamando de manera incorrecta la funcion (parametros de mas o de menos)
    @Override
    public void exitLlamada_funcion(Llamada_funcionContext ctx) {
        String nombreFuncion=ctx.ID_NOMBRE_VAR_FUNC().getText();
        Funcion funcionEncontrada = (Funcion)this.tablaSimbolos.buscarId(nombreFuncion);
        if(funcionEncontrada==null){
            System.out.println("Warning: No se encuentra declarada la funcion "+ctx.getText());
        } else {
            funcionEncontrada.setUsada(true);
            if(this.bufferTempParametrosParaLlamada>funcionEncontrada.getArgumentos().size()){
                System.out.println("Warning: Se está llamando a la funcion <-- "+funcionEncontrada.getID()+" --> con parámetros de más");
            } else if(this.bufferTempParametrosParaLlamada<funcionEncontrada.getArgumentos().size()){
                System.out.println("Warning: Se está llamando a la funcion <-- "+funcionEncontrada.getID()+" --> con parámetros de menos");
            }
        }
        this.bufferTempParametrosParaLlamada=0;
    }

    @Override
    public void enterParametros_para_llamada(Parametros_para_llamadaContext ctx) {
        this.bufferTempParametrosParaLlamada++;
    }
    

    

}
