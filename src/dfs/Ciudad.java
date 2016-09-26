/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dfs;

import java.util.ArrayList;

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
    private ArrayList<Ruta> rutasAnexas=new ArrayList<Ruta>();

    public Ciudad(String id,String codigo,String nombre,String pais,String continente){
        this.Id=Integer.parseInt(id);
        this.codigo=codigo;
        this.nombre=nombre;
        this.pais=pais;
        this.Continente=continente;
    }
    
    /**
     * @return the Id
     */
    public int getId() {
        return Id;
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
    
}
