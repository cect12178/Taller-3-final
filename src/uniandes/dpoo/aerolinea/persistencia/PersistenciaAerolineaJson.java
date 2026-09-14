package uniandes.dpoo.aerolinea.persistencia;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uniandes.dpoo.aerolinea.exceptions.AeropuertoDuplicadoException;
import uniandes.dpoo.aerolinea.exceptions.InformacionInconsistenteException;
import uniandes.dpoo.aerolinea.modelo.Aerolinea;
import uniandes.dpoo.aerolinea.modelo.Aeropuerto;
import uniandes.dpoo.aerolinea.modelo.Avion;
import uniandes.dpoo.aerolinea.modelo.Ruta;
import uniandes.dpoo.aerolinea.modelo.Vuelo;


public class PersistenciaAerolineaJson implements IPersistenciaAerolinea
{
    private static final String AEROPUERTOS = "aeropuertos";
    private static final String AVIONES = "aviones";
    private static final String RUTAS = "rutas";
    private static final String VUELOS = "vuelos";

    private static final String NOMBRE = "nombre";
    private static final String CODIGO = "codigo";
    private static final String NOMBRE_CIUDAD = "nombreCiudad";
    private static final String LATITUD = "latitud";
    private static final String LONGITUD = "longitud";

    private static final String CAPACIDAD = "capacidad";

    private static final String CODIGO_RUTA = "codigoRuta";
    private static final String ORIGEN = "origen";
    private static final String DESTINO = "destino";
    private static final String HORA_SALIDA = "horaSalida";
    private static final String HORA_LLEGADA = "horaLlegada";

    private static final String FECHA = "fecha";
    private static final String AVION = "avion";

    @Override
    public void cargarAerolinea( String archivo, Aerolinea aerolinea ) throws IOException, InformacionInconsistenteException
    {
        String jsonCompleto = new String( Files.readAllBytes( new File( archivo ).toPath( ) ) );
        JSONObject raiz = new JSONObject( jsonCompleto );

        Map<String, Aeropuerto> aeropuertos = cargarAeropuertos( raiz.getJSONArray( AEROPUERTOS ) );
        cargarAviones( aerolinea, raiz.getJSONArray( AVIONES ) );
        cargarRutas( aerolinea, raiz.getJSONArray( RUTAS ), aeropuertos );
        cargarVuelos( aerolinea, raiz.getJSONArray( VUELOS ) );
    }

   
    private Map<String, Aeropuerto> cargarAeropuertos( JSONArray jAeropuertos ) throws InformacionInconsistenteException
    {
        Map<String, Aeropuerto> aeropuertos = new HashMap<String, Aeropuerto>( );
        for( int i = 0; i < jAeropuertos.length( ); i++ )
        {
            JSONObject jAeropuerto = jAeropuertos.getJSONObject( i );
            try
            {
                Aeropuerto nuevo = new Aeropuerto( jAeropuerto.getString( NOMBRE ), jAeropuerto.getString( CODIGO ), jAeropuerto.getString( NOMBRE_CIUDAD ),
                        jAeropuerto.getDouble( LATITUD ), jAeropuerto.getDouble( LONGITUD ) );
                aeropuertos.put( nuevo.getCodigo( ), nuevo );
            }
            catch( AeropuertoDuplicadoException e )
            {
                throw new InformacionInconsistenteException( e.getMessage( ) );
            }
        }
        return aeropuertos;
    }

    private void cargarAviones( Aerolinea aerolinea, JSONArray jAviones )
    {
        for( int i = 0; i < jAviones.length( ); i++ )
        {
            JSONObject jAvion = jAviones.getJSONObject( i );
            Avion nuevo = new Avion( jAvion.getString( NOMBRE ), jAvion.getInt( CAPACIDAD ) );
            aerolinea.agregarAvion( nuevo );
        }
    }

