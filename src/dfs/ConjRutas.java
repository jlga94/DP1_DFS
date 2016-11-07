/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dfs;

import java.util.ArrayList;

/**
 *
 * @author GUERRA
 */
public class ConjRutas {
    public ArrayList<Ruta> vuelos = new ArrayList<>();
    public int tiempo=0;
    ConjRutas(){
        
    }
    ConjRutas(Ruta vuel,int tiemp){
        vuelos.add(vuel);
        tiempo=tiemp;
    }    
    
    ConjRutas(Ruta vuel1, Ruta vuel2,int tiemp){
        vuelos.add(vuel1);
        vuelos.add(vuel2);
        tiempo=tiemp;
    }       
    public void print(){
        //for(int i=0;i<vuelos.size();i++){
            //System.out.print(vuelos.get(i).getAeroOrig().getCodAeropuerto()+"-"+
              //              vuelos.get(i).getAeroFin().getCodAeropuerto()+"/");
        //}
        System.out.println(" Tiempo: "+tiempo);
    }    
}
