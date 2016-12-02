/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dfs;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;



/**
 *
 * @author JoseLuis
 */
public class DFS {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        

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
        GestorCiudades gestor= new GestorCiudades("Extras/plan_vuelo.txt", "Extras/_aeropuertos.OACI.txt", "Extras/_husos_horarios.txt");
        gestor.lineaInicial();
        //gestor.asignarPedidos("Extras/_pedidos_N.txt");
        Gson gson = new Gson();
        BufferArchivos temporal=new BufferArchivos();
        try (Reader reader = new FileReader("DataGenerada/pedidosArutear.json")) {

            temporal=gson.fromJson(reader, BufferArchivos.class);
            

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        gestor.asignarPedidos(temporal);

        /*try (FileWriter writer = new FileWriter("gestor.json")) {
            gson.toJson(gestor,writer);
        } catch(IOException e){
            e.printStackTrace();
        }*/
        
        
        
    }
    
}
