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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
/**
 *
 * @author JoseLuis
 */
public class GestorCiudades {
    private TreeMap<String,Ciudad> ciudades=new TreeMap<String,Ciudad>();//MAP KEY-Codigo Ciudad y el VALUE-Objeto Ciudad
    private int maxCapacidadCiudades=500000;
    private int maxCapacidadesVuelos=500000;
    private int maxTiempoContinental=24;
    private int maxTiempoIntercontinental=48;
    private int porcentajeEvaluacion=1;
    private int estadoRutaFactible=0;
    private int estadoRutaXTiempo=1;
    private int estadoRutaXCapacidadAlmacen=2;
    private int estadoRutaXCapacidadVuelo=3;

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

                String[] hhmm=horaO.split(":");        
                int horaPartida=Integer.parseInt(hhmm[0]);
                String[] hhmm2=horaF.split(":");        
                int horaFin=Integer.parseInt(hhmm2[0]);
                
                Ruta newRuta=new Ruta(ciudadO,ciudadF,horaO,horaF);
                newRuta.horaF=horaFin;
                newRuta.horaO=horaPartida;
                if(ciudades.get(ciudadO).getContinente().equals(ciudades.get(ciudadF).getContinente())) newRuta.setTiempo(12);
                else newRuta.setTiempo(24);
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
    
