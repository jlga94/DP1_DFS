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
    }
    
    public void asignarPedidos(String archPedidos)throws FileNotFoundException{
        BufferedReader brPedidos = new BufferedReader(new FileReader(archPedidos));
        String linea;
        try{
            int numPedido=1;
            while((linea=brPedidos.readLine()) != null){
                String [] datos=linea.trim().split("-");
                String codCiudadO=datos[0];
                String codCiudadF=datos[1];
                
                DFS(codCiudadO,codCiudadF,numPedido);
                
                numPedido++;
            }
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
    
    private void DFS(String codCiudadO,String codCiudadF,int numPedido){
        Ciudad ciudadO=ciudades.get(codCiudadO);
        
        
    } 
    
    
    public void imprimirCiudades(){
        Set set = ciudades.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
        Map.Entry me = (Map.Entry)i.next();
        System.out.print(me.getKey() + ": ");
        Ciudad ciudadActual=(Ciudad)me.getValue();
        
        System.out.println("Nombre:"+ciudadActual.getNombre()+" Continente: "+ciudadActual.getContinente());
      }
    }
    
}
