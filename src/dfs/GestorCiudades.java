/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dfs;

import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author JoseLuis
 */
public class GestorCiudades {
    private TreeMap<String,Ciudad> ciudades=new TreeMap<String,Ciudad>();//MAP KEY-Codigo Ciudad y el VALUE-Objeto Ciudad
    private int maxCapacidadCiudades=500;
    private int maxCapacidadesVuelos=50;
    private int maxTiempoContinental=24;
    private int maxTiempoIntercontinental=48;
    private Random rnd;
    private int TiempoEntregaPaquetes=0;
    
    
    private void leerCiudades(String archAeropuertos,String archHusos) throws FileNotFoundException{
        BufferedReader brAeropuertos = new BufferedReader(new FileReader(archAeropuertos));
        BufferedReader brHusos = new BufferedReader(new FileReader(archHusos));
        String linea;
        String continente="";
        try{
            while((linea=brAeropuertos.readLine()) != null){
                String [] datos=linea.trim().split("\t");
                if (datos.length<5){
                    continente=linea;
                }else{
                    String codigo=datos[1];
                    Ciudad newCiudad= new Ciudad(datos[0],codigo,datos[2],datos[3],continente);
                    ciudades.put(codigo, newCiudad);
                }
            }
            brAeropuertos.close();
            while((linea=brHusos.readLine()) != null){
                String [] datos=linea.trim().split("\t");
                String codigo=datos[1];
                int huso_horario=Integer.parseInt(datos[2]);
                Ciudad ciudadBuscada=ciudades.get(codigo);
                ciudadBuscada.setHuso_horario(huso_horario);
            }
            brHusos.close();
            
        }catch (IOException e)
        {
            System.err.println(e.toString());
        }
        finally
        {
            try
            {
                brAeropuertos.close();
            }
            catch (IOException ex)
            {
                System.err.println(ex.toString());
            }
        } 
    }
    
    private void leerRutas(String archRutas) throws FileNotFoundException{
        BufferedReader brRutas = new BufferedReader(new FileReader(archRutas));
        String linea;
        try{
            while((linea=brRutas.readLine()) != null){
                String [] datos=linea.trim().split("-");
                String ciudadO=datos[0];
                String ciudadF=datos[1];
                String horaO=datos[2];
                String horaF=datos[3];
                
                Ruta newRuta=new Ruta(ciudadO,ciudadF,horaO,horaF);
                Ciudad ciudadOrigen=ciudades.get(ciudadO);
                ciudadOrigen.agregarRuta(newRuta);
            }
        }catch (IOException e)
        {
            System.err.println(e.toString());
        }
        finally
        {
            try
            {
                brRutas.close();
            }
            catch (IOException ex)
            {
                System.err.println(ex.toString());
            }
        } 
        
    }
    
    public GestorCiudades(String archVuelos,String archAeropuertos,String archHusos) throws FileNotFoundException{
        leerCiudades(archAeropuertos,archHusos);
        leerRutas(archVuelos);
        this.rnd=new Random();
    }
    
    public void asignarPedidos(String archPedidos)throws FileNotFoundException{
        BufferedReader brPedidos = new BufferedReader(new FileReader(archPedidos));
        String linea;
        int horaPedido=9;
        try{
            int numPedido=1;
            while((linea=brPedidos.readLine()) != null){
                String [] datos=linea.trim().split("-");
                String codCiudadO=datos[0];
                String codCiudadF=datos[1];
                int cantPaquetes=Integer.parseInt(datos[2]);
                
                DFS(codCiudadO,codCiudadF,numPedido,horaPedido,cantPaquetes);
                
                numPedido++;
            }
            System.out.println("Tiempo Total por paquetes: "+this.TiempoEntregaPaquetes);
        }catch (IOException e)
        {
            System.err.println(e.toString());
        }
        finally
        {
            try
            {
                brPedidos.close();
            }
            catch (IOException ex)
            {
                System.err.println(ex.toString());
            }
        } 
    }
    
    private class Tupla{
        private String recorridoRuta;
        private int tiempoRuta;
        
        public Tupla(String recorridoRuta,int tiempoRuta){
            this.recorridoRuta=recorridoRuta;
            this.tiempoRuta=tiempoRuta;
        }

        /**
         * @return the ruta
         */
        public String getRuta() {
            return recorridoRuta;
        }

