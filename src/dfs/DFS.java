/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dfs;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;



/**
 *
 * @author JoseLuis
 */
public class DFS {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        
        GestorCiudades gestor= new GestorCiudades("Extras/plan_vuelo.txt", "Extras/_aeropuertos.OACI.txt", "Extras/_husos_horarios.txt");
        gestor.lineaInicial();
        /*Gson gson = new Gson();
        GestorCiudades temporal= new GestorCiudades();
        try (Reader reader = new FileReader("staff.json")) {

	
            
            temporal=gson.fromJson(reader, GestorCiudades.class);
            

        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //gestor.lineaInicial();
        gestor.asignarPedidos();        
        /*Set set = gestor.getCiudades().entrySet();
        Iterator i = set.iterator();
        //FileWriter file=new FileWriter("archivo.txt");
        JSONObject obj=new JSONObject();
        JSONArray cities=new JSONArray();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Ciudad ciudadActual=(Ciudad)me.getValue();
            
            JSONArray ciudad=new JSONArray();
            ciudad.add(ciudadActual.getId());
            ciudad.add(ciudadActual.getCodigo());
            ciudad.add(ciudadActual.getNombre());
            ciudad.add(ciudadActual.getPais());
            ciudad.add(ciudadActual.getContinente());
            ciudad.add(ciudadActual.getHuso_horario());
            ciudad.add(ciudadActual.getCantPaquetes());
            
            JSONArray rutas=new JSONArray();
            
            ArrayList<Ruta> rutasAnexas= ciudadActual.getRutasAnexas();
            for(Ruta rutaNueva: rutasAnexas){
                rutas.add(rutaNueva.getCiudadOrigen());
                rutas.add(rutaNueva.getCiudadFin());
                rutas.add(rutaNueva.getHoraOrigen());
                rutas.add(rutaNueva.getHoraFin());
                rutas.add(rutaNueva.horaO);
                rutas.add(rutaNueva.horaF);
                rutas.add(rutaNueva.getTiempo());
                
                JSONArray cantidadDePaquetesPorDia=new JSONArray();
                for(int j=0;j<7;j++)cantidadDePaquetesPorDia.add(rutaNueva.cantidadPaquetesXDia[j]);
                
                rutas.add(cantidadDePaquetesPorDia);
                
            }
            ciudad.add(rutas);
            
            TreeMap almacen= ciudadActual.proyeccionAlmacen;
            
            JSONArray CapacidadAlmacenSemana=new JSONArray();
            for(int x=0;x<7;++x){
                TreeMap temporal=(TreeMap) almacen.get(x);
                JSONArray CapacidadDia = new JSONArray();
                
                for(int j=0;j<24;++j){//24 HORAS
                    CapacidadDia.add(temporal.get(j*100));
                    CapacidadDia.add(temporal.get(j*100+1));
                }
                CapacidadAlmacenSemana.add(CapacidadDia);
            }
            ciudad.add(CapacidadAlmacenSemana);
            
            ciudad.add(ciudadActual.getCantVisitadasRutasAnexas());
            
            HashMap<String,ArrayList<ConjRutas>> rutasPorDestino=ciudadActual.rutasXDestino;
            
            JSONArray rutasPorDestinoJSONArray=new JSONArray();
            
            for(String llave : rutasPorDestino.keySet()){
                JSONArray supTemporal=new JSONArray();
                ArrayList<ConjRutas> value=rutasPorDestino.get(llave);
                JSONArray superSupremoTemporal=new JSONArray();
                for(ConjRutas supremoTemporal : value){
                    for(Ruta minisupremo : supremoTemporal.vuelos){
                        
                    }
                    superSupremoTemporal.add(supremoTemporal.vuelos);
                    superSupremoTemporal.add(supremoTemporal.tiempo);
                }
                supTemporal.add(llave);
                supTemporal.add(superSupremoTemporal);
                rutasPorDestinoJSONArray.add(supTemporal);
            }
            
            ciudad.add(rutasPorDestinoJSONArray);
            
            //ciudad.add(ciudadActual.rutasXDestino);
            
            cities.add(ciudad);
        }
        obj.put("ciudades",cities);
        try(FileWriter file=new FileWriter("archivo.txt")){
                file.write(obj.toJSONString());
        }*/
        //gestor.asignarPedidos();
        //gestor.imprimirCiudades();
        
        
        /*Gson gson = new Gson();

        try (FileWriter writer = new FileWriter("staff.json")) {
            gson.toJson(gestor,writer);
        } catch(IOException e){
            e.printStackTrace();
        }*/
        
        
        
    }
    
}
