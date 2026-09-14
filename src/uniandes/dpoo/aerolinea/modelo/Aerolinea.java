package uniandes.dpoo.aerolinea.modelo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uniandes.dpoo.aerolinea.exceptions.InformacionInconsistenteException;
import uniandes.dpoo.aerolinea.exceptions.VueloSobrevendidoException;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.modelo.tarifas.CalculadoraTarifas;
import uniandes.dpoo.aerolinea.modelo.tarifas.CalculadoraTarifasTemporadaAlta;
import uniandes.dpoo.aerolinea.modelo.tarifas.CalculadoraTarifasTemporadaBaja;
import uniandes.dpoo.aerolinea.persistencia.CentralPersistencia;
import uniandes.dpoo.aerolinea.persistencia.IPersistenciaAerolinea;
import uniandes.dpoo.aerolinea.persistencia.IPersistenciaTiquetes;
import uniandes.dpoo.aerolinea.persistencia.TipoInvalidoException;
import uniandes.dpoo.aerolinea.tiquetes.Tiquete;


public class Aerolinea
{

    private List<Avion> aviones;


    private Map<String, Ruta> rutas;

 
    private List<Vuelo> vuelos;

   
    private Map<String, Cliente> clientes;

    
    public Aerolinea( )
    {
        aviones = new LinkedList<Avion>( );
        rutas = new HashMap<String, Ruta>( );
        vuelos = new LinkedList<Vuelo>( );
        clientes = new HashMap<String, Cliente>( );
    }


    public void agregarRuta( Ruta ruta )
    {
        this.rutas.put( ruta.getCodigoRuta( ), ruta );
    }

 
    public void agregarAvion( Avion avion )
    {
        this.aviones.add( avion );
    }

  
    public void agregarCliente( Cliente cliente )
    {
        this.clientes.put( cliente.getIdentificador( ), cliente );
    }

  
    public boolean existeCliente( String identificadorCliente )
    {
        return this.clientes.containsKey( identificadorCliente );
    }

   
    public Cliente getCliente( String identificadorCliente )
    {
        return this.clientes.get( identificadorCliente );
    }

   
    public Collection<Avion> getAviones( )
    {
        return aviones;
    }

   
    public Collection<Ruta> getRutas( )
    {
        return rutas.values( );
    }

 
    public Ruta getRuta( String codigoRuta )
    {
        return rutas.get( codigoRuta );
    }

 
    public Collection<Vuelo> getVuelos( )
    {
        return vuelos;
    }

  
    public Vuelo getVuelo( String codigoRuta, String fechaVuelo )
    {
        for( Vuelo vuelo : vuelos )
        {
            if( vuelo.getRuta( ).getCodigoRuta( ).equals( codigoRuta ) && vuelo.getFecha( ).equals( fechaVuelo ) )
                return vuelo;
        }
        return null;
    }

  
    public Collection<Cliente> getClientes( )
    {
        return clientes.values( );
    }

  
    public Collection<Tiquete> getTiquetes( )
    {
        List<Tiquete> todosLosTiquetes = new LinkedList<Tiquete>( );
        for( Vuelo vuelo : vuelos )
            todosLosTiquetes.addAll( vuelo.getTiquetes( ) );

        return todosLosTiquetes;
    }

 
  
