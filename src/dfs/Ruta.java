/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dfs;

import java.text.DateFormat;
import javax.print.attribute.standard.DateTimeAtCompleted;

/**
 *
 * @author JoseLuis
 */
public class Ruta {
    private String ciudadOrigen;
    private String ciudadFin;
    private String horaOrigen;
    private String horaFin;
    public int horaO;
    public int horaF;
    private int tiempo;
    public int[] cantidadPaquetesXDia = new int[7]; //Cantidad de paquetes que hay en el dia, será tratado como un arreglo circular
    
    

    public Ruta(String ciudadOrigen,String ciudadFin,String horaOrigen,String horaFin){
        this.ciudadFin=ciudadFin;
        this.ciudadOrigen=ciudadOrigen;
        this.horaOrigen=horaOrigen;
        this.horaFin=horaFin;
    }
    
    /**
     * @return the ciudadOrigen
     */
    public String getCiudadOrigen() {
        return ciudadOrigen;
    }

    /**
     * @param ciudadOrigen the ciudadOrigen to set
     */
    public void setCiudadOrigen(String ciudadOrigen) {
        this.ciudadOrigen = ciudadOrigen;
    }

    /**
     * @return the ciudadFin
     */
    public String getCiudadFin() {
        return ciudadFin;
    }

    /**
     * @param ciudadFin the ciudadFin to set
     */
    public void setCiudadFin(String ciudadFin) {
        this.ciudadFin = ciudadFin;
    }

    /**
     * @return the horaOrigen
     */
    public String getHoraOrigen() {
        return horaOrigen;
    }

    /**
     * @param horaOrigen the horaOrigen to set
     */
    public void setHoraOrigen(String horaOrigen) {
        this.horaOrigen = horaOrigen;
    }

    /**
     * @return the horaFin
     */
    public String getHoraFin() {
        return horaFin;
    }

    /**
     * @param horaFin the horaFin to set
     */
    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    /**
     * @return the tiempo
     */
    public int getTiempo() {
        return tiempo;
    }

    /**
     * @param tiempo the tiempo to set
     */
    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }
    
    public int getIndCapacidad(int indice){
        return this.cantidadPaquetesXDia[indice];
    }
    
    public void setIndCapacidad(int indice,int capacidadIndice){
        this.cantidadPaquetesXDia[indice]=capacidadIndice;
    }
    
}
