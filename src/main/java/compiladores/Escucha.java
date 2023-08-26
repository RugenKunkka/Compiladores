package compiladores;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import compiladores.ExpRegParser.InstruccionContext;
import compiladores.ExpRegParser.ProgramaContext;


//con ctrl + espacio tenes el shortcut para hacer override o implementaciones
public class Escucha extends ExpRegBaseListener{
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
