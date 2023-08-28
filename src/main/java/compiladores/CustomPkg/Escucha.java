package compiladores.CustomPkg;

import java.time.temporal.ValueRange;
import java.util.ArrayList;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import compiladores.ExpRegBaseListener;
import compiladores.ExpRegParser.Asignacion_ldContext;
import compiladores.ExpRegParser.Bloque_instruccionesContext;
import compiladores.ExpRegParser.Declaracion_funcionContext;
import compiladores.ExpRegParser.Declaracion_matrizContext;
import compiladores.ExpRegParser.Declaracion_y_asigancion_de_variableContext;
//import compiladores.ExpRegParser;
import compiladores.ExpRegParser.InstruccionContext;
import compiladores.ExpRegParser.Parametros_declaracionContext;
import compiladores.ExpRegParser.ProgramaContext;
import compiladores.ExpRegParser.Variable_o_parametro_aisladaContext;


//con ctrl + espacio tenes el shortcut para hacer override o implementaciones
public class Escucha extends ExpRegBaseListener{

    private TablaSimbolos tablaSimbolos; // Instancia de TablaSimbolos
    private ArrayList<Identificador> identificadoresTemp;

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
    public void exitDeclaracion_y_asigancion_de_variable(Declaracion_y_asigancion_de_variableContext ctx) {
        String nombrePrimerVariable = ctx.ID_NOMBRE_VAR_FUNC().getText();
        //System.out.println("TATATATA: "+nombrePrimerVariable);
        int tipoVariable=ctx.tipo_variable().getStart().getType();
        Variable tempVariable = new Variable(nombrePrimerVariable,tipoVariable);
        ArrayList<Variable> variables= new ArrayList<Variable>();
        variables.add(tempVariable);

        Asignacion_ldContext tempAsignacion_ld=ctx.asignacion_ld();
        while(tempAsignacion_ld!=null && tempAsignacion_ld.COMA()!=null){
            variables.add(new Variable(tempAsignacion_ld.ID_NOMBRE_VAR_FUNC().getText(), tipoVariable));
            tempAsignacion_ld=tempAsignacion_ld.asignacion_ld();
        }

        String aImprimir="";
        for(Variable variable : variables ){
            this.tablaSimbolos.agregarId(variable);
            aImprimir= aImprimir + variable.toString()+", ";
        }
        System.out.println(aImprimir);
    }

    @Override
    public void enterBloque_instrucciones(Bloque_instruccionesContext ctx) {
        System.out.println("Contexto nuevo!!!");
        this.tablaSimbolos.crearContexto();
        for(Identificador identificador: this.identificadoresTemp){
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
        System.out.println("FUNCION: "+funcion.toString());

    }
    @Override
    public void enterParametros_declaracion(Parametros_declaracionContext ctx) {
        RuleContext parentContext = ctx.getParent();
        if(parentContext instanceof Declaracion_funcionContext){
            Declaracion_funcionContext declaracionFuncionContext = (Declaracion_funcionContext) parentContext;
            Funcion funcion = new Funcion(declaracionFuncionContext.ID_NOMBRE_VAR_FUNC().getText(), declaracionFuncionContext.tipo_variable().getStart().getType());
            Token idToken = declaracionFuncionContext.ID_NOMBRE_VAR_FUNC().getSymbol();
            String idTexto = idToken.getText();
            System.out.println("EL PADRE ES!!!:"+ funcion.toString());
            this.tablaSimbolos.agregarId(funcion);
        }
        
    }

    

    @Override
    public void exitVariable_o_parametro_aislada(Variable_o_parametro_aisladaContext ctx) {
        Variable variableAislada= new Variable(ctx.ID_NOMBRE_VAR_FUNC().getText(),ctx.tipo_variable().getStart().getType());
        this.identificadoresTemp.add(variableAislada);
        System.out.println("VarAislada: "+variableAislada.toString());
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
        System.out.println("TERMINO EL PROGRAMAAA???!!!");
        this.tablaSimbolos.toPrint();
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
    
    

}
