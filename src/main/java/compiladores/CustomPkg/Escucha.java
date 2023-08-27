package compiladores.CustomPkg;

import java.util.ArrayList;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import compiladores.ExpRegBaseListener;
import compiladores.ExpRegParser.Asignacion_ldContext;
import compiladores.ExpRegParser.Declaracion_y_asigancion_de_variableContext;
//import compiladores.ExpRegParser;
import compiladores.ExpRegParser.InstruccionContext;
import compiladores.ExpRegParser.ProgramaContext;


//con ctrl + espacio tenes el shortcut para hacer override o implementaciones
public class Escucha extends ExpRegBaseListener{

    private TablaSimbolos tablaSimbolos; // Instancia de TablaSimbolos

    public Escucha() {
        this.tablaSimbolos = TablaSimbolos.getInstance(); // Obtener la instancia única
    }

    @Override
    public void exitDeclaracion_y_asigancion_de_variable(Declaracion_y_asigancion_de_variableContext ctx) {
        String nombrePrimerVariable = ctx.ID_NOMBRE_VARIABLE().getText();
        int tipoVariable=ctx.tipo_variable().getStart().getType();
        Variable tempVariable = new Variable(nombrePrimerVariable,tipoVariable);
        ArrayList<Variable> variables= new ArrayList<Variable>();
        variables.add(tempVariable);

        Asignacion_ldContext tempAsignacion_ld=ctx.asignacion_ld();
        while(tempAsignacion_ld!=null && tempAsignacion_ld.COMA()!=null){
            variables.add(new Variable(tempAsignacion_ld.ID_NOMBRE_VARIABLE().getText(), tipoVariable));
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
        super.exitPrograma(ctx);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        // TODO Auto-generated method stub
        super.visitTerminal(node);
    }
    
}