    public void cargarAerolinea( String archivo, String tipoArchivo ) throws TipoInvalidoException, IOException, InformacionInconsistenteException
    {
        IPersistenciaAerolinea cargador = CentralPersistencia.getPersistenciaAerolinea( tipoArchivo );
        cargador.cargarAerolinea( archivo, this );
    }

  
    public void salvarAerolinea( String archivo, String tipoArchivo ) throws TipoInvalidoException, IOException
    {
        IPersistenciaAerolinea cargador = CentralPersistencia.getPersistenciaAerolinea( tipoArchivo );
        cargador.salvarAerolinea( archivo, this );
    }

 
    public void cargarTiquetes( String archivo, String tipoArchivo ) throws TipoInvalidoException, IOException, InformacionInconsistenteException
    {
        IPersistenciaTiquetes cargador = CentralPersistencia.getPersistenciaTiquetes( tipoArchivo );
        cargador.cargarTiquetes( archivo, this );
    }

   
    public void salvarTiquetes( String archivo, String tipoArchivo ) throws TipoInvalidoException, IOException
    {
        IPersistenciaTiquetes cargador = CentralPersistencia.getPersistenciaTiquetes( tipoArchivo );
        cargador.salvarTiquetes( archivo, this );
    }

   
    public void programarVuelo( String fecha, String codigoRuta, String nombreAvion ) throws Exception
    {
        Ruta ruta = getRuta( codigoRuta );
        if( ruta == null )
            throw new Exception( "No existe una ruta con el código " + codigoRuta );

        Avion avion = null;
        for( Avion unAvion : aviones )
        {
            if( unAvion.getNombre( ).equals( nombreAvion ) )
            {
                avion = unAvion;
                break;
            }
        }
        if( avion == null )
            throw new Exception( "No existe un avión con el nombre " + nombreAvion );

        for( Vuelo vueloExistente : vuelos )
        {
            if( vueloExistente.getAvion( ) == avion && vueloExistente.getFecha( ).equals( fecha ) && seSolapan( vueloExistente.getRuta( ), ruta ) )
                throw new Exception( "El avión " + nombreAvion + " ya está ocupado en ese horario el día " + fecha );
        }

        Vuelo nuevoVuelo = new Vuelo( ruta, fecha, avion );
        vuelos.add( nuevoVuelo );
    }

  
    private boolean seSolapan( Ruta ruta1, Ruta ruta2 )
    {
        int inicio1 = Ruta.getHoras( ruta1.getHoraSalida( ) ) * 60 + Ruta.getMinutos( ruta1.getHoraSalida( ) );
        int fin1 = inicio1 + ruta1.getDuracion( );

        int inicio2 = Ruta.getHoras( ruta2.getHoraSalida( ) ) * 60 + Ruta.getMinutos( ruta2.getHoraSalida( ) );
        int fin2 = inicio2 + ruta2.getDuracion( );

        return inicio1 < fin2 && inicio2 < fin1;
    }

   
    public int venderTiquetes( String identificadorCliente, String fecha, String codigoRuta, int cantidad ) throws VueloSobrevendidoException, Exception
    {
        Cliente cliente = getCliente( identificadorCliente );
        if( cliente == null )
            throw new Exception( "No existe un cliente con el identificador " + identificadorCliente );

        Vuelo vuelo = getVuelo( codigoRuta, fecha );
        if( vuelo == null )
            throw new Exception( "No existe un vuelo para la ruta " + codigoRuta + " en la fecha " + fecha );

        CalculadoraTarifas calculadora = esTemporadaAlta( fecha ) ? new CalculadoraTarifasTemporadaAlta( ) : new CalculadoraTarifasTemporadaBaja( );

        return vuelo.venderTiquetes( cliente, calculadora, cantidad );
    }

   
    private boolean esTemporadaAlta( String fecha )
    {
        int mes = Integer.parseInt( fecha.substring( 5, 7 ) );
        boolean temporadaBaja = ( mes >= 1 && mes <= 5 ) || ( mes >= 9 && mes <= 11 );
        return !temporadaBaja;
    }

    
    public void registrarVueloRealizado( String fecha, String codigoRuta )
    {
        Vuelo vuelo = getVuelo( codigoRuta, fecha );
        if( vuelo != null )
        {
            for( Cliente cliente : clientes.values( ) )
                cliente.usarTiquetes( vuelo );
        }
    }

    public String consultarSaldoPendienteCliente( String identificadorCliente )
    {
        Cliente cliente = getCliente( identificadorCliente );
        if( cliente == null )
            return "";

        return "" + cliente.calcularValorTotalTiquetes( );
    }

}