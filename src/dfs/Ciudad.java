/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javafx.util.Pair;

/**
 *
 * @author JoseLuis
 */
public class Ciudad {
    private int Id;
    private String codigo;
    private String nombre;
    private String pais;
    private String Continente;
    private int huso_horario;
    private int cantPaquetes;
    public ArrayList<Ruta> rutasAnexas=new ArrayList<Ruta>();
    public TreeMap proyeccionAlmacen = new TreeMap();
    private ArrayList<Integer> cantVisitadasRutasAnexas=new ArrayList<Integer>();
    public HashMap<String,ArrayList<ConjRutas>> rutas = new HashMap<>(); // llave es el codigo de la ciudad destino, valor es las rutas posibles

    public Ciudad(String id,String codigo,String nombre,String pais,String continente){
        this.Id=Integer.parseInt(id);
        this.codigo=codigo;
        this.nombre=nombre;
        this.pais=pais;
        this.Continente=continente;
        this.cantPaquetes=0;
        for(int i=0;i<7;i++){
            proyeccionAlmacen.put(i, new TreeMap());
            TreeMap temp=(TreeMap)proyeccionAlmacen.get(i);
            for(int j=0;j<24;++j){
                temp.put(j*100, 0);
                temp.put(j*100+1, 0);
            }
        }
    }
    
    /**
     * @return the Id
     */
    public int getId() {
        return Id;
    }
    
    public void print(){
        System.out.println("========"+codigo+"========");
        Set set = rutas.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            System.out.println(me.getKey() + ": ");
            ArrayList<ConjRutas> conjRuta=(ArrayList<ConjRutas>)me.getValue();
            for(int j=0;j<conjRuta.size();j++){
                ConjRutas conj=conjRuta.get(j);
                ArrayList<Ruta> vuelos=conj.vuelos;
                for(int h=0;h<vuelos.size();h++){
                    System.out.print(vuelos.get(h).getCiudadOrigen()+"-"+vuelos.get(h).getCiudadFin());                  
                }
                System.out.println();
            }
        }    
    }
    /**
     * @param Id the Id to set
     */
    public void setId(int Id) {
        this.Id = Id;
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the pais
     */
    public String getPais() {
        return pais;
    }

    /**
     * @param pais the pais to set
     */
    public void setPais(String pais) {
        this.pais = pais;
    }

    /**
     * @return the Continente
     */
    public String getContinente() {
        return Continente;
    }

    /**
     * @param Continente the Continente to set
     */
    public void setContinente(String Continente) {
        this.Continente = Continente;
    }

    /**
     * @return the huso_horario
     */
    public int getHuso_horario() {
        return huso_horario;
    }

    /**
     * @param huso_horario the huso_horario to set
     */
    public void setHuso_horario(int huso_horario) {
        this.huso_horario = huso_horario;
    }

    /**
     * @return the rutasAnexas
     */
    public ArrayList getRutasAnexas() {
        return rutasAnexas;
    }

    /**
     * @param rutasAnexas the rutasAnexas to set
     */
    public void setRutasAnexas(ArrayList rutasAnexas) {
        this.rutasAnexas = rutasAnexas;
    }
    
    public void agregarRuta(Ruta newruta){
        this.rutasAnexas.add(newruta);
    }

    /**
     * @return the cantPaquetes
     */
    public int getCantPaquetes() {
        return cantPaquetes;
    }

    /**
     * @param cantPaquetes the cantPaquetes to set
     */
    public void setCantPaquetes(int cantPaquetes) {
        this.cantPaquetes = cantPaquetes;
    }
    
    public void instanciarCantidadIdasRutasAnexas(){
        for(int i=0;i<this.rutasAnexas.size();i++){
            this.cantVisitadasRutasAnexas.add(1);//Se instancian con 1 para empezas con probabilidad 1
        }
        
    }

    public void incrementarRutaEscogida(int indiceRuta){
        int cant=cantVisitadasRutasAnexas.get(indiceRuta);
        cantVisitadasRutasAnexas.set(indiceRuta, cant+1);
    }
    
    public void disminuirOtrasRutas(int indiceRuta){
        for(int i=0;i<this.rutasAnexas.size();i++){
            if(i!=indiceRuta){
                int cant=cantVisitadasRutasAnexas.get(i);
                if(cant>1)
                    cantVisitadasRutasAnexas.set(i,cant-1);
            }
        }
    }
    
    
    /**
     * @return the cantVisitadasRutasAnexas
     */
    public ArrayList<Integer> getCantVisitadasRutasAnexas() {
        return cantVisitadasRutasAnexas;
    }
    
}