    private void instanciarVecesRecorridasCiudades(){
        Set set = ciudades.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Ciudad ciudadActual=(Ciudad)me.getValue();
            ciudadActual.instanciarCantidadIdasRutasAnexas();
            /*
            Set set2 = ciudades.entrySet();
            Iterator i2 = set2.iterator();
            while(i2.hasNext()) {
                Map.Entry me2 = (Map.Entry)i.next();
                Ciudad ciudadBuscada=(Ciudad)me2.getValue();
                System.out.println("CIUDAD INICIO: "+ciudadActual.getCodigo() + " -CIUDAD FIN: "+ciudadBuscada.getCodigo());
                if(ciudadActual.getCodigo().equals(ciudadBuscada.getCodigo()))
                    continue;
                ciudadActual.instanciarCantVisitadasrutasXDestino(ciudadBuscada.getCodigo());
            }
            */
        }
    }
    
    public GestorCiudades(String archVuelos,String archAeropuertos,String archHusos) throws FileNotFoundException{
        leerCiudades(archAeropuertos,archHusos);
        leerRutas(archVuelos);
        generarConjRutas();//generar todas las rutasXDestino posibles
        instanciarVecesRecorridasCiudades();
        
        //UTILIZAR ALGORITMO DE TODAS LAS CIUDADES A TODAS LAS CIUDADES
        
        this.rnd=new Random();
    }
    public void generarConjRutas(){
        int tEspera;
        int tiempoRuta;
        for(Ciudad ciudad : ciudades.values()) {
            ArrayList<Ruta>vuelos = ciudad.rutasAnexas;
            for(int i=0;i<vuelos.size();i++){
                String destino=vuelos.get(i).getCiudadFin();
                if(!ciudad.rutasXDestino.containsKey(destino)){ // si todavia no tiene ninguna ruta ese destino
                    ArrayList<ConjRutas> rutas= new ArrayList<>();
                    rutas.add(new ConjRutas(vuelos.get(i),vuelos.get(i).getTiempo()));
                    ciudad.rutasXDestino.put(destino, rutas);                    
                }
                else{
                    ArrayList<ConjRutas> rutas=ciudad.rutasXDestino.get(destino);
                    rutas.add(new ConjRutas(vuelos.get(i),vuelos.get(i).getTiempo()));
                    ciudad.rutasXDestino.put(destino,rutas);    
                }
                
                // caso con Escala
                String ciudInter=vuelos.get(i).getCiudadFin();
                Ciudad ciudadIntermedia=ciudades.get(ciudInter);
                for(int j=0;j<ciudadIntermedia.rutasAnexas.size();j++){
                    Ruta vuelo2=ciudadIntermedia.rutasAnexas.get(j);
                    String destino2=vuelo2.getCiudadFin();
                    if(ciudad.getContinente().equals(ciudades.get(destino2).getContinente())) continue;
                    tEspera=vuelo2.horaO-vuelos.get(i).horaF;
                    if(tEspera<0)tEspera+=24;
                    tiempoRuta=vuelos.get(i).getTiempo()+vuelo2.getTiempo()+tEspera;
                    if(tiempoRuta>48) continue; // si se demora más de 48 horas, no tomar en cuenta
                    if(!ciudad.rutasXDestino.containsKey(destino2)){ // si todavia no tiene ninguna ruta ese destino
                        ArrayList<ConjRutas> rutas= new ArrayList<>();
                        ConjRutas ruta=new ConjRutas(vuelos.get(i),vuelo2,tiempoRuta);
                        rutas.add(ruta);
                        ciudad.rutasXDestino.put(destino2, rutas);                    
                    }
                    else{
                        ArrayList<ConjRutas> rutas=ciudad.rutasXDestino.get(destino2);
                        ConjRutas ruta=new ConjRutas(vuelos.get(i),vuelo2,tiempoRuta);
                        rutas.add(ruta);
                        ciudad.rutasXDestino.put(destino2,rutas);    
                    }
                }
            }
        }          
    }
    
    
    
    private void limpiarCapacidad_Almacenes_Rutas(String fechaActual){

        if(fechaActual.equals(""))return;
        
        Calendar c=Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("dd/M/yyyy").parse(fechaActual));
        } catch (ParseException ex) {
            Logger.getLogger(DFS.class.getName()).log(Level.SEVERE, null, ex);
        }
        int dayweek=c.get(Calendar.DAY_OF_WEEK)-1;//porque la semana comienza el domingo y el arreglo del 0-6
        
        //recorremos cada ciudad para limpiar las proyecciones una vez cambiado el dia
        Set set = ciudades.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Ciudad ciudadActual=(Ciudad)me.getValue();
            
            TreeMap proyecciones = (TreeMap)ciudadActual.proyeccionAlmacen.get(dayweek);
            if(proyecciones==null)System.out.println(dayweek);
            for(int j=0;j<24;++j){//24 HORAS
                proyecciones.put(j*100, 0);
                proyecciones.put(j*100+1, 0);
            }
            
            ArrayList<Ruta> rutasAnexas = ciudadActual.getRutasAnexas();//Se deben limpiar la capacidad del dia en la ruta
            for(Ruta r: rutasAnexas){
                r.setIndCapacidad(dayweek, 0);
            }
        }
    }
    
    public void asignarPedidos()throws FileNotFoundException{

        int numPedido=1;
        Ciudad siguienteCiudad;
        String fechaActual="";
        while((siguienteCiudad=siguienteEnvio()) != null){            
            
            if(!siguienteCiudad.getUltimaFecha().equals(fechaActual)){//Se limpia el dia si ha cambiado de todos los almacenes
                limpiarCapacidad_Almacenes_Rutas(fechaActual);
                fechaActual=siguienteCiudad.getUltimaFecha();
            }
            DFS(siguienteCiudad.getCodigo(),siguienteCiudad.getUltimoDestino(),numPedido,siguienteCiudad.getUltimaHora(),1,siguienteCiudad.getUltimaFecha());
            
            siguienteCiudad.avanzarBuffer();
            
            numPedido++;
        }
        System.out.println("Tiempo Total por paquetes: "+this.TiempoEntregaPaquetes);
        
    }
    
    public void asignarPedidos(String archPedidos)throws FileNotFoundException{
        BufferedReader brPedidos = new BufferedReader(new FileReader(archPedidos));
        String linea;
        //int horaPedido=9;
        String fechaActual="";
        try{
            int numPedido=1;
            while((linea=brPedidos.readLine()) != null){
                String [] datos=linea.trim().split("-");
                String codCiudadO=datos[0];
                String codCiudadF=datos[1];
                int cantPaquetes=Integer.parseInt(datos[2]);
                String horaPedido=datos[3];               
                String fechaPedido=datos[4];
                
                if(!fechaPedido.equals(fechaActual)){//Se limpia el dia si ha cambiado de todos los almacenes
                    limpiarCapacidad_Almacenes_Rutas(fechaActual);
                    fechaActual=fechaPedido;
                }
                DFS(codCiudadO,codCiudadF,numPedido,horaPedido,cantPaquetes,fechaPedido);
                
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
        
    private void DFS(String codCiudadO,String codCiudadF,int numPedido, String horaPedido,int cantPaquetes,String fechaPedido){
        Ciudad ciudadO=ciudades.get(codCiudadO);
        Ciudad ciudadF=ciudades.get(codCiudadF);
        int maxTiempoVuelo;
        
        //Establece el tiempo maximo de vuelo
        if(ciudadO.getContinente().equals(ciudadF.getContinente()))
            maxTiempoVuelo=maxTiempoContinental;
        else
            maxTiempoVuelo=maxTiempoIntercontinental;
        
        
        RutaEscogida mejorRuta= new RutaEscogida(1000);//Numero alto para que sea reemplazado en le primera iteración
        int indiceRutaEscogida=-1;
        
        ArrayList<Ruta> RutasAnexadasO=ciudadO.getRutasAnexas(); //CAMBIAR A LAS RUTAS QUE PUEDAN SER FACTIBLES        
        //ArrayList<ConjRutas> listaRutasFactibles=(ArrayList<ConjRutas>) ciudadO.rutasXDestino.get(codCiudadF);
        
               
        int cantidadAnexos=RutasAnexadasO.size();        
        //int cantidadAnexos=listaRutasFactibles.size();
             
        int cantAnexosRevisados=0;
        int encontroAlMenosUno=0; 
        int porcCantAnexos=cantidadAnexos/porcentajeEvaluacion;//PORCENTAJE DE LAS RUTAS QUE SE ESTÁN EVALUANDO
        
        //obtenemos el dia de la semana a examinar para el origen del pedido
        Calendar c=Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("dd/M/yyyy").parse(fechaPedido));
        } catch (ParseException ex) {
            Logger.getLogger(DFS.class.getName()).log(Level.SEVERE, null, ex);
        }
        int dayweek=c.get(Calendar.DAY_OF_WEEK)-1;//porque la semana comienza el domingo y el arreglo del 0-6
        
        int cantidadNFTiempo=0;
        int cantidadNFCapacidadAlmacen=0;
        int cantidadNFCapacidadVuelo=0;
        
        
        ArrayList<Integer> listaRutasAEscoger=crearListaAEscogerXCiudad(ciudadO);
        //ArrayList<Integer> listaRutasAEscoger=crearListaAEscogerXCiudad(ciudadO,codCiudadF);
                
        while(cantAnexosRevisados<cantidadAnexos && encontroAlMenosUno==0){
            int cantPorcAnexosRevisados=porcCantAnexos;
            while(cantPorcAnexosRevisados>0 && cantAnexosRevisados<cantidadAnexos){
                
                int indRutaListaARevisar=rnd.nextInt(listaRutasAEscoger.size());//Aqui se puede meter el colonia de hormigas                
                int indRutaARevisar=listaRutasAEscoger.get(indRutaListaARevisar);
                removerElementos_Lista(indRutaARevisar,listaRutasAEscoger);// Se remueve los elementos repetidos
                
                Ruta rutaActual=RutasAnexadasO.get(indRutaARevisar);
                //ConjRutas rutaEvaluada=listaRutasFactibles.get(indRutaARevisar);

                RutaEscogida resultadoRutaInicial = new RutaEscogida(0);

                String[] superTemporal=horaPedido.split(":");
                
                RutaEscogida resultadoRuta=recursiveSearch(rutaActual,resultadoRutaInicial,maxTiempoVuelo,codCiudadF,Integer.parseInt(superTemporal[0]),fechaPedido,cantPaquetes,dayweek);                
                //RutaEscogida resultadoRuta=evaluarRuta(rutaEvaluada,resultadoRutaInicial,maxTiempoVuelo,codCiudadF,Integer.parseInt(superTemporal[0]),fechaPedido,cantPaquetes,dayweek);
                
                
                if(resultadoRuta.getEstadoRuta()==this.estadoRutaFactible){
                    //AQUI SE EVALUA LO QUE SE TENGA QUE EVALUAR PARA ESCOGER EL MEJOR

                    if(resultadoRuta.getTiempoRuta()/maxTiempoVuelo + resultadoRuta.capacidades <mejorRuta.getTiempoRuta()/maxTiempoVuelo + mejorRuta.capacidades){ //REVISAR CAPACIDADES AHIGA LA CALOR
                        mejorRuta=resultadoRuta;
                        indiceRutaEscogida=indRutaARevisar;//Se guarda el indice de la rutaescogida para agregar en uno dicha ciudad
                        encontroAlMenosUno=1;//Se encontro una ruta disponible
                    }

                }else{
                    if(resultadoRuta.getEstadoRuta()==this.estadoRutaXTiempo)
                        cantidadNFTiempo++;
                    else if(resultadoRuta.getEstadoRuta()==this.estadoRutaXCapacidadAlmacen)
                        cantidadNFCapacidadAlmacen++;
                    else if(resultadoRuta.getEstadoRuta()==this.estadoRutaXCapacidadVuelo)
                        cantidadNFCapacidadVuelo++;
                }
                cantPorcAnexosRevisados--;
                cantAnexosRevisados++;
            }
        }
        if(mejorRuta.getTiempoRuta()==1000 || mejorRuta.getEstadoRuta()!=this.estadoRutaFactible)
            System.out.println("Numero de Pedido: "+numPedido+" No se encontró ruta - No Factible Tiempo: "+cantidadNFTiempo+" - No Factible Capacidad Almacen: "+cantidadNFCapacidadAlmacen+" - No Factible Capacidad Vuelo: "+cantidadNFCapacidadVuelo);
            //APLICAR RERUTEO
        
        else{
            System.out.println("Numero de Pedido: "+numPedido+ " Mejor Ruta: "+mejorRuta.imprimirRecorrido()+" Mejor Tiempo: "+mejorRuta.getTiempoRuta());
            ciudadO.incrementarRutaEscogida(indiceRutaEscogida);
            //ciudadO.incrementarRutaEscogidaXCiudad(codCiudadF, indiceRutaEscogida);
            
            actualizarCapacidadAlmacen_Rutas(mejorRuta, cantPaquetes,horaPedido,fechaPedido,codCiudadF,dayweek);
        }
    }

    private RutaEscogida evaluarRuta(ConjRutas rutaEvaluada,RutaEscogida resultadoRuta,int maxTiempoVuelo,String ciudadFinal,int horaPartida, String fechaPedido,int cantidadPaquetes, int dayweek){
        
        for(Ruta rutaActual: rutaEvaluada.vuelos){
            if(dayweek>6)dayweek-=7;
            int tiempoTraslado=calcularTiempoTraslado(rutaActual);
            int tiempoEspera=calcularTiempoEspera(rutaActual, horaPartida);
            //buscamos las ciudades para poder obtener las proyecciones de sus almacenes
            Ciudad ciudadDestino=ciudades.get(rutaActual.getCiudadOrigen());
            Ciudad ciudadOrigen=ciudades.get(rutaActual.getCiudadFin());
            TreeMap almacenOrigen = (TreeMap) ciudadOrigen.proyeccionAlmacen.get(dayweek);        
        
            if(hayCapacidad(ciudadOrigen,dayweek, horaPartida, tiempoEspera,cantidadPaquetes)==0){
                resultadoRuta.setEstadoRuta(this.estadoRutaXCapacidadAlmacen);
                return resultadoRuta;
            }
            
            if(rutaActual.cantidadPaquetesXDia[dayweek]>this.maxCapacidadesVuelos){//Si se sobrepasa la capacidad del Vuelo en ese dia 
                resultadoRuta.setEstadoRuta(this.estadoRutaXCapacidadVuelo);
                return resultadoRuta;
            }
            //int tiempoTraslado_Espera=calcularTiempo(rutaActual,horaPartida);
            int tiempoTotalActualizado=resultadoRuta.getTiempoRuta()+tiempoTraslado+tiempoEspera;

            int horaLLegada=tiempoTotalActualizado;
            if(horaLLegada>23){
                dayweek+=(horaLLegada/24);
                horaLLegada%=24;
            }

            if(dayweek>6)dayweek-=7;

            TreeMap almacenDestino = (TreeMap) ciudadDestino.proyeccionAlmacen.get(dayweek);
            //se revisa si la llegada al aeropuerto de destino desbordaria el almacen
            if((int)(almacenDestino.get(horaLLegada*100))+cantidadPaquetes>maxCapacidadCiudades){
                resultadoRuta.setEstadoRuta(this.estadoRutaXCapacidadAlmacen);
                return resultadoRuta;
            }

            if(tiempoTotalActualizado>maxTiempoVuelo){ // FALTA REVISAR LO DE LAS CAPACIDADES DE CIUDADES
                resultadoRuta.setEstadoRuta(this.estadoRutaXTiempo);
                return resultadoRuta;          
            }
            else{
                if(rutaActual.getCiudadFin().equals(ciudadFinal)){                             
                    resultadoRuta.agregarRuta(rutaActual);
                    resultadoRuta.agregarTiempoEspera(tiempoEspera);
                    resultadoRuta.agregarTiempoTraslado(tiempoTraslado);
                    resultadoRuta.actualizarTiempoRuta(tiempoTraslado, tiempoEspera);
                    resultadoRuta.capacidades+=((int)(almacenDestino.get(horaLLegada*100))+cantidadPaquetes+(int)almacenOrigen.get(horaPartida*100)+cantidadPaquetes)/(2*maxCapacidadCiudades);
                    resultadoRuta.setEstadoRuta(this.estadoRutaFactible);
                }

                if(tiempoTotalActualizado==maxTiempoVuelo){//Si ya llego al tope, no vale la pena seguir buscando caminos
                    resultadoRuta.setEstadoRuta(this.estadoRutaXTiempo);
                    return resultadoRuta;  
                }
            }            
        }
        
        resultadoRuta.setEstadoRuta(this.estadoRutaFactible);
        return resultadoRuta;
    }
    
    private RutaEscogida recursiveSearch(Ruta rutaActual,RutaEscogida resultadoRuta,int maxTiempoVuelo,String ciudadFinal,int horaPartida, String fechaPedido,int cantidadPaquetes, int dayweek){//ACLARAR horaPartida?
        
        if(dayweek>6)dayweek-=7;
        int tiempoTraslado=calcularTiempoTraslado(rutaActual);
        int tiempoEspera=calcularTiempoEspera(rutaActual, horaPartida);
        
        //buscamos las ciudades para poder obtener las proyecciones de sus almacenes
        Ciudad ciudadDestino=ciudades.get(rutaActual.getCiudadOrigen());
        Ciudad ciudadOrigen=ciudades.get(rutaActual.getCiudadFin());
   

        //ARREGLAR EL DIA DE LA SEMANA - ACHO QUE NAO
        
        TreeMap almacenOrigen = (TreeMap) ciudadOrigen.proyeccionAlmacen.get(dayweek);        
        
        if(hayCapacidad(ciudadOrigen,dayweek, horaPartida, tiempoEspera,cantidadPaquetes)==0){
            resultadoRuta.setEstadoRuta(this.estadoRutaXCapacidadAlmacen);
            return resultadoRuta;
        }
        
        if(rutaActual.cantidadPaquetesXDia[dayweek]>this.maxCapacidadesVuelos){//Si se sobrepasa la capacidad del Vuelo en ese dia 
            resultadoRuta.setEstadoRuta(this.estadoRutaXCapacidadVuelo);
            return resultadoRuta;
        }
        //int tiempoTraslado_Espera=calcularTiempo(rutaActual,horaPartida);
        int tiempoTotalActualizado=resultadoRuta.getTiempoRuta()+tiempoTraslado+tiempoEspera;
        
        int horaLLegada=tiempoTotalActualizado;
        if(horaLLegada>23){
            dayweek+=(horaLLegada/24);
            horaLLegada%=24;
        }
        
        if(dayweek>6)dayweek-=7;
        
        TreeMap almacenDestino = (TreeMap) ciudadDestino.proyeccionAlmacen.get(dayweek);
        
        //REVISA SI EXISTE ESPACIO CUANDO LLEGA EL PAQUETE
        
        //se revisa si la llegada al aeropuerto de destino desbordaria el almacen
        if((int)(almacenDestino.get(horaLLegada*100))+cantidadPaquetes>maxCapacidadCiudades){
            resultadoRuta.setEstadoRuta(this.estadoRutaXCapacidadAlmacen);
            return resultadoRuta;
        }
        
        
        if(tiempoTotalActualizado>maxTiempoVuelo){ // FALTA REVISAR LO DE LAS CAPACIDADES DE CIUDADES
            resultadoRuta.setEstadoRuta(this.estadoRutaXTiempo);
            return resultadoRuta;          
        }
        else{
            if(rutaActual.getCiudadFin().equals(ciudadFinal)){
                RutaEscogida newresultadoRuta= new RutaEscogida(resultadoRuta);
                
                newresultadoRuta.agregarRuta(rutaActual);
                newresultadoRuta.agregarTiempoEspera(tiempoEspera);
                newresultadoRuta.agregarTiempoTraslado(tiempoTraslado);
                newresultadoRuta.actualizarTiempoRuta(tiempoTraslado, tiempoEspera);
                newresultadoRuta.capacidades+=((int)(almacenDestino.get(horaLLegada*100))+cantidadPaquetes+(int)almacenOrigen.get(horaPartida*100)+cantidadPaquetes)/(2*maxCapacidadCiudades);
                newresultadoRuta.setEstadoRuta(this.estadoRutaFactible);
                return newresultadoRuta;
            }
            
            if(tiempoTotalActualizado==maxTiempoVuelo){//Si ya llego al tope, no vale la pena seguir buscando caminos
                resultadoRuta.setEstadoRuta(this.estadoRutaXTiempo);
                return resultadoRuta;  
            }
        }
          
        Ciudad ciudadNuevaPartida=ciudades.get(rutaActual.getCiudadFin());
        String[] hhmm=rutaActual.getHoraFin().trim().split(":");        
        horaPartida=Integer.parseInt(hhmm[0]);
        
        RutaEscogida mejorRuta= new RutaEscogida(1000);//Numero alto para que sea reemplazado en le primera iteración
        
        RutaEscogida updateResultadoRuta= new RutaEscogida(resultadoRuta);
                
        updateResultadoRuta.agregarRuta(rutaActual);
        updateResultadoRuta.agregarTiempoEspera(tiempoEspera);
        updateResultadoRuta.agregarTiempoTraslado(tiempoTraslado);
        updateResultadoRuta.actualizarTiempoRuta(tiempoTraslado, tiempoEspera);
        
        
        ArrayList<Ruta> RutasAnexadasNuevaPartida=ciudadNuevaPartida.getRutasAnexas();
        for(Ruta rutaNueva: RutasAnexadasNuevaPartida){
            
            if(rutaNueva.getCiudadFin().equals(rutaActual.getCiudadOrigen())){//Se está volviendo al mismo punto
                continue;
            }
            
            RutaEscogida newResultadoRuta=recursiveSearch(rutaNueva,updateResultadoRuta,maxTiempoVuelo,ciudadFinal,horaPartida,fechaPedido,cantidadPaquetes,dayweek>6?dayweek-7:dayweek);
            
            //MejorRuta resultadoRuta=recursiveSearch(rutaNueva,tiempoTotalActualizado,maxTiempoVuelo,ciudadFinal,seguimientoRuta+"-"+rutaNueva.getCiudadOrigen(),horaPartida);
            if(newResultadoRuta.getEstadoRuta()==this.estadoRutaFactible){
                if(newResultadoRuta.getTiempoRuta()<mejorRuta.getTiempoRuta()){
                    mejorRuta=newResultadoRuta;
                }                
            }
            
        }
        if(mejorRuta.getTiempoRuta()==1000){
            mejorRuta.setEstadoRuta(this.estadoRutaXTiempo);
            return mejorRuta; 
        }
        else{
            mejorRuta.setEstadoRuta(this.estadoRutaFactible);    
            return mejorRuta;
        }
    }
    
    private int calcularTiempoTraslado (Ruta rutaActual){
        int tiempoTraslado;       
        Ciudad ciudadI=ciudades.get(rutaActual.getCiudadOrigen());
        Ciudad ciudadF=ciudades.get(rutaActual.getCiudadFin());
        if(ciudadI.getContinente().equals(ciudadF.getContinente()))
            tiempoTraslado=12;
        else
            tiempoTraslado=24;
        return tiempoTraslado;
    }
    
    private int calcularTiempoEspera (Ruta rutaActual,int horaPartida){
        String[] hhmm=rutaActual.getHoraOrigen().trim().split(":");
        int hhOrigen=Integer.parseInt(hhmm[0]);
        int tiempoEspera;
        if(horaPartida<=hhOrigen)
            tiempoEspera=hhOrigen-horaPartida;
        else
            tiempoEspera=24-(horaPartida-hhOrigen);
        return tiempoEspera;
    }
    
    private int hayCapacidad(Ciudad ciudadOrigen,int dayweek,int horaPartida,int tiempoEspera,int cantidadPaquetes){
        
        if(dayweek>6)dayweek-=7;
        TreeMap almacenOrigen = (TreeMap) ciudadOrigen.proyeccionAlmacen.get(dayweek);  
        if(almacenOrigen==null)System.out.println("dfs.GestorCiudades.hayCapacidad()");
        
        //SE REVISA SI EXISTE CAPACIDAD PARA ALMACENAR LOS PAQUETES DESDE QUE LLEGA EL PEDIDO HASTA QUE SE VAYA EL AVION        
        if(horaPartida+tiempoEspera>23){//si supera el dia esperando el vuelo            
            int tempHora=horaPartida;
            while(tempHora<23){
                //se revisa si la llegada al aeropuerto de origen desbordaria el almacen
                if((int)(almacenOrigen.get(tempHora*100))+cantidadPaquetes>maxCapacidadCiudades) return 0;                
                tempHora++;
            }
            tempHora=0;
            if(dayweek==6)dayweek=0;
            else dayweek++;
            almacenOrigen =(TreeMap) ciudadOrigen.proyeccionAlmacen.get(dayweek);
            while(tempHora<(horaPartida+tiempoEspera)%24){
                //se revisa si la llegada al aeropuerto de origen desbordaria el almacen
                if((int)(almacenOrigen.get(tempHora*100))+cantidadPaquetes>maxCapacidadCiudades)return 0;                
                tempHora++;
            }
        }
        else{
            int tempHora=horaPartida;
            while(tempHora<horaPartida+tiempoEspera){
                if((int)(almacenOrigen.get(tempHora*100))+cantidadPaquetes>maxCapacidadCiudades)return 0;                
                tempHora++;
            }
        }
    
        return 1;
    }
    
    private void actualizarCapacidadAlmacen_Rutas(RutaEscogida mejorRuta,int cantPaquetes,String horaPedido,String fechaPedido,String ciudadLlegada,int dayweek){
        String[] hora=horaPedido.split(":");
        int horaLlegada= Integer.parseInt(hora[0]);//hora en la que llea el pedido        
        
        //actualizamos el almacen del primer lugar de origen hasta que sale el envio
        
        ArrayList<Ruta> recorrido= mejorRuta.getListaRutaEscogida();
        if(dayweek>6)dayweek-=7;
        for(int i=0;i<recorrido.size();++i){
            Ruta punto=recorrido.get(i);
            int capacidadRutaDia=punto.getIndCapacidad(dayweek);//Se actualiza la capacidad de las rutasXDestino escogidas
            punto.setIndCapacidad(dayweek, capacidadRutaDia+cantPaquetes);
            
            int tiempoEspera = mejorRuta.getTiemposEspera().get(i);
            int tiempoTraslado = mejorRuta.getTiemposTraslado().get(i);
            
            //PARA EL TIEMPO DE ESPERA EN EL ALMACEN HASTA QUE SALGA EL VUELO
            if(horaLlegada+tiempoEspera>23){//si supera el dia esperando el vuelo
                TreeMap temporal =(TreeMap) ciudades.get(punto.getCiudadOrigen()).proyeccionAlmacen.get(dayweek);
                int tempHora=horaLlegada;
                while(tempHora<23){
                    int valorActual=(int)temporal.get(tempHora*100+1);//aqui seria la hora no 900
                    temporal.put(tempHora*100, valorActual+cantPaquetes);
                    temporal.put(tempHora*100+1, valorActual+cantPaquetes);
                    tempHora++;
                }
                tempHora=0;
                temporal =(TreeMap) ciudades.get(punto.getCiudadOrigen()).proyeccionAlmacen.get(dayweek==6?0:dayweek+1);
                while(tempHora<(horaLlegada+tiempoEspera)%24){
                    int valorActual=(int)temporal.get(tempHora*100+1);//aqui seria la hora no 900
                    temporal.put(tempHora*100, valorActual+cantPaquetes);
                    temporal.put(tempHora*100+1, valorActual+cantPaquetes);
                    tempHora++;
                }
            }
            else{
                int tempHora=horaLlegada;
                TreeMap temporal =(TreeMap) ciudades.get(punto.getCiudadOrigen()).proyeccionAlmacen.get(dayweek);
                while(tempHora<horaLlegada+tiempoEspera){
                    int valorActual=(int)temporal.get(tempHora*100+1);//aqui seria la hora no 900
                    temporal.put(tempHora*100, valorActual+cantPaquetes);
                    temporal.put(tempHora*100+1, valorActual+cantPaquetes);
                    tempHora++;
                }
            }
            
            //LUEGO EVALUAMOS A DONDE VA EL VUELO
            if(punto.getCiudadFin().equals(ciudadLlegada)){
                int horaLlegadaDestino=horaLlegada+tiempoEspera+tiempoTraslado;
                while(horaLlegadaDestino>23){
                    dayweek+=(horaLlegadaDestino/24);
                    if(dayweek>6)dayweek-=7;
                    horaLlegadaDestino%=24;
                }
                TreeMap temporal =(TreeMap) ciudades.get(punto.getCiudadFin()).proyeccionAlmacen.get(dayweek);
                int valorActual=(int)temporal.get(horaLlegadaDestino*100+1);
                temporal.put(horaLlegadaDestino*100, valorActual+cantPaquetes);
                temporal.put(horaLlegadaDestino*100+1, valorActual);
            }
        }
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
        ciudades.get("SKBO").print();
    }
    
    private ArrayList<Integer> crearListaAEscogerXCiudad(Ciudad ciudadO){        
        ArrayList<Integer> listaAEscoger = new ArrayList<Integer>();
        
        ArrayList<Integer> cantVisitadasRutas = ciudadO.getCantVisitadasRutasAnexas();
        
        int indCantVisitadasRutas=0;
        for(Integer cantVisita : cantVisitadasRutas){
            
            while(cantVisita-->0){
                listaAEscoger.add(indCantVisitadasRutas);
            }
            indCantVisitadasRutas++;
        }
        
        return listaAEscoger;
    }
    
    private ArrayList<Integer> crearListaAEscogerXCiudad(Ciudad ciudadO,String codCiudadFin){        
        ArrayList<Integer> listaAEscoger = new ArrayList<Integer>();
        
        ArrayList<Integer> cantVisitadasRutas = ciudadO.getCantVisitadasRutasAnexas();
        
        int indCantVisitadasRutas=0;
        for(Integer cantVisita : cantVisitadasRutas){
            
            while(cantVisita-->0){
                listaAEscoger.add(indCantVisitadasRutas);
            }
            indCantVisitadasRutas++;
        }
        
        return listaAEscoger;
    }
    
    
    private void removerElementos_Lista(Integer ind, ArrayList<Integer> lst){
        ArrayList<Integer> rmv= new ArrayList<Integer>();
        rmv.add(ind);
        
        lst.removeAll(rmv);
    }
    
    private Ciudad siguienteEnvio(){
        Set set = ciudades.entrySet();
        Iterator i = set.iterator();
        
        Ciudad siguiente=null;
        Date menor=null;
        
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Ciudad ciudadActual=(Ciudad)me.getValue();
            
            if(ciudadActual.getUltimaHora()==null)continue;
            
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy-H:m");
            String fechaTemporal=ciudadActual.getUltimaFecha()+"-"+ciudadActual.getUltimaHora();
            try{
                Date fechaHoraCiudad=formatter.parse(fechaTemporal);
                if(menor==null){
                    menor=fechaHoraCiudad;
                    siguiente=ciudadActual;
                }
                else if(fechaHoraCiudad.compareTo(menor)<0){
                    menor=fechaHoraCiudad;
                    siguiente=ciudadActual;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(siguiente==null)return null;
        return siguiente;
    }
    
    public void lineaInicial() throws FileNotFoundException{
        //buscamos la primera linea de cada archivo asociado a cada ciudad
        //SOLO UNA VEZ SE EJECUTA.
        
        //iteracion sobre las ciudades
        Set set = ciudades.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Ciudad ciudadActual=(Ciudad)me.getValue();
             
            BufferedReader archivoCiudad= ciudadActual.getBrAeropuertos();
            String linea;
            String[] temporalArchivo=new String[1000];
            int numeroPedidos=0;
            try{
                /*if((linea=archivoCiudad.readLine()) != null){
                    String codigoPaquete=linea.substring(0, 9);
                    String fechaLlegadaPaquete=linea.substring(15,17)+"/"+linea.substring(13,15)+"/"+linea.substring(9,13);
                    String horaLlegadaPaquete=linea.substring(17,22);
                    String DestinoPaquete=linea.substring(22,26);
                    ciudadActual.setLinea(1);
                    ciudadActual.setUltimaFecha(fechaLlegadaPaquete);
                    ciudadActual.setUltimaHora(horaLlegadaPaquete);
                    ciudadActual.setUltimoDestino(DestinoPaquete);                     
                }
                archivoCiudad.close();    */
                
                while((linea=archivoCiudad.readLine()) != null){
                    if(numeroPedidos==0){
                        String codigoPaquete=linea.substring(0, 9);
                        String fechaLlegadaPaquete=linea.substring(15,17)+"/"+linea.substring(13,15)+"/"+linea.substring(9,13);
                        String horaLlegadaPaquete=linea.substring(17,22);
                        String DestinoPaquete=linea.substring(22,26);
                        ciudadActual.setLinea(1);
                        ciudadActual.setUltimaFecha(fechaLlegadaPaquete);
                        ciudadActual.setUltimaHora(horaLlegadaPaquete);
                        ciudadActual.setUltimoDestino(DestinoPaquete);  
                    }
                    else temporalArchivo[numeroPedidos]=linea;
                    numeroPedidos++;
                }
                temporalArchivo[numeroPedidos]=null;
                archivoCiudad.close();
                ciudadActual.setArchivoPedidos(temporalArchivo);
            }catch (IOException e)
            {
                System.err.println(e.toString());
            }
            finally
            {
                try
                {
                    archivoCiudad.close();
                }
                catch (IOException ex)
                {
                    System.err.println(ex.toString());
                }
            }
            
        }
        //siguienteEnvio();
    }
    
}
