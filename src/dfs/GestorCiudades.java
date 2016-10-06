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
    
    private void instanciarVecesRecorridasCiudades(){
        Set set = ciudades.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Ciudad ciudadActual=(Ciudad)me.getValue();
            ciudadActual.instanciarCantidadIdasRutasAnexas();
            
        }
        
    }
    
    public GestorCiudades(String archVuelos,String archAeropuertos,String archHusos) throws FileNotFoundException{
        leerCiudades(archAeropuertos,archHusos);
        leerRutas(archVuelos);
        instanciarVecesRecorridasCiudades();
        this.rnd=new Random();
    }
    
    private void limpiarAlmacenes(String fechaActual){
        if(fechaActual.equals(""))return;
        
        Calendar c=Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("dd/M/yyyy").parse(fechaActual));
        } catch (ParseException ex) {
            Logger.getLogger(DFS.class.getName()).log(Level.SEVERE, null, ex);
        }
        int dayweek=c.get(Calendar.DAY_OF_WEEK)-2;//porque la semana comienza el domingo y el arreglo del 0-6
        
        //recorremos cada ciudad para limpiar las proyecciones una vez cambiado el dia
        Set set = ciudades.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Ciudad ciudadActual=(Ciudad)me.getValue();
            
            TreeMap proyecciones = (TreeMap)ciudadActual.proyeccionAlmacen.get(dayweek);
            for(int j=0;j<24;++j){
                proyecciones.put(j*100, 0);
                proyecciones.put(j*100+1, 0);
            }
        }
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
                //String horaPedido="Humo";
                String fechaPedido=datos[4];
                if(!fechaPedido.equals(fechaActual)){
                    limpiarAlmacenes(fechaActual);
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
        
    private void DFS(String codCiudadO,String codCiudadF,int numPedido,/*int horaPedido,*/String horaPedido,int cantPaquetes,String fechaPedido){
        Ciudad ciudadO=ciudades.get(codCiudadO);
        Ciudad ciudadF=ciudades.get(codCiudadF);
        int maxTiempoVuelo;
        if(ciudadO.getContinente().equals(ciudadF.getContinente()))
            maxTiempoVuelo=maxTiempoContinental;
        else
            maxTiempoVuelo=maxTiempoIntercontinental;
        
        
        RutaEscogida mejorRuta= new RutaEscogida(1000);//Numero alto para que sea reemplazado en le primera iteración
        int indiceRutaEscogida=-1;
        
        ArrayList<Ruta> RutasAnexadasO=ciudadO.getRutasAnexas();
        int cantidadAnexos=RutasAnexadasO.size();
        int cantAnexosRevisados=0;
        int encontroAlMenosUno=0; 
        int porcCantAnexos=cantidadAnexos/4;//PORCENTAJE DE LAS RUTAS QUE SE ESTÁN EVALUANDO
        
        ArrayList<Integer> listaRutasAEscoger=crearListaAEscoger(ciudadO);
        
        while(cantAnexosRevisados<cantidadAnexos && encontroAlMenosUno==0){
            int cantPorcAnexosRevisados=porcCantAnexos;
            while(cantPorcAnexosRevisados>0 && cantAnexosRevisados<cantidadAnexos){
                
                int indRutaListaARevisar=rnd.nextInt(listaRutasAEscoger.size());//Aqui se puede meter el colonia de hormigas                
                int indRutaARevisar=listaRutasAEscoger.get(indRutaListaARevisar);
                removerElementos_Lista(indRutaARevisar,listaRutasAEscoger);// Se remueve los elementos repetidos
                
                Ruta rutaActual=RutasAnexadasO.get(indRutaARevisar);


                RutaEscogida resultadoRutaInicial = new RutaEscogida(0);

                RutaEscogida resultadoRuta=recursiveSearch(rutaActual,resultadoRutaInicial,maxTiempoVuelo,codCiudadF,9);//era 9 aca 
                if(resultadoRuta!=null){
                    //AQUI SE EVALUA LO QUE SE TENGA QUE EVALUAR PARA ESCOGER EL MEJOR

                    if(resultadoRuta.getTiempoRuta()<mejorRuta.getTiempoRuta()){
                        mejorRuta=resultadoRuta;
                        indiceRutaEscogida=indRutaARevisar;//Se guarda el indice de la rutaescogida para agregar en uno dicha ciudad
                        encontroAlMenosUno=1;//Se encontro una ruta disponible
                    }

                }
                cantPorcAnexosRevisados--;
                cantAnexosRevisados++;
                
            }
        }
        if(mejorRuta.getTiempoRuta()==1000)
            System.out.println("Numero de Pedido: "+numPedido+" No se encontró ruta");
        else{
            System.out.println("Numero de Pedido: "+numPedido+ " Mejor Ruta: "+mejorRuta.imprimirRecorrido()+" Mejor Tiempo: "+mejorRuta.getTiempoRuta());
            ciudadO.incrementarRutaEscogida(indiceRutaEscogida);
            //agregarPaquetes_Ciudades(mejorRuta,cantPaquetes);
            //this.TiempoEntregaPaquetes+=mejorTiempo*cantPaquetes;
            actualizarAlmacen(mejorRuta, cantPaquetes,horaPedido,fechaPedido,codCiudadF);
        }
    }
    
    private void actualizarAlmacen(RutaEscogida mejorRuta,int cantPaquetes,String horaPedido,String fechaPedido,String ciudadLlegada){
        String[] hora=horaPedido.split(":");
        int horaLlegada= Integer.parseInt(hora[0]);//hora en la que llea el pedido
        //int horaTotal = mejorTiempo+ horaLlegada;
        //String[] puntos=mejorRuta.split("-");//puntos por los que pasa el envio
        
        //dia de la semana en la que llega el paquete
        Calendar c=Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("dd/M/yyyy").parse(fechaPedido));
        } catch (ParseException ex) {
            Logger.getLogger(DFS.class.getName()).log(Level.SEVERE, null, ex);
        }
        int dayweek=c.get(Calendar.DAY_OF_WEEK)-2;//porque la semana comienza el domingo y el arreglo del 0-6
        
        //actualizamos el almacen del primer lugar de origen hasta que sale el envio
        
        ArrayList<Ruta> recorrido= mejorRuta.getListaRutaEscogida();
        
        for(int i=0;i<recorrido.size();++i){
            Ruta punto=recorrido.get(i);
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
                temporal =(TreeMap) ciudades.get(punto.getCiudadOrigen()).proyeccionAlmacen.get(dayweek+1);
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
                    ++dayweek;
                    horaLlegadaDestino-=24;
                }
                TreeMap temporal =(TreeMap) ciudades.get(punto.getCiudadFin()).proyeccionAlmacen.get(dayweek);
                int valorActual=(int)temporal.get(horaLlegadaDestino*100+1);
                temporal.put(horaLlegadaDestino*100, valorActual+cantPaquetes);
                temporal.put(horaLlegadaDestino*100+1, valorActual);
            }
        }
    }
    
    /*private void actualizarAlmacen(String mejorRuta,int cantPaquetes,String horaPedido,String fechaPedido,int mejorTiempo,String ciudadLlegada){
        String[] hora=horaPedido.split(":");
        int horaLlegada= Integer.parseInt(hora[0]);//hora en la que llea el pedido
        //int horaTotal = mejorTiempo+ horaLlegada;
        String[] puntos=mejorRuta.split("-");//puntos por los que pasa el envio
        
        //dia de la semana en la que llega el paquete
        Calendar c=Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("dd/M/yyyy").parse(fechaPedido));
        } catch (ParseException ex) {
            Logger.getLogger(DFS.class.getName()).log(Level.SEVERE, null, ex);
        }
        int dayweek=c.get(Calendar.DAY_OF_WEEK)-2;//porque la semana comienza el domingo y el arreglo del 0-6
        
        //actualizamos el almacen del primer lugar de origen hasta que sale el envio
        for (Ruta temp : ciudades.get(puntos[0]).rutasAnexas) {
            if(temp.getCiudadFin().equals(puntos[1])){
                String[] horasModificadas=temp.getHoraOrigen().split(":");
                int horaSalidaVuelo=Integer.parseInt(horasModificadas[0]);
                if(horaSalidaVuelo<horaLlegada){
                    int tempHora=horaLlegada;
                    TreeMap temporal =(TreeMap) ciudades.get(puntos[0]).proyeccionAlmacen.get(dayweek);
                    while(tempHora<23){
                        int valorActual=(int)temporal.get(tempHora*100+1);//aqui seria la hora no 900
                        temporal.put(tempHora*100, valorActual+cantPaquetes);
                        temporal.put(tempHora*100+1, valorActual+cantPaquetes);
                        tempHora++;
                    }
                    tempHora=0;
                    temporal =(TreeMap) ciudades.get(puntos[0]).proyeccionAlmacen.get(dayweek+1);
                    while(tempHora<horaSalidaVuelo){
                        int valorActual=(int)temporal.get(tempHora*100+1);//aqui seria la hora no 900
                        temporal.put(tempHora*100, valorActual+cantPaquetes);
                        temporal.put(tempHora*100+1, valorActual+cantPaquetes);
                        tempHora++;
                    }
                }
                else{
                    int tempHora=horaLlegada;
                    TreeMap temporal =(TreeMap) ciudades.get(puntos[0]).proyeccionAlmacen.get(dayweek);
                    while(tempHora<horaSalidaVuelo){
                        int valorActual=(int)temporal.get(tempHora*100+1);//aqui seria la hora no 900
                        temporal.put(tempHora*100, valorActual+cantPaquetes);
                        temporal.put(tempHora*100+1, valorActual+cantPaquetes);
                        tempHora++;
                    }
                }
                horasModificadas=temp.getHoraFin().split(":");
                horaLlegada=Integer.parseInt(horasModificadas[0]);
                break;
            }
        }
        //actualizamos los almacenes por cada punto que se recorre durante el viaje
        int k=0;
        for (String ciudad : puntos) {
            if(k==0)continue;//porque ya se realizo el primer punto
            else if(k==puntos.length)break;
            k++;
//            TreeMap temp =(TreeMap) ciudades.get(ciudad).proyeccionAlmacen.get(dayweek);
//            int valorActual=(int)temp.get(horaLlegada*100+1);//aqui seria la hora no 900
//            temp.put(horaLlegada*100, valorActual+cantPaquetes);
//            if(ciudad.equals(ciudadLlegada))temp.put(horaLlegada*100+1, valorActual);
//            else temp.put(horaLlegada*100+1, valorActual+cantPaquetes);
    
    
    
    
            for (Ruta temp : ciudades.get(ciudad).rutasAnexas) {
                if(temp.getCiudadFin().equals(puntos[k+1])){
                    String[] horasModificadas=temp.getHoraOrigen().split(":");
                    int horaSalidaVuelo=Integer.parseInt(horasModificadas[0]);
                    if(horaSalidaVuelo<horaLlegada){
                        int tempHora=horaLlegada;
                        TreeMap temporal =(TreeMap) ciudades.get(ciudad).proyeccionAlmacen.get(dayweek);
                        while(tempHora<23){
                            int valorActual=(int)temporal.get(tempHora*100+1);//aqui seria la hora no 900
                            temporal.put(tempHora*100, valorActual+cantPaquetes);
                            temporal.put(tempHora*100+1, valorActual+cantPaquetes);
                            tempHora++;
                        }
                        tempHora=0;
                        temporal =(TreeMap) ciudades.get(ciudad).proyeccionAlmacen.get(dayweek+1);
                        while(tempHora<horaSalidaVuelo){
                            int valorActual=(int)temporal.get(tempHora*100+1);//aqui seria la hora no 900
                            temporal.put(tempHora*100, valorActual+cantPaquetes);
                            temporal.put(tempHora*100+1, valorActual+cantPaquetes);
                            tempHora++;
                        }
                    }
                    else{
                        int tempHora=horaLlegada;
                        TreeMap temporal =(TreeMap) ciudades.get(ciudad).proyeccionAlmacen.get(dayweek);
                        while(tempHora<horaSalidaVuelo){
                            int valorActual=(int)temporal.get(tempHora*100+1);//aqui seria la hora no 900
                            temporal.put(tempHora*100, valorActual+cantPaquetes);
                            temporal.put(tempHora*100+1, valorActual+cantPaquetes);
                            tempHora++;
                        }
                    }
                    horasModificadas=temp.getHoraFin().split(":");
                    horaLlegada=Integer.parseInt(horasModificadas[0]);
                    break;
                }
            }
        }
    }*/
    
    private void agregarPaquetes_Ciudades(String rutaEscogida,int cantPaquetesEnviados){
        
        
        String[] ciudadesTranscurridas=rutaEscogida.trim().split("-");
        for(String codCiudadActual:ciudadesTranscurridas){
            Ciudad ciudadActual=ciudades.get(codCiudadActual);
            int cantPaquetesActual=ciudadActual.getCantPaquetes();
            ciudadActual.setCantPaquetes(cantPaquetesActual+cantPaquetesEnviados);
        }
        
    }
    
    private RutaEscogida recursiveSearch(Ruta rutaActual,RutaEscogida resultadoRuta,int maxTiempoVuelo,String ciudadFinal,int horaPartida){
        
        int tiempoTraslado=calcularTiempoTraslado(rutaActual);
        int tiempoEspera=calcularTiempoEspera(rutaActual, horaPartida);
        
        //int tiempoTraslado_Espera=calcularTiempo(rutaActual,horaPartida);
        int tiempoTotalActualizado=resultadoRuta.getTiempoRuta()+tiempoTraslado+tiempoEspera;
        
        if(tiempoTotalActualizado>maxTiempoVuelo){ // FALTA REVISAR LO DE LAS CAPACIDADES DE CIUDADES
            return null;
        }else{
            if(rutaActual.getCiudadFin().equals(ciudadFinal)){
                RutaEscogida newresultadoRuta= new RutaEscogida(resultadoRuta);
                
                newresultadoRuta.agregarRuta(rutaActual);
                newresultadoRuta.agregarTiempoEspera(tiempoEspera);
                newresultadoRuta.agregarTiempoTraslado(tiempoTraslado);
                newresultadoRuta.actualizarTiempoRuta(tiempoTraslado, tiempoEspera);
                
                return newresultadoRuta;
            }
            
            if(tiempoTotalActualizado==maxTiempoVuelo)//Si ya llego al tope, no vale la pena seguir buscando caminos
                return null;
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
            
            RutaEscogida newResultadoRuta=recursiveSearch(rutaNueva,updateResultadoRuta,maxTiempoVuelo,ciudadFinal,horaPartida);//era 9 aca
            
            //MejorRuta resultadoRuta=recursiveSearch(rutaNueva,tiempoTotalActualizado,maxTiempoVuelo,ciudadFinal,seguimientoRuta+"-"+rutaNueva.getCiudadOrigen(),horaPartida);
            if(newResultadoRuta!=null){
                if(newResultadoRuta.getTiempoRuta()<mejorRuta.getTiempoRuta()){
                    mejorRuta=newResultadoRuta;
                }                
            }
            
        }
        if(mejorRuta.getTiempoRuta()==10000)
            return null;
        else{
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
    
    private ArrayList<Integer> crearListaAEscoger(Ciudad ciudadO){        
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
    
}
