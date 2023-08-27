package compiladores.CustomPkg;

abstract class Identificador {
    String ID;
    int tipoDato;
    Boolean usada;
    Boolean inicializada;

    Identificador(String ID, int tipoVariable) {
        this.ID = ID;
        this.tipoDato=tipoVariable;
        this.usada = false;
        this.inicializada = false;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public int getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(int tipoDato) {
        this.tipoDato = tipoDato;
    }

    public Boolean getUsada() {
        return usada;
    }

    public void setUsada(Boolean usada) {
        this.usada = usada;
    }

    public Boolean getInicializada() {
        return inicializada;
    }

    public void setInicializada(Boolean inicializada) {
        this.inicializada = inicializada;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        String stringToReturn="";
        stringToReturn=("ID:"+this.ID+" tipoDato:"+this.tipoDato );
        return stringToReturn;
    }

    public void toPrint(){
        System.out.println(this.toString());
    }

}
