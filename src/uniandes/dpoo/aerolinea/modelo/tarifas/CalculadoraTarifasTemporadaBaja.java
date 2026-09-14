package uniandes.dpoo.aerolinea.modelo.tarifas;

import uniandes.dpoo.aerolinea.modelo.Ruta;
import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.modelo.cliente.ClienteCorporativo;

/**
 * Esta clase se utiliza para calcular las tarifas en temporada baja.
 * 
 * En temporada baja, los clientes que son personas naturales tienen una tarifa base diferente a la de los clientes corporativos.
 * 
 * Adicionalmente, los clientes corporativos tienen un descuento diferente según el tamaño (los clientes naturales no tienen descuento).
 */
public class CalculadoraTarifasTemporadaBaja extends CalculadoraTarifas
{
    /**
     * El costo por kilómetro en temporada baja para personas naturales
     */
    protected final int COSTO_POR_KM_NATURAL = 600;

    /**
     * El costo por kilómetro en temporada baja para clientes corporativos
     */
    protected final int COSTO_POR_KM_CORPORATIVO = 900;

    /**
     * El descuento que se le puede aplicar a empresas pequeñas
     */
    protected final double DESCUENTO_PEQ = 0.02;

    /**
     * El descuento que se le puede aplicar a empresas medianas
     */
    protected final double DESCUENTO_MEDIANAS = 0.1;

    /**
     * El descuento que se le puede aplicar a empresas grandes
     */
    protected final double DESCUENTO_GRANDES = 0.2;

    /**
     * Calcula el costo base como COSTO_POR_KM x distancia.
     */
    @Override
    public int calcularCostoBase( Vuelo vuelo, Cliente cliente )
    {
        Ruta ruta = vuelo.getRuta( );
        int distancia = calcularDistanciaVuelo( ruta );

        int costoPorKm = ( cliente instanceof ClienteCorporativo ) ? COSTO_POR_KM_CORPORATIVO : COSTO_POR_KM_NATURAL;

        return costoPorKm * distancia;
    }

    @Override
    public double calcularPorcentajeDescuento( Cliente cliente )
    {
        double descuento = 0;

        if( cliente instanceof ClienteCorporativo )
        {
            ClienteCorporativo corporativo = ( ClienteCorporativo )cliente;
            switch( corporativo.getTamanoEmpresa( ) )
            {
                case ClienteCorporativo.PEQUENA:
                    descuento = DESCUENTO_PEQ;
                    break;
                case ClienteCorporativo.MEDIANA:
                    descuento = DESCUENTO_MEDIANAS;
                    break;
                case ClienteCorporativo.GRANDE:
                    descuento = DESCUENTO_GRANDES;
                    break;
            }
        }

        return descuento;
    }

}