package compiladores.CustomPkg;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class TablaSimbolos  {
    private static TablaSimbolos instance = null;

    List<HashMap<String, Identificador>> tabla;
    ArrayList<NodoContext> historialContext;

    int cantidadDeContextos=0;
    int nivelDeProfundidad=0;
    private TablaSimbolos(){
        this.tabla =  new ArrayList<>();
        this.tabla.add(new HashMap<>());//esto que se agrega es el contexto a nivel global!!!! el resto seran agregados cada vez que se encuentre una llave
        this.historialContext= new ArrayList<NodoContext>();
    }

    public static synchronized  TablaSimbolos getInstance() {
        if (instance == null) {
            instance = new TablaSimbolos();
        }
        return instance;
    }

    void crearContexto() {
        tabla.add(new HashMap<>());
        this.cantidadDeContextos++;
        this.nivelDeProfundidad++;
    }

    void eliminarContexto() {
        tabla.remove(tabla.size() - 1);
        this.nivelDeProfundidad--;
    }

    void agregarId(Identificador identificador) {
        if (!tabla.isEmpty()) {
            if(!this.tabla.get(tabla.size()-1).keySet().contains(identificador.ID)){
                tabla.get(tabla.size() - 1).put(identificador.ID, identificador);
                historialContext.add(new NodoContext(this.nivelDeProfundidad, identificador));
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

    public void toPrint(){
        for(NodoContext nodoContext :this.historialContext){
            nodoContext.toPrint();
        }
    }

    public void saveTablaSimbolos() {
        String filePath = "TablaSimbolos.txt"; // Ruta relativa dentro del proyecto
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
            for (NodoContext nodoContext : this.historialContext) {
                String content = nodoContext.toString();
                writer.println(content);
            }
            System.out.println("Contenido guardado en " + filePath);
        } catch (IOException e) {
            System.err.println("Error al guardar el contenido en el archivo: " + e.getMessage());
        }
    }
    
}