    private void cargarRutas( Aerolinea aerolinea, JSONArray jRutas, Map<String, Aeropuerto> aeropuertos ) throws InformacionInconsistenteException
    {
        for( int i = 0; i < jRutas.length( ); i++ )
        {
            JSONObject jRuta = jRutas.getJSONObject( i );

            String codigoOrigen = jRuta.getString( ORIGEN );
            String codigoDestino = jRuta.getString( DESTINO );

            Aeropuerto origen = aeropuertos.get( codigoOrigen );
            Aeropuerto destino = aeropuertos.get( codigoDestino );

            if( origen == null )
                throw new InformacionInconsistenteException( "No existe un aeropuerto con el código " + codigoOrigen );
            if( destino == null )
                throw new InformacionInconsistenteException( "No existe un aeropuerto con el código " + codigoDestino );

            Ruta nueva = new Ruta( origen, destino, jRuta.getString( HORA_SALIDA ), jRuta.getString( HORA_LLEGADA ), jRuta.getString( CODIGO_RUTA ) );
            aerolinea.agregarRuta( nueva );
        }
    }

  
    private void cargarVuelos( Aerolinea aerolinea, JSONArray jVuelos ) throws InformacionInconsistenteException
    {
        for( int i = 0; i < jVuelos.length( ); i++ )
        {
            JSONObject jVuelo = jVuelos.getJSONObject( i );

            String codigoRuta = jVuelo.getString( CODIGO_RUTA );
            String fecha = jVuelo.getString( FECHA );
            String nombreAvion = jVuelo.getString( AVION );

            try
            {
                aerolinea.programarVuelo( fecha, codigoRuta, nombreAvion );
            }
            catch( Exception e )
            {
                throw new InformacionInconsistenteException( e.getMessage( ) );
            }
        }
    }

    @Override
    public void salvarAerolinea( String archivo, Aerolinea aerolinea ) throws IOException
    {
        JSONObject jobject = new JSONObject( );

        salvarAeropuertos( aerolinea, jobject );
        salvarAviones( aerolinea, jobject );
        salvarRutas( aerolinea, jobject );
        salvarVuelos( aerolinea, jobject );

        PrintWriter pw = new PrintWriter( archivo );
        jobject.write( pw, 2, 0 );
        pw.close( );
    }

    private void salvarAeropuertos( Aerolinea aerolinea, JSONObject jobject )
    {
        JSONArray jAeropuertos = new JSONArray( );
        Map<String, Aeropuerto> aeropuertosUnicos = new HashMap<String, Aeropuerto>( );

        for( Ruta ruta : aerolinea.getRutas( ) )
        {
            aeropuertosUnicos.put( ruta.getOrigen( ).getCodigo( ), ruta.getOrigen( ) );
            aeropuertosUnicos.put( ruta.getDestino( ).getCodigo( ), ruta.getDestino( ) );
        }

        for( Aeropuerto aeropuerto : aeropuertosUnicos.values( ) )
        {
            JSONObject jAeropuerto = new JSONObject( );
            jAeropuerto.put( NOMBRE, aeropuerto.getNombre( ) );
            jAeropuerto.put( CODIGO, aeropuerto.getCodigo( ) );
            jAeropuerto.put( NOMBRE_CIUDAD, aeropuerto.getNombreCiudad( ) );
            jAeropuerto.put( LATITUD, aeropuerto.getLatitud( ) );
            jAeropuerto.put( LONGITUD, aeropuerto.getLongitud( ) );
            jAeropuertos.put( jAeropuerto );
        }

        jobject.put( AEROPUERTOS, jAeropuertos );
    }

    private void salvarAviones( Aerolinea aerolinea, JSONObject jobject )
    {
        JSONArray jAviones = new JSONArray( );
        for( Avion avion : aerolinea.getAviones( ) )
        {
            JSONObject jAvion = new JSONObject( );
            jAvion.put( NOMBRE, avion.getNombre( ) );
            jAvion.put( CAPACIDAD, avion.getCapacidad( ) );
            jAviones.put( jAvion );
        }
        jobject.put( AVIONES, jAviones );
    }

    private void salvarRutas( Aerolinea aerolinea, JSONObject jobject )
    {
        JSONArray jRutas = new JSONArray( );
        for( Ruta ruta : aerolinea.getRutas( ) )
        {
            JSONObject jRuta = new JSONObject( );
            jRuta.put( CODIGO_RUTA, ruta.getCodigoRuta( ) );
            jRuta.put( ORIGEN, ruta.getOrigen( ).getCodigo( ) );
            jRuta.put( DESTINO, ruta.getDestino( ).getCodigo( ) );
            jRuta.put( HORA_SALIDA, ruta.getHoraSalida( ) );
            jRuta.put( HORA_LLEGADA, ruta.getHoraLlegada( ) );
            jRutas.put( jRuta );
        }
        jobject.put( RUTAS, jRutas );
    }

    private void salvarVuelos( Aerolinea aerolinea, JSONObject jobject )
    {
        JSONArray jVuelos = new JSONArray( );
        for( Vuelo vuelo : aerolinea.getVuelos( ) )
        {
            JSONObject jVuelo = new JSONObject( );
            jVuelo.put( CODIGO_RUTA, vuelo.getRuta( ).getCodigoRuta( ) );
            jVuelo.put( FECHA, vuelo.getFecha( ) );
            jVuelo.put( AVION, vuelo.getAvion( ).getNombre( ) );
            jVuelos.put( jVuelo );
        }
        jobject.put( VUELOS, jVuelos );
    }

}