package compiladores.CustomPkg;

public class Variable extends Identificador{
    Variable(String ID, int tipoVariable) {
        super(ID,tipoVariable);
    }

    Variable(String ID, int tipoVariable,boolean esInicializada) {
        super(ID,tipoVariable,esInicializada);
    }
}
