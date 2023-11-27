package compiladores.CustomPkg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Optimizador {

    String codigoIntermedioInicial;
    String[] listaDeInstrucciones;
    ArrayList<String> arrayDeInstrucciones;

    public Optimizador(String codigoIntermedioInicial){
        this.codigoIntermedioInicial=codigoIntermedioInicial;
        listaDeInstrucciones=codigoIntermedioInicial.split("\\r?\\n");
        arrayDeInstrucciones= new ArrayList<>();
        eliminarRenglonesVacios();
    }
    
    public void optimizar(){
        int cantidadDeSentenciasAnterior=0;
        while(cantidadDeSentenciasAnterior!=arrayDeInstrucciones.size()){
            cantidadDeSentenciasAnterior=arrayDeInstrucciones.size();
            eliminiarYReemplazarLosQueSonIguales();//OK!!!
            reemplazarDeIzquierdaADerecha();
            reemplazarDerechaPorDerecha();
            propagacionDeConstantes();
        }
        
        
    }

    public void propagacionDeConstantes(){
        String[] operadores={"+","-","*","/"};
        for(int i=0; i<this.arrayDeInstrucciones.size();i++){
            String ladoIzquierdo=arrayDeInstrucciones.get(i).split("=")[0];
            String ladoDerecho=arrayDeInstrucciones.get(i).split("=")[1];
            if(!verificarSiTieneLetraOGuion(ladoDerecho)){
                for(String operador:operadores){
                    if(ladoDerecho.contains(operador)){
                         int indiceAsterisco = ladoDerecho.indexOf(operador);
                        float numero1 = Float.parseFloat(ladoDerecho.substring(0, indiceAsterisco));
                        float numero2 = Float.parseFloat(ladoDerecho.substring(indiceAsterisco + 1));
                        //float numero1=Float.parseFloat(ladoDerecho.split(operador)[0]);
                        //float numero2=Float.parseFloat(ladoDerecho.split(operador)[1]);
                        float resultado=0;
                        if(operador.equals("+")){
                            resultado=numero1+numero2;
                        } else if(operador.equals("-")){
                            resultado=numero1-numero2;
                        } else if(operador.equals("*")){
                            resultado=numero1*numero2;
                        } else {
                            resultado=numero1/numero2;
                        }
                        if(!tieneDecimales(resultado)){
                            String sentenciaNueva="";
                            sentenciaNueva+=ladoIzquierdo;
                            sentenciaNueva+="=";
                            sentenciaNueva+=Integer.toString((int)resultado);
                            arrayDeInstrucciones.set(i,sentenciaNueva);
                        }
                    }
                }
            }
        }
    }

    private static boolean tieneDecimales(double numero){
        return numero != (int) numero;
    }

    private boolean verificarSiTieneLetraOGuion(String palabra){
        String regex = ".*[a-zA-Z_].*";

        // Crea un objeto Pattern y un objeto Matcher
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(palabra);

        // Verifica si la cadena contiene letras
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }


    //busca los elementos del lado izquierdo y derecho qeu sólo tengan un término y los reemplaza
    //buscando reemplazar el lado derecho por la equivalencia por ejemplo
    /* 
     * t7=t0+t6
     * x1=t7
     * ==> x1=t0+t6
     * 
     * t0=b
     * t7=t0+t6
     * ==> t7=t0+t6
     * 
     * por ultimo elimina esos terminos como el t7=t0+t6 compleot y t0=b
     * 
    */
    private void reemplazarDeIzquierdaADerecha(){
        String[] operadores={"+","-","*","/"};
        for(int i=0; i<arrayDeInstrucciones.size();i++){
            String ladoIzquerdo=arrayDeInstrucciones.get(i).split("=")[0];
            String ladoDerecho=arrayDeInstrucciones.get(i).split("=")[1];
            boolean contieneOperadores=false;
            for(String operador:operadores){
                if(ladoDerecho.contains(operador)){//si no hay operador es xq está solo y estamos en condiciones de reemplazar en las próximas sentencias
                    contieneOperadores=true;
                    break;
                }
            }
            if(!contieneOperadores){
                boolean reemplazamosUnaVez=false;
                for(int j=i+1;j<arrayDeInstrucciones.size();j++){
                    //si tenía t0=b; tengo que buscar ahora del lado derecho en las prox instruccioens t0 y reemplazarlas por b y si en algun momento reemplazo, tengo que
                    //eliminar la sentencia o instruccion completa
                    String ladoDerTemp=arrayDeInstrucciones.get(j).split("=")[1];
                    for(String operador:operadores){
                        if(ladoDerTemp.contains(operador)){
                            int indiceAsterisco = ladoDerTemp.indexOf(operador);
                            String termino1 = ladoDerTemp.substring(0, indiceAsterisco);
                            String termino2 = ladoDerTemp.substring(indiceAsterisco + 1);
                            boolean podemosReemplazar=false;
                            if(termino1.equals(ladoIzquerdo)){
                                termino1=ladoDerecho;
                                podemosReemplazar=true;
                            }
                            if(termino2.equals(ladoIzquerdo)){
                                termino2=ladoDerecho;
                                podemosReemplazar=true;
                            }
                            if(podemosReemplazar){
                                String sentenciaTemporal="";
                                sentenciaTemporal+=arrayDeInstrucciones.get(j).split("=")[0];
                                sentenciaTemporal+="=";
                                sentenciaTemporal+=termino1;
                                sentenciaTemporal+=operador;
                                sentenciaTemporal+=termino2;
                                arrayDeInstrucciones.set(j, sentenciaTemporal);
                                reemplazamosUnaVez=true;
                            }
                            break;
                        }
                    }   
                }
                if(reemplazamosUnaVez){
                    arrayDeInstrucciones.remove(i);
                    i--;
                }
            }

        }
    }

    //ejemplo 
    //t15=b-t6
    //x2=t15
    //==> x2=b-t6 y se elimina la sentencia de t15
    private void reemplazarDerechaPorDerecha(){
        String[] operadores={"+","-","*","/"};
        for(int i=0; i<arrayDeInstrucciones.size();i++){
            String ladoIzquierdo=arrayDeInstrucciones.get(i).split("=")[0];
            String ladoDerecho=arrayDeInstrucciones.get(i).split("=")[1];
            boolean seReemplazoUnaVez=false;
            for(String operador:operadores){
                if(ladoDerecho.contains(operador)){
                    for(int j=i+1;j<arrayDeInstrucciones.size();j++){
                        String ladoIzquierdoTemp=arrayDeInstrucciones.get(j).split("=")[0];
                        String ladoDerechoTemp=arrayDeInstrucciones.get(j).split("=")[1];
                        boolean sePuedeReemplazar=false;
                        if(ladoDerechoTemp.equals(ladoIzquierdo)){
                            ladoDerechoTemp=ladoDerecho;
                            sePuedeReemplazar=true;
                        }
                        if(sePuedeReemplazar){
                            String sentenciaTemporal="";
                            sentenciaTemporal+=ladoIzquierdoTemp;
                            sentenciaTemporal+="=";
                            sentenciaTemporal+=ladoDerecho;
                            arrayDeInstrucciones.set(j, sentenciaTemporal);
                            seReemplazoUnaVez=true;
                        }
                        
                    }
                }
            }
            if(seReemplazoUnaVez){
                arrayDeInstrucciones.remove(i);
                i--;
            }
        }
    }


    private void eliminarRenglonesVacios(){
        for (String instruccion : listaDeInstrucciones) {
            // Verificar si la línea no está vacía antes de agregarla
            if (!instruccion.trim().isEmpty()) {
                arrayDeInstrucciones.add(instruccion);
            }
        }
    }
    private void eliminiarYReemplazarLosQueSonIguales(){
        String[] operadores={"+","-","*","/"};
        for(int i=0; i<arrayDeInstrucciones.size();i++){
            String instruccion=arrayDeInstrucciones.get(i);
            String ladoIzquierdoDeLaIgualdad=instruccion.split("=")[0];
            String ladoDerechoIgualdad="="+instruccion.split("=")[1];
            ArrayList<String> tParaReemplazar = new ArrayList<String>();
            for(int j=i+1;j<arrayDeInstrucciones.size();j++){
                String ladoDerTemp="="+arrayDeInstrucciones.get(j).split("=")[1];
                if(ladoDerTemp.equals(ladoDerechoIgualdad)){
                    tParaReemplazar.add(arrayDeInstrucciones.get(j).split("=")[0]);
                    arrayDeInstrucciones.remove(j);
                    j--;
                } else if(tParaReemplazar.size()>0){
                    ladoDerTemp=ladoDerTemp.replaceAll("=", "");
                    for(String operador: operadores){
                        if(ladoDerTemp.contains(operador)){
                            /*String termino1=ladoDerTemp.split(operador)[0];
                            String termino2=ladoDerTemp.split(operador)[1];*/
                            String termino1="";
                            String termino2="";
                            if(operador.equals("*")){
                                int indiceAsterisco = ladoDerTemp.indexOf("*");
                                termino1 = ladoDerTemp.substring(0, indiceAsterisco);
                                termino2 = ladoDerTemp.substring(indiceAsterisco + 1);
                            }
                            else{
                                termino1=ladoDerTemp.split(operador)[0];
                                termino2=ladoDerTemp.split(operador)[1];
                            }
                            
                            boolean reemplazmos=false;
                            for(int q=0;q<tParaReemplazar.size();q++){
                                if(tParaReemplazar.get(q).equals(termino1)){
                                    termino1=ladoIzquierdoDeLaIgualdad;
                                    //System.out.println(tParaReemplazar.get(q));
                                    reemplazmos=true;
                                }
                                if(tParaReemplazar.get(q).equals(termino2)){
                                    termino2=ladoIzquierdoDeLaIgualdad;
                                    //System.out.println(tParaReemplazar.get(q));
                                    reemplazmos=true;
                                }
                            }
                            if(reemplazmos==true){
                                String sentenciaTemp="";
                                sentenciaTemp+=arrayDeInstrucciones.get(j).split("=")[0];
                                sentenciaTemp+="=";
                                sentenciaTemp+=termino1;
                                sentenciaTemp+=operador;
                                sentenciaTemp+=termino2;
                                arrayDeInstrucciones.set(j,sentenciaTemp);
                            }
                            
                            break;
                        }
                    }
                }
            }
        }
    }

    public void printCodigoOptimizado(){
        System.out.println("-----------CODIGO OPTIMIZADO-----------");
        for(int i=0; i<arrayDeInstrucciones.size();i++){
            System.out.println(arrayDeInstrucciones.get(i));
        }
    }
}
