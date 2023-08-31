package compiladores.CustomPkg;

import compiladores.ExpRegBaseVisitor;
import compiladores.ExpRegParser;
import compiladores.ExpRegParser.Asignacion_ldContext;
import compiladores.ExpRegParser.Bloque_funcionContext;
import compiladores.ExpRegParser.Bloque_instruccionesContext;
import compiladores.ExpRegParser.Declaracion_funcionContext;
import compiladores.ExpRegParser.Declaracion_y_asignacion_de_variableContext;
import compiladores.ExpRegParser.FactorContext;
import compiladores.ExpRegParser.InstruccionContext;
import compiladores.ExpRegParser.InstruccionesContext;
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
    String texto;
    Integer indent;//controla para que salga como un arbolito bien en base al nivel de profundidad respecto de la raiz del árbol
    ArrayList<ErrorNode> errores;

    int labelEIndex=0;
    int variableTempIndex=0;

    TablaSimbolos tablaSimbolos;

    public MyVisitor(){
        this.errores=new ArrayList<>();
        initString();
        this.tablaSimbolos=TablaSimbolos.getInstance();
        System.out.println("INICIALIZAMOS LE VISITOR!!");
    }

    //este metodoes el que nos va a permitir recorrer el árbol
    @Override 
    public String visit(ParseTree tree) {
        return super.visit(tree);
    }

    @Override
    public String visitDeclaracion_y_asignacion_de_variable(Declaracion_y_asignacion_de_variableContext ctx) {
        
        
        Asignacion_ldContext asignacionLdContext=ctx.asignacion_ld();
        //si el hijo asignacion_ld existe y tiene un nodo coma conectado, ya sabés que hay una declaración múltiple
        //int x,y,z =2,3,4;  
        //int x,y,z=2; Estos casos son los que contempla
        if(asignacionLdContext!=null && asignacionLdContext.COMA()!=null){
            //ejemplo intx,y,z=1,2,3
            String sentenciaMultiple = ctx.getText();
            String[] splitedResult=sentenciaMultiple.split("=");
            splitedResult[0]=this.removeVariableTypeOfSentence(splitedResult[0]);
            String[] splitedVariables=splitedResult[0].split(",");
            if(splitedResult.length>1){
                
                String[] splitedValueToAssign=splitedResult[1].split(",");
                if(splitedVariables.length == splitedValueToAssign.length){
                    for(int i=0; i<splitedVariables.length;i++){
                        System.out.println(splitedVariables[i]+"="+splitedValueToAssign[i]);
                    }
                } else {
                    for(int i=0; i<splitedVariables.length;i++){
                        System.out.println(splitedVariables[i]+"="+splitedValueToAssign[0]);
                    }
                }
            }
            //quiere decir que es solo una declaracion y no una asignacion
            //contempla el int x=3;
            else{
                
            }
            
            
        } else{
            String codigoIntermedio="";
                codigoIntermedio+=ctx.ID_NOMBRE_VAR_FUNC().getText();
                Asignacion_ldContext asignacionLdContext2=ctx.asignacion_ld();
                if(asignacionLdContext2!=null && asignacionLdContext2.IGUAL()!=null)
                {
                    codigoIntermedio+="=";
                }
                /*if(){

                }*/
                System.out.println(codigoIntermedio);
        }
        visitAllHijos(ctx);
        

        
        return null;
    }

    @Override
    public String visitTermino(TerminoContext ctx) {
        
        return null;
    }


    private String removeVariableTypeOfSentence(String string){
        String stringToReturn= string.replace("int", "");
        stringToReturn= stringToReturn.replace("float", "");
        stringToReturn= stringToReturn.replace("double", "");
        stringToReturn= stringToReturn.replace("char", "");
        return stringToReturn;
    }

    @Override
    public String visitAsignacion_ld(Asignacion_ldContext ctx) {
        TerminalNode igualNode=ctx.IGUAL();
        TerminalNode comaNode=ctx.COMA();
        TerminalNode idNode=ctx.ID_NOMBRE_VAR_FUNC();
        if(igualNode!=null){
            System.out.print(igualNode.getText());
        }
        if(comaNode!=null){
            //System.out.println("");
        }
        if(idNode!=null){
            System.out.println(idNode.getText());
        }
        visitAllHijos(ctx);
        return null;
    }

    @Override
    public String visitInstruccion(InstruccionContext ctx) {
        System.out.println("");
        this.visitAllHijos(ctx);
        return null;
    }

    @Override
    public String visitFactor(FactorContext ctx) {
        
        
        visitAllHijos(ctx);
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

    //Testeado con funciones!!
    @Override
    public String visitBloque_funcion(Bloque_funcionContext ctx) {
        System.out.println("//Declaracion de la funcion --> "+ctx.declaracion_funcion().ID_NOMBRE_VAR_FUNC().getText());
        System.out.println("lbl e"+this.labelEIndex);
        this.labelEIndex++;
        //tengo que saber cuantas variables tiene declaradas en los parentesis
        //primer variable se saca asi nomas el resto con algo de recursividad
        Declaracion_funcionContext declaracionFuncionContext=ctx.declaracion_funcion();
        Parametros_declaracionContext parametroDeclaracionContext=declaracionFuncionContext.parametros_declaracion();
        boolean existe=true;
        while(parametroDeclaracionContext!=null){
            
            System.out.println("POP "+parametroDeclaracionContext.variable_o_parametro_aislada().ID_NOMBRE_VAR_FUNC().getText());
            parametroDeclaracionContext=parametroDeclaracionContext.parametros_declaracion();
        }
        
        //de alguna forma tengo que generar el codigo
        System.out.println("-------falta bloque inst-------");
        System.out.println("PUSH t"+this.variableTempIndex);
        System.out.println("jpm Me falta saber a donde debo saltar <---");
        return null;
    }

    @Override
    public String visitBloque_instrucciones(Bloque_instruccionesContext ctx) {
        this.visitAllHijos(ctx);
        return null;
    }
    
    

    

    @Override
    public String visitParametros_declaracion(Parametros_declaracionContext ctx) {
        System.out.println("POP "+ctx.variable_o_parametro_aislada().ID_NOMBRE_VAR_FUNC());
        return null;
    }
    
    //--------------------------------------
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
