/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dfs;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

/**
 *
 * @author Sebastián
 */



public class BufferArchivos {
    
    
    //private TreeMap<Integer, TreeMap<Integer,String[]>> listasPorEscala = new TreeMap<Integer, TreeMap<Integer,String[]>>();
    private TreeMap<Integer,String[]> listaPedidosEscala1 =new TreeMap<Integer, String[]>();
    private TreeMap<Integer,String[]> listaPedidosEscala2 =new TreeMap<Integer, String[]>();
    private TreeMap<Integer,String[]> listaPedidosEscala3 =new TreeMap<Integer, String[]>();
    private int escala1=10;
    private int escala2=30;
    private int escala3=6;
    
    public void generarJson(){
        Gson gson = new Gson();

        try (FileWriter writer = new FileWriter("pedidosArutear.json")) {
            gson.toJson(this,writer);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    /*public void organizarPedidos(GestorCiudades gestor) throws ParseException{
        Ciudad siguienteCiudad;
        String fechaActual="";
        boolean primero=true;
        int horaInicio,minutoInicio;
        int limiteInferiorEscalaHora1=0,limiteInferiorEscalaHora2=0,limiteInferiorEscalaHora3=0,limiteSuperiorEscalaHora1=0,limiteSuperiorEscalaHora2=0,limiteSuperiorEscalaHora3=0;
        int limiteInferiorEscalaMinuto1=0,limiteInferiorEscalaMinuto2=0,limiteInferiorEscalaMinuto3=0,limiteSuperiorEscalaMinuto1=0,limiteSuperiorEscalaMinuto2=0,limiteSuperiorEscalaMinuto3=0;
        int numeroPedidosEscala1=0,numeroPedidosEscala2=0,numeroPedidosEscala3=0;
        int indiceEscala1=0,indiceEscala2=0,indiceEscala3=0;
        String[] pedidosAlmacenados1=new String[1000000],pedidosAlmacenados2=new String[1000000],pedidosAlmacenados3=new String[1000000];
        
        int numpedido=0;
        String[] ultimaFecha=new String[3];
        SimpleDateFormat formatter = new SimpleDateFormat("H:m");
        while((siguienteCiudad=gestor.siguienteEnvio()) != null){
            //System.out.println(numpedido++);
            //if(numpedido==10000)break;
            int horaPedido=Integer.parseInt((siguienteCiudad.getUltimaHora().split(":")[0]));
            int minutoPedido=Integer.parseInt((siguienteCiudad.getUltimaHora().split(":")[1]));
            String temporalFecha[]=new String[3];
            temporalFecha=siguienteCiudad.getUltimaFecha().split("/");
            if(!primero && !temporalFecha[0].equals(ultimaFecha[0]))
            {
                String[] temporalAmeter=new String[numeroPedidosEscala1];
                for(int i=0;i<numeroPedidosEscala1;++i) temporalAmeter[i]=pedidosAlmacenados1[i];
                getListaPedidosEscala1().put(indiceEscala1++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                for(int i=0;i<pedidosAlmacenados1.length;++i)pedidosAlmacenados1[i]="";//limpiamos los pedidos
                limiteInferiorEscalaHora1=limiteSuperiorEscalaHora1=limiteInferiorEscalaMinuto1=numeroPedidosEscala1=0;
                limiteSuperiorEscalaMinuto1=escala1;
                
                String[] temporalAmeter2=new String[numeroPedidosEscala2];
                for(int i=0;i<numeroPedidosEscala2;++i) temporalAmeter2[i]=pedidosAlmacenados2[i];
                getListaPedidosEscala2().put(indiceEscala2++, temporalAmeter2);//insertamos los paquetes correspondientes al intervalo
                for(int i=0;i<pedidosAlmacenados2.length;++i)pedidosAlmacenados2[i]="";//limpiamos los pedidos
                limiteInferiorEscalaHora2=limiteSuperiorEscalaHora2=limiteInferiorEscalaMinuto2=numeroPedidosEscala2=0;
                limiteSuperiorEscalaMinuto2=escala2;
                
                
                ultimaFecha=temporalFecha;

            }
            if(primero){
                ultimaFecha=temporalFecha;
                //limites Iniciales
                limiteInferiorEscalaHora1=limiteInferiorEscalaHora2=limiteInferiorEscalaHora3=horaInicio=horaPedido;
                limiteInferiorEscalaMinuto1=limiteInferiorEscalaMinuto2=limiteInferiorEscalaHora3=minutoInicio=minutoPedido;
                
                //minutos y las escalas
                limiteSuperiorEscalaMinuto1+=escala1;
                limiteSuperiorEscalaMinuto2+=escala2;
                limiteSuperiorEscalaMinuto3+=escala3;
                
                //si los minutos son mas de 60
                if(limiteSuperiorEscalaMinuto1>59)
                {
                    limiteSuperiorEscalaMinuto1-=60;
                    limiteSuperiorEscalaHora1++;
                }
                if(limiteSuperiorEscalaMinuto2>59)
                {
                    limiteSuperiorEscalaMinuto2-=60;
                    limiteSuperiorEscalaHora2++;
                }
                if(limiteSuperiorEscalaMinuto3>59)
                {
                    limiteSuperiorEscalaHora3-=60;
                    limiteSuperiorEscalaHora3++;
                }
                
                //horas
                if(limiteSuperiorEscalaHora1>23)limiteSuperiorEscalaHora1-=24;
                if(limiteSuperiorEscalaHora2>23)limiteSuperiorEscalaHora2-=24;
                if(limiteSuperiorEscalaHora3>23)limiteSuperiorEscalaHora3-=24;
                primero=false;
            }
            //cuando el indice menor es realmente menor que el superior numericamente
            if(limiteInferiorEscalaHora1<=limiteSuperiorEscalaHora1)
            {
                //si se encuentra dentro del intervalo
                if(limiteInferiorEscalaMinuto1<limiteSuperiorEscalaMinuto1)
                {
                    if(limiteInferiorEscalaHora1<=horaPedido && limiteSuperiorEscalaHora1>=horaPedido && limiteInferiorEscalaMinuto1<=minutoPedido && limiteSuperiorEscalaMinuto1>=minutoPedido )
                        pedidosAlmacenados1[numeroPedidosEscala1++]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();

                    //en caso no se encuentre dentro del intervalo se modifica el intervalo
                    else
                    {
                        String[] temporalAmeter=new String[numeroPedidosEscala1];
                        for(int i=0;i<numeroPedidosEscala1;++i)temporalAmeter[i]=pedidosAlmacenados1[i];
                        getListaPedidosEscala1().put(indiceEscala1++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                        for(int i=0;i<pedidosAlmacenados1.length;++i)pedidosAlmacenados1[i]="";//limpiamos los pedidos
                        pedidosAlmacenados1[0]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();//se inserta el leido
                        numeroPedidosEscala1=1;//reseteamos el contador de pedidos tomando el cuenta el ya ingresado
                        limiteInferiorEscalaMinuto1+=getEscala1();//se cambian los intervalos
                        limiteSuperiorEscalaMinuto1+=getEscala1();
                        if(limiteInferiorEscalaMinuto1>59)
                        {
                            ++limiteInferiorEscalaHora1;
                            limiteInferiorEscalaMinuto1-=60;
                        }
                        if(limiteSuperiorEscalaMinuto1>59)
                        {
                            ++limiteSuperiorEscalaHora1;
                            limiteSuperiorEscalaMinuto1-=60;
                        }
                        if(limiteInferiorEscalaHora1>23)limiteInferiorEscalaHora1-=24;//se normalizan
                        if(limiteSuperiorEscalaHora1>23)limiteSuperiorEscalaHora1-=24;
                    }
                }
                else
                {
                    if(limiteInferiorEscalaHora1<=horaPedido && limiteSuperiorEscalaHora1>=horaPedido && (limiteInferiorEscalaMinuto1<=minutoPedido || limiteSuperiorEscalaMinuto1>=minutoPedido))
                        pedidosAlmacenados1[numeroPedidosEscala1++]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();

                    //en caso no se encuentre dentro del intervalo se modifica el intervalo
                    else
                    {
                        String[] temporalAmeter=new String[numeroPedidosEscala1];
                        for(int i=0;i<numeroPedidosEscala1;++i)temporalAmeter[i]=pedidosAlmacenados1[i];
                        getListaPedidosEscala1().put(indiceEscala1++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                        for(int i=0;i<pedidosAlmacenados1.length;++i)pedidosAlmacenados1[i]="";//limpiamos los pedidos
                        pedidosAlmacenados1[0]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();//se inserta el leido
                        numeroPedidosEscala1=1;//reseteamos el contador de pedidos tomando el cuenta el ya ingresado
                        limiteInferiorEscalaMinuto1+=getEscala1();//se cambian los intervalos
                        limiteSuperiorEscalaMinuto1+=getEscala1();
                        if(limiteInferiorEscalaMinuto1>59)
                        {
                            ++limiteInferiorEscalaHora1;
                            limiteInferiorEscalaMinuto1-=60;
                        }
                        if(limiteSuperiorEscalaMinuto1>59)
                        {
                            ++limiteSuperiorEscalaHora1;
                            limiteSuperiorEscalaMinuto1-=60;
                        }
                        if(limiteInferiorEscalaMinuto1>59) ++limiteInferiorEscalaHora1;
                        if(limiteSuperiorEscalaMinuto1>59) ++limiteSuperiorEscalaHora1;
                        if(limiteInferiorEscalaHora1>23)limiteInferiorEscalaHora1-=24;//se normalizan
                        if(limiteSuperiorEscalaHora1>23)limiteSuperiorEscalaHora1-=24;
                    }
                }
            }
            else{//minimo 23 maixmo 2 por ejemplo
                if(limiteInferiorEscalaMinuto1<limiteSuperiorEscalaMinuto1)
                {
                    if((limiteInferiorEscalaHora1<= horaPedido || horaPedido <=limiteSuperiorEscalaHora1) && limiteInferiorEscalaMinuto1<=minutoPedido && limiteSuperiorEscalaMinuto1>=minutoPedido)
                        pedidosAlmacenados1[numeroPedidosEscala1++]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();

                    //en caso no se encuentre dentro del intervalo se modifica el intervalo
                    else
                    {
                        String[] temporalAmeter=new String[numeroPedidosEscala1];
                        for(int i=0;i<numeroPedidosEscala1;++i)temporalAmeter[i]=pedidosAlmacenados1[i];
                        getListaPedidosEscala1().put(indiceEscala1++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                        for(int i=0;i<pedidosAlmacenados1.length;++i)pedidosAlmacenados1[i]="";//limpiamos los pedidos
                        pedidosAlmacenados1[0]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                        numeroPedidosEscala1=1;
                        limiteInferiorEscalaMinuto1+=getEscala1();//se cambian los intervalos
                        limiteSuperiorEscalaMinuto1+=getEscala1();
                         if(limiteInferiorEscalaMinuto1>59)
                        {
                            ++limiteInferiorEscalaHora1;
                            limiteInferiorEscalaMinuto1-=60;
                        }
                        if(limiteSuperiorEscalaMinuto1>59)
                        {
                            ++limiteSuperiorEscalaHora1;
                            limiteSuperiorEscalaMinuto1-=60;
                        }
                        if(limiteInferiorEscalaMinuto1>59) ++limiteInferiorEscalaHora1;
                        if(limiteSuperiorEscalaMinuto1>59) ++limiteSuperiorEscalaHora1;
                        if(limiteInferiorEscalaHora1>23)limiteInferiorEscalaHora1-=24;//se normalizan
                        if(limiteSuperiorEscalaHora1>23)limiteSuperiorEscalaHora1-=24;
                    }
                }
                else
                {
                    if((limiteInferiorEscalaHora1<= horaPedido || horaPedido <=limiteSuperiorEscalaHora1) && (limiteInferiorEscalaMinuto1<=minutoPedido || limiteSuperiorEscalaMinuto1>=minutoPedido))
                        pedidosAlmacenados1[numeroPedidosEscala1++]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();

                    //en caso no se encuentre dentro del intervalo se modifica el intervalo
                    else
                    {
                        String[] temporalAmeter=new String[numeroPedidosEscala1];
                        for(int i=0;i<numeroPedidosEscala1;++i)temporalAmeter[i]=pedidosAlmacenados1[i];
                        getListaPedidosEscala1().put(indiceEscala1++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                        for(int i=0;i<pedidosAlmacenados1.length;++i)pedidosAlmacenados1[i]="";//limpiamos los pedidos
                        pedidosAlmacenados1[0]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                        numeroPedidosEscala1=1;
                        limiteInferiorEscalaMinuto1+=getEscala1();//se cambian los intervalos
                        limiteSuperiorEscalaMinuto1+=getEscala1();
                         if(limiteInferiorEscalaMinuto1>59)
                        {
                            ++limiteInferiorEscalaHora1;
                            limiteInferiorEscalaMinuto1-=60;
                        }
                        if(limiteSuperiorEscalaMinuto1>59)
                        {
                            ++limiteSuperiorEscalaHora1;
                            limiteSuperiorEscalaMinuto1-=60;
                        }
                        if(limiteInferiorEscalaMinuto1>59) ++limiteInferiorEscalaHora1;
                        if(limiteSuperiorEscalaMinuto1>59) ++limiteSuperiorEscalaHora1;
                        if(limiteInferiorEscalaHora1>23)limiteInferiorEscalaHora1-=24;//se normalizan
                        if(limiteSuperiorEscalaHora1>23)limiteSuperiorEscalaHora1-=24;
                    }
                }
            }
            //cuando el indice menor es realmente menor que el superior numericamente
            if(limiteInferiorEscalaHora2<=limiteSuperiorEscalaHora2)
            {
                //si se encuentra dentro del intervalo
                if(limiteInferiorEscalaMinuto2<limiteSuperiorEscalaMinuto2)
                {
                    if(limiteInferiorEscalaHora2<=horaPedido && limiteSuperiorEscalaHora2>=horaPedido && limiteInferiorEscalaMinuto2<=minutoPedido && limiteSuperiorEscalaMinuto2>=minutoPedido )
                        pedidosAlmacenados2[numeroPedidosEscala2++]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();

                    //en caso no se encuentre dentro del intervalo se modifica el intervalo
                    else
                    {
                        String[] temporalAmeter=new String[numeroPedidosEscala2];
                        for(int i=0;i<numeroPedidosEscala2;++i)temporalAmeter[i]=pedidosAlmacenados2[i];
                        getListaPedidosEscala2().put(indiceEscala2++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                        for(int i=0;i<pedidosAlmacenados2.length;++i)pedidosAlmacenados2[i]="";//limpiamos los pedidos
                        pedidosAlmacenados2[0]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();//se inserta el leido
                        numeroPedidosEscala2=1;//reseteamos el contador de pedidos tomando el cuenta el ya ingresado
                        limiteInferiorEscalaMinuto2+=getEscala2();//se cambian los intervalos
                        limiteSuperiorEscalaMinuto2+=getEscala2();
                        if(limiteInferiorEscalaMinuto2>59)
                        {
                            ++limiteInferiorEscalaHora2;
                            limiteInferiorEscalaMinuto2-=60;
                        }
                        if(limiteSuperiorEscalaMinuto2>59)
                        {
                            ++limiteSuperiorEscalaHora2;
                            limiteSuperiorEscalaMinuto2-=60;
                        }
                        if(limiteInferiorEscalaHora2>23)limiteInferiorEscalaHora2-=24;//se normalizan
                        if(limiteSuperiorEscalaHora2>23)limiteSuperiorEscalaHora2-=24;
                    }
                }
                else
                {
                    if(limiteInferiorEscalaHora2<=horaPedido && limiteSuperiorEscalaHora2>=horaPedido && (limiteInferiorEscalaMinuto2<=minutoPedido || limiteSuperiorEscalaMinuto2>=minutoPedido))
                        pedidosAlmacenados2[numeroPedidosEscala2++]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();

                    //en caso no se encuentre dentro del intervalo se modifica el intervalo
                    else
                    {
                        String[] temporalAmeter=new String[numeroPedidosEscala2];
                        for(int i=0;i<numeroPedidosEscala2;++i)temporalAmeter[i]=pedidosAlmacenados2[i];
                        getListaPedidosEscala2().put(indiceEscala2++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                        for(int i=0;i<pedidosAlmacenados2.length;++i)pedidosAlmacenados2[i]="";//limpiamos los pedidos
                        pedidosAlmacenados2[0]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();//se inserta el leido
                        numeroPedidosEscala2=1;//reseteamos el contador de pedidos tomando el cuenta el ya ingresado
                        limiteInferiorEscalaMinuto2+=getEscala2();//se cambian los intervalos
                        limiteSuperiorEscalaMinuto2+=getEscala2();
                        if(limiteInferiorEscalaMinuto2>59)
                        {
                            ++limiteInferiorEscalaHora2;
                            limiteInferiorEscalaMinuto2-=60;
                        }
                        if(limiteSuperiorEscalaMinuto2>59)
                        {
                            ++limiteSuperiorEscalaHora2;
                            limiteSuperiorEscalaMinuto2-=60;
                        }
                        if(limiteInferiorEscalaMinuto2>59) ++limiteInferiorEscalaHora2;
                        if(limiteSuperiorEscalaMinuto2>59) ++limiteSuperiorEscalaHora2;
                        if(limiteInferiorEscalaHora2>23)limiteInferiorEscalaHora2-=24;//se normalizan
                        if(limiteSuperiorEscalaHora2>23)limiteSuperiorEscalaHora2-=24;
                    }
                }
            }
            else{//minimo 23 maixmo 2 por ejemplo
                if(limiteInferiorEscalaMinuto2<limiteSuperiorEscalaMinuto2)
                {
                    if((limiteInferiorEscalaHora2<= horaPedido || horaPedido <=limiteSuperiorEscalaHora2) && limiteInferiorEscalaMinuto2<=minutoPedido && limiteSuperiorEscalaMinuto2>=minutoPedido)
                        pedidosAlmacenados2[numeroPedidosEscala2++]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();

                    //en caso no se encuentre dentro del intervalo se modifica el intervalo
                    else
                    {
                        String[] temporalAmeter=new String[numeroPedidosEscala2];
                        for(int i=0;i<numeroPedidosEscala2;++i)temporalAmeter[i]=pedidosAlmacenados2[i];
                        getListaPedidosEscala2().put(indiceEscala2++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                        for(int i=0;i<pedidosAlmacenados2.length;++i)pedidosAlmacenados2[i]="";//limpiamos los pedidos
                        pedidosAlmacenados2[0]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                        numeroPedidosEscala2=1;
                        limiteInferiorEscalaMinuto2+=getEscala2();//se cambian los intervalos
                        limiteSuperiorEscalaMinuto2+=getEscala2();
                         if(limiteInferiorEscalaMinuto2>59)
                        {
                            ++limiteInferiorEscalaHora2;
                            limiteInferiorEscalaMinuto2-=60;
                        }
                        if(limiteSuperiorEscalaMinuto2>59)
                        {
                            ++limiteSuperiorEscalaHora2;
                            limiteSuperiorEscalaMinuto2-=60;
                        }
                        if(limiteInferiorEscalaMinuto2>59) ++limiteInferiorEscalaHora2;
                        if(limiteSuperiorEscalaMinuto2>59) ++limiteSuperiorEscalaHora2;
                        if(limiteInferiorEscalaHora2>23)limiteInferiorEscalaHora2-=24;//se normalizan
                        if(limiteSuperiorEscalaHora2>23)limiteSuperiorEscalaHora2-=24;
                    }
                }
                else
                {
                    if((limiteInferiorEscalaHora2<= horaPedido || horaPedido <=limiteSuperiorEscalaHora2) && (limiteInferiorEscalaMinuto2<=minutoPedido || limiteSuperiorEscalaMinuto2>=minutoPedido))
                        pedidosAlmacenados2[numeroPedidosEscala2++]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();

                    //en caso no se encuentre dentro del intervalo se modifica el intervalo
                    else
                    {
                        String[] temporalAmeter=new String[numeroPedidosEscala2];
                        for(int i=0;i<numeroPedidosEscala2;++i)temporalAmeter[i]=pedidosAlmacenados2[i];
                        getListaPedidosEscala2().put(indiceEscala2++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                        for(int i=0;i<pedidosAlmacenados2.length;++i)pedidosAlmacenados2[i]="";//limpiamos los pedidos
                        pedidosAlmacenados2[0]=siguienteCiudad.getUltimoCodigo()+"-"+temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                        numeroPedidosEscala2=1;
                        limiteInferiorEscalaMinuto2+=getEscala2();//se cambian los intervalos
                        limiteSuperiorEscalaMinuto2+=getEscala2();
                         if(limiteInferiorEscalaMinuto2>59)
                        {
                            ++limiteInferiorEscalaHora2;
                            limiteInferiorEscalaMinuto2-=60;
                        }
                        if(limiteSuperiorEscalaMinuto2>59)
                        {
                            ++limiteSuperiorEscalaHora2;
                            limiteSuperiorEscalaMinuto2-=60;
                        }
                        if(limiteInferiorEscalaMinuto2>59) ++limiteInferiorEscalaHora2;
                        if(limiteSuperiorEscalaMinuto2>59) ++limiteSuperiorEscalaHora2;
                        if(limiteInferiorEscalaHora2>23)limiteInferiorEscalaHora2-=24;//se normalizan
                        if(limiteSuperiorEscalaHora2>23)limiteSuperiorEscalaHora2-=24;
                    }
                }
            }
            
            siguienteCiudad.avanzarBuffer();
        }
    }

    /**
     * @return the listaPedidosEscala1
     */
    public TreeMap<Integer,String[]> getListaPedidosEscala1() {
        return listaPedidosEscala1;
    }

    /**
     * @param listaPedidosEscala1 the listaPedidosEscala1 to set
     */
    public void setListaPedidosEscala1(TreeMap<Integer,String[]> listaPedidosEscala1) {
        this.listaPedidosEscala1 = listaPedidosEscala1;
    }

    /**
     * @return the listaPedidosEscala2
     */
    public TreeMap<Integer,String[]> getListaPedidosEscala2() {
        return listaPedidosEscala2;
    }

    /**
     * @param listaPedidosEscala2 the listaPedidosEscala2 to set
     */
    public void setListaPedidosEscala2(TreeMap<Integer,String[]> listaPedidosEscala2) {
        this.listaPedidosEscala2 = listaPedidosEscala2;
    }

    /**
     * @return the listaPedidosEscala3
     */
    public TreeMap<Integer,String[]> getListaPedidosEscala3() {
        return listaPedidosEscala3;
    }

    /**
     * @param listaPedidosEscala3 the listaPedidosEscala3 to set
     */
    public void setListaPedidosEscala3(TreeMap<Integer,String[]> listaPedidosEscala3) {
        this.listaPedidosEscala3 = listaPedidosEscala3;
    }

    /**
     * @return the escala1
     */
    public int getEscala1() {
        return escala1;
    }

    /**
     * @param escala1 the escala1 to set
     */
    public void setEscala1(int escala1) {
        this.escala1 = escala1;
    }

    /**
     * @return the escala2
     */
    public int getEscala2() {
        return escala2;
    }

    /**
     * @param escala2 the escala2 to set
     */
    public void setEscala2(int escala2) {
        this.escala2 = escala2;
    }

    /**
     * @return the escala3
     */
    public int getEscala3() {
        return escala3;
    }

    /**
     * @param escala3 the escala3 to set
     */
    public void setEscala3(int escala3) {
        this.escala3 = escala3;
    }


}
