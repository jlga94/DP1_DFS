/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dfs;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.TreeMap;

/**
 *
 * @author JoseLuis
 */
public class DFS {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws FileNotFoundException {
        GestorCiudades gestor= new GestorCiudades("Extras/_plan_vuelo.txt", "Extras/_aeropuertos.OACI.txt", "Extras/_husos_horarios.txt");
        gestor.imprimirCiudades();
        gestor.asignarPedidos("Extras/_pedidos.txt");
    }
    
}
