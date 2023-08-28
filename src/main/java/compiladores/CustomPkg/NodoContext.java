package compiladores.CustomPkg;

public class NodoContext {
    int nivelDeProfundidad;
    Identificador identificador;

    NodoContext(int nivelDeProfundidad, Identificador identificador){
        this.nivelDeProfundidad=nivelDeProfundidad;
        this.identificador=identificador;
    }

    public String toString(){
        String stringAImprimir="";
        for(int i=0; i<this.nivelDeProfundidad;i++){
            stringAImprimir+="====";
        }
        stringAImprimir+=">";
        stringAImprimir+= this.identificador.toString();
        return stringAImprimir;
    }

    public void toPrint(){
        
        System.out.println(this.toString());
    }

    public int getNivelDeProfundidad() {
        return nivelDeProfundidad;
    }

    public void setNivelDeProfundidad(int nivelDeProfundidad) {
        this.nivelDeProfundidad = nivelDeProfundidad;
    }

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
    }

    
}