        /**
         * @param ruta the ruta to set
         */
        public void setRuta(String ruta) {
            this.recorridoRuta = ruta;
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
    
    }
    
    
    private void DFS(String codCiudadO,String codCiudadF,int numPedido,int horaPedido,int cantPaquetes){
        Ciudad ciudadO=ciudades.get(codCiudadO);
        Ciudad ciudadF=ciudades.get(codCiudadF);
        int maxTiempoVuelo;
        if(ciudadO.getContinente().equals(ciudadF.getContinente()))
            maxTiempoVuelo=maxTiempoContinental;
        else
            maxTiempoVuelo=maxTiempoIntercontinental;
        
        String mejorRuta="";
        int mejorTiempo=10000;//Numero alto para que sea reemplazado en le primera iteración
        
        ArrayList<Ruta> RutasAnexadasO=ciudadO.getRutasAnexas();
        int cantidadAnexos=RutasAnexadasO.size();
        int [] anexosRevisados=new int[cantidadAnexos];
        int cantAnexosRevisados=0;
        int encontroAlMenosUno=0; 
        int porcCantAnexos=cantidadAnexos/4;//PORCENTAJE DE LAS RUTAS QUE SE ESTÁN EVALUANDO
        //for(Ruta rutaActual: RutasAnexadasO){
        while(cantAnexosRevisados<cantidadAnexos && encontroAlMenosUno==0){
            int cantPorcAnexosRevisados=porcCantAnexos;
            while(cantPorcAnexosRevisados>0 && cantAnexosRevisados<cantidadAnexos){
                int indRutaARevisar=rnd.nextInt(cantidadAnexos);
                //int indRutaARevisar=(int) ((int)cantidadAnexos*Math.random());
                if(anexosRevisados[indRutaARevisar]==0){
                    Ruta rutaActual=RutasAnexadasO.get(indRutaARevisar);

                    Tupla resultadoRuta=recursiveSearch(rutaActual,0,maxTiempoVuelo,codCiudadF,codCiudadO,horaPedido);
                    if(resultadoRuta!=null){
                        int tiempoRuta=resultadoRuta.getTiempoRuta();
                        //System.out.println("Ruta: "+resultadoRuta.getRuta()+" Tiempo: "+tiempoRuta);
                        if(tiempoRuta<mejorTiempo){
                            mejorTiempo=tiempoRuta;
                            mejorRuta=resultadoRuta.getRuta();
                        }       
                        encontroAlMenosUno=1;//Se encontro una ruta disponible
                    }
                    anexosRevisados[indRutaARevisar]=1;
                    cantPorcAnexosRevisados--;
                    cantAnexosRevisados++;
                }
            }
        }
        if(mejorRuta.equals(""))
            System.out.println("Numero de Pedido: "+numPedido+" No se encontró ruta");
        else{
            System.out.println("Numero de Pedido: "+numPedido+ " Mejor Ruta: "+mejorRuta+" Mejor Tiempo: "+mejorTiempo);
            agregarPaquetes_Ciudades(mejorRuta,cantPaquetes);
            this.TiempoEntregaPaquetes+=mejorTiempo*cantPaquetes;
        }
    } 
    
    private void agregarPaquetes_Ciudades(String rutaEscogida,int cantPaquetesEnviados){
        String[] ciudadesTranscurridas=rutaEscogida.trim().split("-");
        for(String codCiudadActual:ciudadesTranscurridas){
            Ciudad ciudadActual=ciudades.get(codCiudadActual);
            int cantPaquetesActual=ciudadActual.getCantPaquetes();
            ciudadActual.setCantPaquetes(cantPaquetesActual+cantPaquetesEnviados);
        }
        
    }
    
    private Tupla recursiveSearch(Ruta rutaActual,int tiempoTotalActual,int maxTiempoVuelo,String ciudadFinal,String seguimientoRuta,int horaPartida){
        
        int tiempoTraslado_Espera=calcularTiempo(rutaActual,horaPartida);
        int tiempoTotalActualizado=tiempoTotalActual+tiempoTraslado_Espera;
        
        if(tiempoTotalActualizado>maxTiempoVuelo){
            return null;
        }else{
            if(rutaActual.getCiudadFin().equals(ciudadFinal)){
                Tupla resultado= new Tupla(seguimientoRuta+"-"+rutaActual.getCiudadFin(),tiempoTotalActualizado);
                return resultado;
            }
            
            if(tiempoTotalActualizado==maxTiempoVuelo)//Si ya llego al tope, no vale la pena seguir buscando caminos
                return null;
        }
        
        
        Ciudad ciudadNuevaPartida=ciudades.get(rutaActual.getCiudadFin());
        String[] hhmm=rutaActual.getHoraFin().trim().split(":");        
        horaPartida=Integer.parseInt(hhmm[0]);
        
        
        String mejorRuta="";
        int mejorTiempo=10000;
        ArrayList<Ruta> RutasAnexadasNuevaPartida=ciudadNuevaPartida.getRutasAnexas();
        for(Ruta rutaNueva: RutasAnexadasNuevaPartida){
            
            if(rutaNueva.getCiudadFin().equals(rutaActual.getCiudadOrigen())){//Se está volviendo al mismo punto
                continue;
            }
            
            Tupla resultadoRuta=recursiveSearch(rutaNueva,tiempoTotalActualizado,maxTiempoVuelo,ciudadFinal,seguimientoRuta+"-"+rutaNueva.getCiudadOrigen(),horaPartida);
            if(resultadoRuta!=null){
                if(resultadoRuta.getTiempoRuta()<mejorTiempo){
                    mejorTiempo=resultadoRuta.getTiempoRuta();
                    mejorRuta=resultadoRuta.getRuta();
                }                
            }
            
        }
        if(mejorRuta.equals(""))
            return null;
        else{
            Tupla resultado= new Tupla(mejorRuta,mejorTiempo);
            return resultado;
        }
    }
    
    private int calcularTiempo(Ruta rutaActual,int horaPartida){
        String[] hhmm=rutaActual.getHoraOrigen().trim().split(":");
        int hhOrigen=Integer.parseInt(hhmm[0]);
        int tiempoEspera;
        if(horaPartida<=hhOrigen)
            tiempoEspera=hhOrigen-horaPartida;
        else
            tiempoEspera=24-(horaPartida-hhOrigen);
        
        int tiempoTraslado;
        
        Ciudad ciudadI=ciudades.get(rutaActual.getCiudadOrigen());
        Ciudad ciudadF=ciudades.get(rutaActual.getCiudadFin());
        if(ciudadI.getContinente().equals(ciudadF.getContinente()))
            tiempoTraslado=12;
        else
            tiempoTraslado=24;
                    
        return tiempoEspera+tiempoTraslado;
    }
    
    
    public void imprimirCiudades(){
        Set set = ciudades.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            System.out.print(me.getKey() + ": ");
            Ciudad ciudadActual=(Ciudad)me.getValue();

            System.out.println("Nombre:"+ciudadActual.getNombre()+" Cantidad Paquetes: "+ciudadActual.getCantPaquetes());
            
        }
    }
    
    
    
}
