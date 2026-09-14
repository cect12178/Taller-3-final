package uniandes.dpoo.aerolinea.modelo;


public class Ruta
{
  
    private Aeropuerto origen;

  
    private Aeropuerto destino;

  
    private String horaSalida;

   
    private String horaLlegada;

  
    private String codigoRuta;

    public Ruta( Aeropuerto origen, Aeropuerto destino, String horaSalida, String horaLlegada, String codigoRuta )
    {
        this.origen = origen;
        this.destino = destino;
        this.horaSalida = horaSalida;
        this.horaLlegada = horaLlegada;
        this.codigoRuta = codigoRuta;
    }

    public String getCodigoRuta( )
    {
        return codigoRuta;
    }

    public Aeropuerto getOrigen( )
    {
        return origen;
    }

    public Aeropuerto getDestino( )
    {
        return destino;
    }

    public String getHoraSalida( )
    {
        return horaSalida;
    }

    public String getHoraLlegada( )
    {
        return horaLlegada;
    }

   
    public int getDuracion( )
    {
        int minutosSalida = getHoras( horaSalida ) * 60 + getMinutos( horaSalida );
        int minutosLlegada = getHoras( horaLlegada ) * 60 + getMinutos( horaLlegada );

        int duracion = minutosLlegada - minutosSalida;
        if( duracion < 0 )
            duracion += 24 * 60; // El vuelo llega al día siguiente

        return duracion;
    }

  
    public static int getMinutos( String horaCompleta )
    {
        int minutos = Integer.parseInt( horaCompleta ) % 100;
        return minutos;
    }

   
    public static int getHoras( String horaCompleta )
    {
        int horas = Integer.parseInt( horaCompleta ) / 100;
        return horas;
    }

}