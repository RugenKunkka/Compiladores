package compiladores.CustomPkg;

abstract class Identificador {
    String ID;
    int tipoDato;
    Boolean usada;
    Boolean inicializada;
    //linea en la que se declaro la variable creo que me haria falta y lineas en las que se usa la variable también
    Identificador(String ID, int tipoVariable) {
        this.ID = ID;
        this.tipoDato=tipoVariable;
        this.usada = false;
        this.inicializada = false;
    }

    Identificador(String ID, int tipoVariable,Boolean esInicializada) {
        this.ID = ID;
        this.tipoDato=tipoVariable;
        this.usada = false;
        this.inicializada = esInicializada;
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
        stringToReturn=("ID:"+this.ID+" tipoDato:"+this.tipoDato+" esUsada?: "+this.usada+" EstaInicializada?:" + this.inicializada);
        return stringToReturn;
    }

    public void toPrint(){
        System.out.println(this.toString());
    }

}
