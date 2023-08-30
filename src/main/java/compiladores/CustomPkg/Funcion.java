package compiladores.CustomPkg;

import java.util.List;
import java.util.ArrayList;

class Funcion extends Identificador {
    List<Integer> argumentos;

    

    Funcion(String ID,int tipoDato) {
        super(ID,tipoDato);
        this.argumentos = new ArrayList<>();
    }

    void addArgumento(int argumento){
        this.argumentos.add(argumento);
    }

    public List<Integer> getArgumentos() {
        return argumentos;
    }

    @Override
    public String toString() {
        String stringToReturn =super.toString()+" argumentos: ";
        for (Integer argumento : argumentos) {
            stringToReturn+= argumento+" ";
        }
        return stringToReturn;
    }
}
