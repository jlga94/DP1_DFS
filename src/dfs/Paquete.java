/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dfs;

/**
 *
 * @author Sebastián
 */
public class Paquete{
    private String id;
    private String fecha;//de llegada  
    private String hora;//de llegada
    private String origen;
    private String destino;
    private RutaEscogida ruta;
    
    

    public Paquete(String ID, String FECHA, String HORA, String ORIGEN, String DESTINO, RutaEscogida RUTA)
    {
        this.id = ID;
        this.fecha = FECHA;
        this.hora = HORA;
        this.origen = ORIGEN;
        this.destino = DESTINO;
        this.ruta=RUTA;
    }
    
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the hora
     */
    public String getHora() {
        return hora;
    }

    /**
     * @param hora the hora to set
     */
    public void setHora(String hora) {
        this.hora = hora;
    }

    /**
     * @return the origen
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * @param origen the origen to set
     */
    public void setOrigen(String origen) {
        this.origen = origen;
    }

    /**
     * @return the destino
     */
    public String getDestino() {
        return destino;
    }

    /**
     * @param destino the destino to set
     */
    public void setDestino(String destino) {
        this.destino = destino;
    }

    /**
     * @return the ruta
     */
    public RutaEscogida getRuta() {
        return ruta;
    }

    /**
     * @param ruta the ruta to set
     */
    public void setRuta(RutaEscogida ruta) {
        this.ruta = ruta;
    }
}
