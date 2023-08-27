package compiladores.CustomPkg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class TablaSimbolos {
    private static TablaSimbolos instance = null;

    List<HashMap<String, Identificador>> tabla;

    TablaSimbolos(){
        this.tabla =  new ArrayList<>();
        this.tabla.add(new HashMap<>());//esto que se agrega es el contexto a nivel global!!!! el resto seran agregados cada vez que se encuentre una llave
    }

    public static TablaSimbolos getInstance() {
        if (instance == null) {
            instance = new TablaSimbolos();
        }
        return instance;
    }

    void nuevoContexto() {
        tabla.add(new HashMap<>());
    }

    void eliminarContexto() {
        tabla.remove(tabla.size() - 1);
    }

    void agregarId(Identificador identificador) {
        if (!tabla.isEmpty()) {
            if(!this.tabla.get(tabla.size()-1).keySet().contains(identificador.ID)){
                tabla.get(tabla.size() - 1).put(identificador.ID, identificador);
                return;
            } else {
                System.out.println("El identificador ya existe en el contexto");
                return;
            }
        }
        System.out.println("La tabla de Simbolos no ha sido inicializada");
        return;
    }

    Identificador buscarId(String ID) {
        for (int i = tabla.size() - 1; i >= 0; i--) {
            Identificador id = tabla.get(i).get(ID);
            if (id != null) {
                return id;
            }
        }
        return null;
    }

    public List<HashMap<String, Identificador>> getTabla() {
        return tabla;
    }

    public void setTabla(List<HashMap<String, Identificador>> tabla) {
        this.tabla = tabla;
    }
    
}
