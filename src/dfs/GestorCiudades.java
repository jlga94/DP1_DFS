
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dfs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    private int maxCapacidadCiudades=600;
    private int maxCapacidadesVuelos=200;
    private int maxTiempoContinental=24;
    private int maxTiempoIntercontinental=48;
    private int porcentajeEvaluacion=4;
    
    private int estadoRutaFactible=0;
    private int estadoRutaXTiempo=1;
    private int estadoRutaXCapacidadAlmacen=2;
    private int estadoRutaXCapacidadVuelo=3;

    private Random rnd;
    private int TiempoEntregaPaquetes=0;

    GestorCiudades() {
        
    }
    
    
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
                    getCiudades().put(codigo, newCiudad);
                }
            }
            brAeropuertos.close();
            while((linea=brHusos.readLine()) != null){
                String [] datos=linea.trim().split("\t");
                String codigo=datos[1];
                int huso_horario=Integer.parseInt(datos[2]);
                Ciudad ciudadBuscada=getCiudades().get(codigo);
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
                
                
            int husoO=ciudades.get(ciudadO).getHuso_horario();
            int husoF=ciudades.get(ciudadF).getHuso_horario();            
            int hSalida=(horaPartida-husoO)%24;
            int hLlegada=(horaFin-husoF)%24;
            if(hSalida<0) hSalida+=24;
            if(hLlegada<0) hLlegada+=24;
            if(hLlegada<hSalida) hLlegada+=24;

            int tiempoVuelo=hLlegada-hSalida;

            newRuta.setTiempo(tiempoVuelo);
            
//                if(getCiudades().get(ciudadO).getContinente().equals(getCiudades().get(ciudadF).getContinente())) newRuta.setTiempo(12);
//                else newRuta.setTiempo(24);
                Ciudad ciudadOrigen=getCiudades().get(ciudadO);
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
        Set setKeys = ciudades.keySet();
        ArrayList<String> listOfNames = new ArrayList(setKeys);

        for(int i=0;i<listOfNames.size();i++) {

            Ciudad ciudadActual=(Ciudad)ciudades.get(listOfNames.get(i));
            ciudadActual.instanciarCantidadIdasRutasAnexas();


            for(int j=0;j<listOfNames.size();j++) {
                Ciudad ciudadBuscada=(Ciudad)ciudades.get(listOfNames.get(j));
                
                if(ciudadActual.getCodigo().equals(ciudadBuscada.getCodigo()))
                    continue;
                //System.out.println("CIUDAD INICIO: "+ciudadActual.getCodigo() + " -CIUDAD FIN: "+ciudadBuscada.getCodigo());
                    
                ciudadActual.instanciarCantVisitadasrutasXDestino(ciudadBuscada.getCodigo());
            }

        }
    }
    
    public GestorCiudades(String archVuelos,String archAeropuertos,String archHusos) throws FileNotFoundException{
        leerCiudades(archAeropuertos,archHusos);
        leerRutas(archVuelos);
        generarConjRutas();//generar todas las rutasXDestino posibles
        //ArrayList<ConjRutas> rutas=encuentraRutas(ciudades.get("SVMI"),"SCEL",24,0);
        //System.out.println(rutas.size());
        //for(int i=0;i<rutas.size();i++) rutas.get(i).print();    
        instanciarVecesRecorridasCiudades();
        
        //UTILIZAR ALGORITMO DE TODAS LAS CIUDADES A TODAS LAS CIUDADES
        
        this.rnd=new Random();
    }
    public void generarConjRutas(){
        int tEspera;
        int tiempoRuta;
        for(Ciudad ciudad : ciudades.values()) {
            for(Ciudad ciudFin : ciudades.values()){
                if(!ciudFin.getCodigo().equals(ciudad.getCodigo())){
                    int tMax=48; //maximo de horas
                    if(ciudFin.getContinente().equals(ciudad.getContinente())) tMax=24;
                    ArrayList<ConjRutas> rutas=encuentraRutas(ciudad,ciudFin.getCodigo(),tMax,0);
                    if(rutas.size()>0) ciudad.rutasXDestino.put(ciudFin.getCodigo(), rutas);
                }

            }
        }         
    }
    
    public  ArrayList<ConjRutas> encuentraRutas(Ciudad ciudOrigen, String ciudFinal,int tiempoDisp,int niveles){
        ArrayList<ConjRutas> rutas= new ArrayList<>();
        if(tiempoDisp<=0 || niveles==3) return rutas; // si ya no hay más tiempo no seguir más
        ArrayList<Ruta>vuelos = ciudOrigen.rutasAnexas;

        for(int i=0;i<vuelos.size();i++){
            //caso directo
            Ruta vuelo=vuelos.get(i);
            Ciudad ciudadFinVuelo=ciudades.get(vuelo.getCiudadFin());
            if(vuelo.getCiudadFin().equals(ciudFinal)){ // si cumple el destino
                if(vuelo.getTiempo()<=tiempoDisp) // si cumple la regla de negocio
                    rutas.add(new ConjRutas(vuelo,vuelo.getTiempo()));
            }
            else{ //intento con escala
                int nivelesManda=niveles+1;
                ArrayList<ConjRutas> rutasEscala=encuentraRutas(ciudadFinVuelo,ciudFinal
                        ,tiempoDisp-vuelo.getTiempo(),nivelesManda);// se obtiene las rutas desde la escala hasta el destino
                
                for(int j=0;j<rutasEscala.size();j++){ //verificar que se cumple el tiempo(considerando espera) x ruta
                    ConjRutas ruta =rutasEscala.get(j);
                    int tEspera;
                    int tEsperaTotal=0;
                    int tVueloTotal=vuelo.getTiempo();
                    for(int h=0;h<ruta.vuelos.size();h++){
                        Ruta vueloInt=ruta.vuelos.get(h);
                        if(h==0) tEspera=vueloInt.horaO-vuelo.horaF; //para el primer vuelo
                        else tEspera= vueloInt.horaO-ruta.vuelos.get(h-1).horaF;
                        if(tEspera<0) tEspera+=24;
                        tEsperaTotal+=tEspera;
                        tVueloTotal+=vueloInt.getTiempo();
                    }
                    int tTotal=tVueloTotal+tEsperaTotal;
                    if(tTotal<=tiempoDisp) {
                        ruta.vuelos.add(0, vuelo); // agregamos el vuelo inicial(origen-escala)
                        ruta.tiempo=tTotal;
                        rutas.add(ruta);
                    }                  
                    
                }
            }
        }      
        return rutas;
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
        Set set = getCiudades().entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Ciudad ciudadActual=(Ciudad)me.getValue();
            
            TreeMap proyecciones = (TreeMap)ciudadActual.proyeccionAlmacen.get(dayweek);
            
            TreeMap proyeccionesPedidos=(TreeMap)ciudadActual.proyeccionPedidos.get(dayweek);
            
            if(proyecciones==null)System.out.println(dayweek);
            for(int j=0;j<24;++j){//24 HORAS
                proyecciones.put(j*100, 0);
                proyecciones.put(j*100+1, 0);
                ((ArrayList)proyeccionesPedidos.get(j)).clear();
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
            //DFS(siguienteCiudad.getCodigo(),siguienteCiudad.getUltimoDestino(),numPedido,siguienteCiudad.getUltimaHora(),1,siguienteCiudad.getUltimaFecha(),siguienteCiudad.get);
            
            siguienteCiudad.avanzarBuffer();
            
            numPedido++;
        }
        System.out.println("Tiempo Total por paquetes: "+this.TiempoEntregaPaquetes);
        
    }
    
    /*public void asignarPedidos(String archPedidos)throws FileNotFoundException{
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
                DFS(codCiudadO,codCiudadF,numPedido,horaPedido,cantPaquetes,fechaPedido,"sdfsdfs");
                
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
    }*/
        
    private boolean DFS(String codCiudadO,String codCiudadF,int numPedido, String horaPedido,int cantPaquetes,String fechaPedido, String codigoPaquete, RutaEscogida rutaAomitir,boolean reruteo, int tiempoTranscurrido){
        
        Paquete nuevoPaquete=new Paquete(codigoPaquete,fechaPedido,horaPedido,codCiudadO,codCiudadF,null);
        
        Ciudad ciudadO=getCiudades().get(codCiudadO);
        Ciudad ciudadF=getCiudades().get(codCiudadF);
        int maxTiempoVuelo;
        
        //Establece el tiempo maximo de vuelo
        if(ciudadO.getContinente().equals(ciudadF.getContinente()))
            maxTiempoVuelo=maxTiempoContinental-tiempoTranscurrido;
        else
            maxTiempoVuelo=maxTiempoIntercontinental-tiempoTranscurrido;
        
        
        RutaEscogida mejorRuta= new RutaEscogida(1000);//Numero alto para que sea reemplazado en le primera iteración
        int indiceRutaEscogida=-1;
        
        //ArrayList<Ruta> RutasAnexadasO=ciudadO.getRutasAnexas(); //CAMBIAR A LAS RUTAS QUE PUEDAN SER FACTIBLES        
        ArrayList<ConjRutas> listaRutasFactibles=(ArrayList<ConjRutas>) ciudadO.rutasXDestino.get(codCiudadF);
        
        int cantidadAnexos;
        if(listaRutasFactibles!=null){
            //int cantidadAnexos=RutasAnexadasO.size();        
            cantidadAnexos=listaRutasFactibles.size();
        }else{
            cantidadAnexos=0;
            mejorRuta.setEstadoRuta(estadoRutaXTiempo);
        }
        int cantAnexosRevisados=0;
        int encontroAlMenosUno=0; 
        
        if(cantidadAnexos%4==0)
            porcentajeEvaluacion=4;
        else if(cantidadAnexos%3==0)
            porcentajeEvaluacion=3;
        else if(cantidadAnexos%2==0)
            porcentajeEvaluacion=2;
        else
            porcentajeEvaluacion=1;
        
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
        
        ArrayList<ConjRutas> listaRutasNFCapacidadAlmacen = new ArrayList<ConjRutas>();
        ArrayList<RutaEscogida> listaRutasIncompletasNFCapacidadAlmacen = new ArrayList<RutaEscogida>();
        ArrayList<ConjRutas> listaRutasNFCapacidadVuelo = new ArrayList<ConjRutas>();
        ArrayList<RutaEscogida> listaRutasIncompletasNFCapacidadVuelo = new ArrayList<RutaEscogida>();
        
        
        //ArrayList<Integer> listaRutasAEscoger=crearListaAEscogerXCiudad(ciudadO);
        ArrayList<Integer> listaRutasAEscoger=crearListaAEscogerXCiudad(ciudadO,codCiudadF);
                
        while(cantAnexosRevisados<cantidadAnexos && encontroAlMenosUno==0){
            int cantPorcAnexosRevisados=porcCantAnexos;
            while(cantPorcAnexosRevisados>0 && cantAnexosRevisados<cantidadAnexos){
                
                int indRutaListaARevisar=rnd.nextInt(listaRutasAEscoger.size());//Aqui se puede meter el colonia de hormigas                
                int indRutaARevisar=listaRutasAEscoger.get(indRutaListaARevisar);
                removerElementos_Lista(indRutaARevisar,listaRutasAEscoger);// Se remueve los elementos repetidos
                
                //Ruta rutaActual=RutasAnexadasO.get(indRutaARevisar);
                ConjRutas rutaEvaluada=listaRutasFactibles.get(indRutaARevisar);

                RutaEscogida resultadoRutaInicial = new RutaEscogida(0);

                String[] superTemporal=horaPedido.split(":");
                
                //RutaEscogida resultadoRuta=recursiveSearch(rutaActual,resultadoRutaInicial,maxTiempoVuelo,codCiudadF,Integer.parseInt(superTemporal[0]),fechaPedido,cantPaquetes,dayweek);                
                RutaEscogida resultadoRuta=evaluarRuta(rutaEvaluada,resultadoRutaInicial,maxTiempoVuelo,codCiudadF,Integer.parseInt(superTemporal[0]),fechaPedido,cantPaquetes,dayweek);
                
                
                if(resultadoRuta.getEstadoRuta()==this.estadoRutaFactible){
                    //AQUI SE EVALUA LO QUE SE TENGA QUE EVALUAR PARA ESCOGER EL MEJOR
                    
                    if(reruteo && compararSiRutasIguales(resultadoRuta,rutaAomitir)) continue;
                    
                    if(resultadoRuta.getTiempoRuta()/maxTiempoVuelo + resultadoRuta.capacidades <mejorRuta.getTiempoRuta()/maxTiempoVuelo + mejorRuta.capacidades){ //REVISAR CAPACIDADES AHIGA LA CALOR
                        mejorRuta=resultadoRuta;
                        indiceRutaEscogida=indRutaARevisar;//Se guarda el indice de la rutaescogida para agregar en uno dicha ciudad
                        encontroAlMenosUno=1;//Se encontro una ruta disponible
                    }

                }else{
                    if(resultadoRuta.getEstadoRuta()==this.estadoRutaXTiempo)
                        cantidadNFTiempo++;
                    else if(resultadoRuta.getEstadoRuta()==this.estadoRutaXCapacidadAlmacen)
                    {
                        cantidadNFCapacidadAlmacen++;
                        listaRutasNFCapacidadAlmacen.add(rutaEvaluada);
                        listaRutasIncompletasNFCapacidadAlmacen.add(resultadoRuta);
                    }
                    else if(resultadoRuta.getEstadoRuta()==this.estadoRutaXCapacidadVuelo)
                    {
                        cantidadNFCapacidadVuelo++;
                        listaRutasNFCapacidadVuelo.add(rutaEvaluada);
                        listaRutasIncompletasNFCapacidadVuelo.add(resultadoRuta);
                    }
                }
                cantPorcAnexosRevisados--;
                cantAnexosRevisados++;
            }
        }
        if(mejorRuta.getTiempoRuta()==1000 || mejorRuta.getEstadoRuta()!=this.estadoRutaFactible){
            System.out.println(fechaPedido+" Numero de Pedido: "+numPedido+" "+codCiudadO+"-"+codCiudadF +" No se encontró ruta - No Factible Tiempo: "+cantidadNFTiempo+" - No Factible Capacidad Almacen: "+cantidadNFCapacidadAlmacen+" - No Factible Capacidad Vuelo: "+cantidadNFCapacidadVuelo);
            //APLICAR RERUTEO
            if(cantidadNFCapacidadVuelo>0 && !reruteo)
                if(reruteoPorVuelos(listaRutasNFCapacidadVuelo, listaRutasIncompletasNFCapacidadVuelo, codCiudadF, dayweek, Integer.parseInt(horaPedido.split(":")[0]),nuevoPaquete))
                    System.out.println("Se reruteo");
                else {
                    System.out.println("No se pudo rerutear");
                    System.exit(0);
                }
                    
                    
            /*if(cantidadNFCapacidadVuelo+cantidadNFCapacidadAlmacen>0){
                System.out.println(fechaPedido+" Numero de Pedido: "+numPedido+" "+codCiudadO+"-"+codCiudadF +" No se encontró ruta - No Factible Tiempo: "+cantidadNFTiempo+" - No Factible Capacidad Almacen: "+cantidadNFCapacidadAlmacen+" - No Factible Capacidad Vuelo: "+cantidadNFCapacidadVuelo);
                System.exit(0);
            }*/
        }
        else{
            //CAMBIO
            if(!reruteo)
            {
                
                System.out.println(fechaPedido+" Numero de Pedido: "+numPedido+ " Ciudad Origen: "+codCiudadO+" - Ciudad Fin: "
                        +codCiudadF+" Mejor Ruta: "+mejorRuta.imprimirRecorrido()+" Mejor Tiempo: "+mejorRuta.getTiempoRuta());
                //ciudadO.incrementarRutaEscogida(indiceRutaEscogida);
                nuevoPaquete.setRuta(mejorRuta);
                ciudadO.incrementarRutaEscogidaXCiudad(codCiudadF, indiceRutaEscogida);
                actualizarCapacidadAlmacen_Rutas(mejorRuta, cantPaquetes,horaPedido,fechaPedido,codCiudadF,dayweek);
                agregarProyeccionPaquete(mejorRuta,codCiudadO,codCiudadF,fechaPedido,horaPedido,dayweek,nuevoPaquete);
                
            }
            else
            {
                
                return true;
            }
            
        }
        return false;//spoof?
    }
    
        private boolean reruteoPorVuelos(ArrayList listaRutasNFCapacidadVuelo, ArrayList listaRutasIncompletasNFCapacidadVuelo,String codOrigen,
                int dayweek, int horaLlegada, Paquete paqueteNoRuteado)
    {
        Ciudad ciudadOrigen=ciudades.get(codOrigen);
        
        for(ConjRutas iterador: (ArrayList<ConjRutas>)listaRutasNFCapacidadVuelo)//Recorremos la lista de rutas incompletas
        {
            for(Ruta iteratorDelIterator: iterador.vuelos)//para cada ruta obtenemos cada punto de la ruta para saber donde se lleno el vuelo
            {
                if(iteratorDelIterator.cantidadPaquetesXDia[dayweek]>=maxCapacidadesVuelos)//si encontramos donde se lleno el vuelo
                {
                    //obtenemos la lista de paquetes que estan esperando en el aeropuerto y que tienen el mismo vuelo asignado
                    
                    for(Paquete paquete: (AbstractList<Paquete>)(ciudadOrigen.proyeccionPedidos.get(dayweek).get(iteratorDelIterator.horaO)))
                    {
                        //obtenemos la ruta del paquete que esta en el aeropuerto
                        RutaEscogida rutaPaquete = paquete.getRuta();
                        //recorremos cada punto de su recorrido para ver si se tiene asignado el mismo vuelo que el envio a rerutear
                        int tiempoTranscurrido=0, indiceRecorrido=0;
                        String horaExaminar="";
                        for(Ruta puntoRutaPaquete: rutaPaquete.getListaRutaEscogida())
                        {
                            if(horaExaminar.equals(""))
                            {
                                if(Integer.parseInt((puntoRutaPaquete.getHoraOrigen().split(":")[0]))<
                                        horaLlegada)horaExaminar=String.valueOf(horaLlegada)+":00";
                                else horaExaminar=puntoRutaPaquete.getHoraOrigen();
                            }
                                
                            if(puntoRutaPaquete.getCiudadOrigen().equals(iteratorDelIterator.getCiudadOrigen()) &&
                                    puntoRutaPaquete.getCiudadFin().equals(iteratorDelIterator.getCiudadFin()))
                            {
                                /*for(int indiceRuta: crearListaAEscogerXCiudad(ciudades.get( paquete.getOrigen()), paquete.getDestino()))
                                {
                                    
                                }*/
                                //una vez que encontramos el paquete que puede ser redireccionado obtenemos desde que hora estuvo en el aeropuerto y 
                                //hallamos otra ruta para el actualizando su tiempo restante
                                
                                if(DFS(puntoRutaPaquete.getCiudadOrigen(), paquete.getDestino(), 1, horaExaminar, 1, paquete.getFecha(), paquete.getId(), paquete.getRuta(), true,tiempoTranscurrido))
                                {
                                    //si se pudo cambiar la ruta de un paquete
                                    //asignarRutaAPaquete(); 
                                    //quitarCapacidadVuelos();
                                    quitarProyeccionPaquetes(puntoRutaPaquete.getCiudadOrigen(),paquete,dayweek);
                                    
                                    DFS(paqueteNoRuteado.getOrigen(),paqueteNoRuteado.getDestino(),1,paqueteNoRuteado.getHora(),1,paqueteNoRuteado.getFecha(),paqueteNoRuteado.getId(),null,true,0);
                                    return true;
                                }
                                else break;//si no encontro pasa al siguiente paquete
                            }
                            tiempoTranscurrido+=rutaPaquete.getTiemposEspera().get(indiceRecorrido);
                            tiempoTranscurrido+=rutaPaquete.getTiemposTraslado().get(indiceRecorrido);
                            //hora a la que llega al siguiente aeropuerto
                            if(Integer.parseInt((puntoRutaPaquete.getHoraFin().split(":")[0]))<
                                        horaLlegada)horaExaminar=String.valueOf(horaLlegada)+":00";
                                else horaExaminar=puntoRutaPaquete.getHoraFin();
                            indiceRecorrido++;
                        }
                    }
                }
            }
        }
        return false;
        
    }
        
        
    private void quitarProyeccionPaquetes(String ciudadOrigen, Paquete paquete, int dayweek) {
        boolean cambiar=false;
        for(Ruta puntoDeRuta:paquete.getRuta().getListaRutaEscogida())
        {
            if(puntoDeRuta.getCiudadOrigen().equals(ciudadOrigen)) cambiar=true;
            if(cambiar)
            {
                puntoDeRuta.cantidadPaquetesXDia[dayweek]--;
                Ciudad ciudadMod= ciudades.get(puntoDeRuta.getCiudadOrigen());
                ciudadMod.proyeccionPedidos.get(dayweek).get(puntoDeRuta.horaO).remove(paquete);//maso menos
            }
        }
    }
    

    private RutaEscogida evaluarRuta(ConjRutas rutaEvaluada,RutaEscogida resultadoRuta,int maxTiempoVuelo,String ciudadFinal,int horaPartida, String fechaPedido,int cantidadPaquetes, int dayweek){
        if(rutaEvaluada.vuelos==null){
            resultadoRuta.setEstadoRuta(this.estadoRutaXTiempo);
            return resultadoRuta;
        }
        //resultadoRuta = new RutaEscogida(0);
        for(Ruta rutaActual: rutaEvaluada.vuelos){
            if(dayweek>6)dayweek-=7;
            int tiempoTraslado=calcularTiempoTraslado(rutaActual);
            int tiempoEspera=calcularTiempoEspera(rutaActual, horaPartida);
            //buscamos las ciudades para poder obtener las proyecciones de sus almacenes
            Ciudad ciudadDestino=getCiudades().get(rutaActual.getCiudadOrigen());
            Ciudad ciudadOrigen=getCiudades().get(rutaActual.getCiudadFin());
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

            if(tiempoTotalActualizado>maxTiempoVuelo){
                resultadoRuta.setEstadoRuta(this.estadoRutaXTiempo);
                return resultadoRuta;          
            }
            else
            {
                resultadoRuta.agregarRuta(rutaActual);
                resultadoRuta.agregarTiempoEspera(tiempoEspera);
                resultadoRuta.agregarTiempoTraslado(tiempoTraslado);
                resultadoRuta.actualizarTiempoRuta(tiempoTraslado, tiempoEspera);
                resultadoRuta.capacidades+=((int)(almacenDestino.get(horaLLegada*100))+cantidadPaquetes+(int)almacenOrigen.get(horaPartida*100)+cantidadPaquetes)/(2*maxCapacidadCiudades);
                
                if(!(rutaActual.getCiudadFin().equals(ciudadFinal)) && (tiempoTotalActualizado==maxTiempoVuelo)){//Si ya llego al tope, no vale la pena seguir buscando caminos                           
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
        Ciudad ciudadDestino=getCiudades().get(rutaActual.getCiudadOrigen());
        Ciudad ciudadOrigen=getCiudades().get(rutaActual.getCiudadFin());
   

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
        if(((int)almacenDestino.get(horaLLegada*100))+cantidadPaquetes>maxCapacidadCiudades){
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
          
        Ciudad ciudadNuevaPartida=getCiudades().get(rutaActual.getCiudadFin());
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
	Ciudad ciudadI=getCiudades().get(rutaActual.getCiudadOrigen());
	Ciudad ciudadF=getCiudades().get(rutaActual.getCiudadFin());
	String[] hhmmO=rutaActual.getHoraOrigen().trim().split(":");
	int hhOrigen=Integer.parseInt(hhmmO[0]);
	String[] hhmmF=rutaActual.getHoraFin().trim().split(":");
	int hhFin=Integer.parseInt(hhmmF[0]);
	
	int utcPartida=hhOrigen-ciudadI.getHuso_horario();
	if(utcPartida<0)
		utcPartida = 24 + utcPartida;
	else if(utcPartida >= 24)
		utcPartida -= 24;
	
	int utcLlegada=hhFin-ciudadF.getHuso_horario();
	if(utcLlegada<0)
		utcLlegada = 24 + utcLlegada;
	else if(utcLlegada >= 24)
		utcLlegada -= 24;
	
	if(utcPartida<utcLlegada)
		return utcLlegada-utcPartida;
	else
		return 24+utcLlegada-utcPartida;
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
                if((int)almacenOrigen.get(tempHora*100)+cantidadPaquetes>maxCapacidadCiudades)return 0;                
                //if((int)(almacenOrigen.get(tempHora*100))+cantidadPaquetes>maxCapacidadCiudades)return 0;                
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
                TreeMap temporal =(TreeMap) getCiudades().get(punto.getCiudadOrigen()).proyeccionAlmacen.get(dayweek);
                int tempHora=horaLlegada;
                while(tempHora<23){
                    int valorActual=(int)temporal.get(tempHora*100+1);//aqui seria la hora no 900
                    temporal.put(tempHora*100, valorActual+cantPaquetes);
                    temporal.put(tempHora*100+1, valorActual+cantPaquetes);
                    tempHora++;
                }
                tempHora=0;
                temporal =(TreeMap) getCiudades().get(punto.getCiudadOrigen()).proyeccionAlmacen.get(dayweek==6?0:dayweek+1);
                while(tempHora<(horaLlegada+tiempoEspera)%24){
                    int valorActual=(int)temporal.get(tempHora*100+1);//aqui seria la hora no 900
                    temporal.put(tempHora*100, valorActual+cantPaquetes);
                    temporal.put(tempHora*100+1, valorActual+cantPaquetes);
                    tempHora++;
                }
            }
            else{
                int tempHora=horaLlegada;
                TreeMap temporal =(TreeMap) getCiudades().get(punto.getCiudadOrigen()).proyeccionAlmacen.get(dayweek);
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
                TreeMap temporal =(TreeMap) getCiudades().get(punto.getCiudadFin()).proyeccionAlmacen.get(dayweek);
                int valorActual=(int)temporal.get(horaLlegadaDestino*100+1);
                temporal.put(horaLlegadaDestino*100, valorActual+cantPaquetes);
                temporal.put(horaLlegadaDestino*100+1, valorActual);
            }
        }
    }
    
    
    
    public void imprimirCiudades(){
        Set set = getCiudades().entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            System.out.print(me.getKey() + ": ");
            Ciudad ciudadActual=(Ciudad)me.getValue();

            System.out.println("Nombre:"+ciudadActual.getNombre()+" Cantidad Paquetes: "+ciudadActual.getCantPaquetes());
            
        }
        getCiudades().get("SKBO").print();
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
        
        ArrayList<Integer> cantVisitadasRutas = ciudadO.CantVisitadasrutasXDestino.get(codCiudadFin);
        
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
        Set set = getCiudades().entrySet();
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
        Set set = getCiudades().entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Ciudad ciudadActual=(Ciudad)me.getValue();
             

            BufferedReader archivoCiudad = new BufferedReader(new FileReader("PedidosActuales/arch_"+ciudadActual.getCodigo()+".txt"));
            String linea;
            String[] temporalArchivo=new String[100000];
            int numeroPedidos=0;
            try{

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

    /**
     * @return the ciudades
     */
    public TreeMap<String,Ciudad> getCiudades() {
        return ciudades;
    }

    /**
     * @param ciudades the ciudades to set
     */
    public void setCiudades(TreeMap<String,Ciudad> ciudades) {
        this.ciudades = ciudades;
    }
    
   
    
    private void agregarProyeccionPaquete(RutaEscogida mejorRuta,String codCiudadO,String ciudadLlegada,String fechaPedido,String horaPedido, int dayweek, Paquete nuevoPaquete)
    {
        Ciudad origen=ciudades.get(codCiudadO);
        //actualizamos el almacen del primer lugar de origen hasta que sale el envio
        
        String[] hora=horaPedido.split(":");
        int horaLlegada= Integer.parseInt(hora[0]);//hora en la que llea el pedido        
        
        
        //actualizamos el almacen del primer lugar de origen hasta que sale el envio
        
        ArrayList<Ruta> recorrido= mejorRuta.getListaRutaEscogida();
        
        for(int i=0;i<recorrido.size();++i){
            Ruta punto=recorrido.get(i);
            
            int tiempoEspera = mejorRuta.getTiemposEspera().get(i);
            int tiempoTraslado = mejorRuta.getTiemposTraslado().get(i);
            
            //PARA EL TIEMPO DE ESPERA EN EL ALMACEN HASTA QUE SALGA EL VUELO
            if(horaLlegada+tiempoEspera>23)
            {//si supera el dia esperando el vuelo
                TreeMap proyeccionPedidosDia =(TreeMap) getCiudades().get(punto.getCiudadOrigen()).proyeccionPedidos.get(dayweek);
                int tempHora=horaLlegada;
                while(tempHora<23){
                    ArrayList listaPedidos= (ArrayList)proyeccionPedidosDia.get(tempHora);
                    listaPedidos.add(nuevoPaquete);
                    tempHora++;
                }
                tempHora=0;
                proyeccionPedidosDia =(TreeMap) getCiudades().get(punto.getCiudadOrigen()).proyeccionPedidos.get(dayweek==6?0:dayweek+1);
                while(tempHora<(horaLlegada+tiempoEspera)%24){
                    ArrayList listaPedidos= (ArrayList)proyeccionPedidosDia.get(tempHora);
                    listaPedidos.add(nuevoPaquete);
                    tempHora++;
                }
            }
            else{
                int tempHora=horaLlegada;
                TreeMap proyeccionPedidosDia =(TreeMap) getCiudades().get(punto.getCiudadOrigen()).proyeccionPedidos.get(dayweek);
                while(tempHora<horaLlegada+tiempoEspera){
                    ArrayList listaPedidos= (ArrayList)proyeccionPedidosDia.get(tempHora);
                    listaPedidos.add(nuevoPaquete);
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
                TreeMap proyeccionPedidosDia =(TreeMap) getCiudades().get(punto.getCiudadOrigen()).proyeccionPedidos.get(dayweek);
                ArrayList listaPedidos= (ArrayList)proyeccionPedidosDia.get(horaLlegadaDestino);
                listaPedidos.add(nuevoPaquete);
                
            }
        }
    }
    
    
    public void asignarPedidos(BufferArchivos archPedidos)throws FileNotFoundException{
        TreeMap<Integer,String[]> temporal = archPedidos.getListaPedidosEscala1();
        
        String fechaActual="";
        int numPedido=1;
        for(int i=0;i<temporal.size();++i)
        {
            String[] listaPedidos=temporal.get(i);
            
            for(int x=0;x<listaPedidos.length;++x)
            {
                String pedido = listaPedidos[x];
                String [] datos=pedido.trim().split("-");
                //datosPaquete[3],datosPaquete[4],1,datosPaquete[2],1,datosPaquete[1]
                String codCiudadO=datos[3];
                String codCiudadF=datos[4];
                //int cantPaquetes=Integer.parseInt(datos[2]);
                String horaPedido=datos[2];               
                String fechaLlegadaPaquete=datos[1];
                
                String fechaPedido=fechaLlegadaPaquete.substring(6,8)+"/"+fechaLlegadaPaquete.substring(4,6)+"/"+fechaLlegadaPaquete.substring(0,4);
                
                if(!fechaPedido.equals(fechaActual)){//Se limpia el dia si ha cambiado de todos los almacenes
                    limpiarCapacidad_Almacenes_Rutas(fechaActual);
                    fechaActual=fechaPedido;
                }
                
                Calendar c=Calendar.getInstance();
                try {
                    c.setTime(new SimpleDateFormat("dd/M/yyyy").parse(fechaPedido));
                } catch (ParseException ex) {
                    //Logger.getLogger(DFS.class.getName()).log(Level.SEVERE, null, ex);
                }
                int dayweek=c.get(Calendar.DAY_OF_WEEK)-1;//porque la semana comienza el domingo y el arreglo del 0-6
                
                DFS(codCiudadO,codCiudadF,numPedido,horaPedido,1,fechaPedido,datos[0],null,false,0);
                
                numPedido++;
                if(numPedido==300){
                    int j=0;
                }
                System.out.println(numPedido);
            }
        }
        
        
    }

    private boolean compararSiRutasIguales(RutaEscogida resultadoRuta, RutaEscogida rutaAomitir) {
        ArrayList<Ruta> puntosResultadoRuta= resultadoRuta.getListaRutaEscogida();
        ArrayList<Ruta> puntosRutaAomitir= rutaAomitir.getListaRutaEscogida();
        
        if(puntosResultadoRuta.size()!=puntosRutaAomitir.size())return false;
        
        Iterator<Ruta> iteratorPuntosResultadoRuta = puntosResultadoRuta.iterator();
        Iterator<Ruta> iteratorPuntosAOmitir = puntosRutaAomitir.iterator();
        
        while(iteratorPuntosAOmitir.hasNext()){
            Ruta puntoResultadoRuta=iteratorPuntosResultadoRuta.next();
            Ruta puntoRutaAomitir=iteratorPuntosAOmitir.next();
            
            if(!(puntoResultadoRuta.getCiudadOrigen().equals(puntoRutaAomitir.getCiudadOrigen()) && 
                        puntoResultadoRuta.getCiudadFin().equals(puntoRutaAomitir.getCiudadFin()))) return false;
        }
        
        /*for(Ruta puntoResultadoRuta:puntosResultadoRuta)
        {
            for(Ruta puntoRutaAomitir:puntosRutaAomitir)
            {
                if(!(puntoResultadoRuta.getCiudadOrigen().equals(puntoRutaAomitir.getCiudadOrigen()) && 
                        puntoResultadoRuta.getCiudadFin().equals(puntoRutaAomitir.getCiudadFin()))) return false;
            }
        }*/
        return true;
    }

}
