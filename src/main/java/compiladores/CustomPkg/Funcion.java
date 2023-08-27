package compiladores.CustomPkg;

import java.util.List;
import java.util.ArrayList;

class Funcion extends Identificador {
    List<Integer> argumentos;

    Funcion(String ID,int tipoDato) {
        super(ID,tipoDato);
        this.argumentos = new ArrayList<>();
    }
}
