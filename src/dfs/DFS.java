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
        //gestor.lineaInicial();
        /*Gson gson = new Gson();
        GestorCiudades temporal= new GestorCiudades();
        try (Reader reader = new FileReader("staff.json")) {

	
            
            temporal=gson.fromJson(reader, GestorCiudades.class);
            

        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //gestor.lineaInicial();
        //gestor.asignarPedidos();        
        //gestor.imprimirCiudades();
        
        
        Gson gson = new Gson();

        try (FileWriter writer = new FileWriter("gestor.json")) {
            gson.toJson(gestor,writer);
        } catch(IOException e){
            e.printStackTrace();
        }
        
        
        
    }
    
}
