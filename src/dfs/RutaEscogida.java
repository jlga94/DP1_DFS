/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dfs;

import java.util.ArrayList;

/**
 *
 * @author alulab14
 */
public class RutaEscogida {
    private ArrayList<Ruta> listaRutaEscogida ;        
    private int tiempoRuta;
    public int capacidades=0;
    private ArrayList<Integer> tiemposEspera;
    private ArrayList<Integer> tiemposTraslado;
    private int estadoRuta;//0: Factible 1:No Factible Por Tiempo 2: No Factible Por Capacidad Almacen 3: No Factible Por Capacidad Vuelo

    public RutaEscogida(int tiempoRuta){
        this.listaRutaEscogida= new ArrayList<Ruta>();
        this.tiemposEspera = new ArrayList<Integer>();
        this.tiemposTraslado = new ArrayList<Integer>();
        this.tiempoRuta=tiempoRuta;
        this.estadoRuta=-1;//Empieza siendo factible
    }

    public RutaEscogida(Ruta ruta,int tiempoTraslado,int tiempoEspera){
        this.listaRutaEscogida = new ArrayList<Ruta>();
        this.listaRutaEscogida.add(ruta);
        this.tiempoRuta=tiempoTraslado+tiempoEspera;
        this.tiemposEspera = new ArrayList<Integer>();
        this.tiemposEspera.add(tiempoEspera);
        this.tiemposTraslado = new ArrayList<Integer>();
        this.tiemposTraslado.add(tiempoTraslado);            
    }

    public RutaEscogida(ArrayList<Ruta> rutas,int tiempoRuta){
        this.listaRutaEscogida=rutas;
        this.tiempoRuta=tiempoRuta;
    }

    public RutaEscogida(RutaEscogida r){
        this.listaRutaEscogida= new ArrayList<Ruta>(r.listaRutaEscogida);
        this.tiempoRuta = r.tiempoRuta;
        this.tiemposEspera = new ArrayList<Integer>(r.tiemposEspera);
        this.tiemposTraslado = new ArrayList<Integer>(r.tiemposTraslado);
        this.estadoRuta=r.estadoRuta;
    }


    /**
     * @return the tiempoRuta
     */
    public int getTiempoRuta() {
        return tiempoRuta;
    }

    /**
     * @param tiempoRuta the tiempoRuta to set
     */
    public void setTiempoRuta(int tiempoRuta) {
        this.tiempoRuta = tiempoRuta;
    }

    public void agregarRuta(Ruta newRuta){
        this.getListaRutaEscogida().add(newRuta);
    }

    public void agregarTiempoTraslado(int tiempoTraslado){
        this.getTiemposTraslado().add(tiempoTraslado);
    }

    public void agregarTiempoEspera(int tiempoEspera){
        this.getTiemposEspera().add(tiempoEspera);
    }

    /**
     * @return the tiemposEspera
     */
    public ArrayList<Integer> getTiemposEspera() {
        return tiemposEspera;
    }

    /**
     * @return the tiemposTraslado
     */
    public ArrayList<Integer> getTiemposTraslado() {
        return tiemposTraslado;
    }

    /**
     * @return the listaRutaEscogida
     */
    public ArrayList<Ruta> getListaRutaEscogida() {
        return listaRutaEscogida;
    }

    public void actualizarTiempoRuta(int tiempoTraslado,int tiempoEspera){
        this.tiempoRuta+=tiempoTraslado+tiempoEspera;
    }

    public String imprimirRecorrido(){
        try{
            String cadena=this.listaRutaEscogida.get(0).getCiudadOrigen();//Se concatena la primera ciudad
            for(Ruta r : this.listaRutaEscogida){
                cadena+="-"+r.getCiudadFin();
            }       
            return cadena;
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Method Halted!, continuing doing the next thing");
        }            
        return null;
    }

    /**
     * @return the estadoRuta
     */
    public int getEstadoRuta() {
        return estadoRuta;
    }

    /**
     * @param estadoRuta the estadoRuta to set
     */
    public void setEstadoRuta(int estadoRuta) {
        this.estadoRuta = estadoRuta;
    }
}
