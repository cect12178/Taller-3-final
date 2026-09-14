package uniandes.dpoo.aerolinea.modelo.tarifas;

import uniandes.dpoo.aerolinea.modelo.Ruta;
import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;

/**
 * Esta clase se utiliza para calcular las tarifas en temporada alta, cuando los vuelos no tienen descuento y se aplica la misma tarifa por kilómetro a todos los clientes.
 */
public class CalculadoraTarifasTemporadaAlta extends CalculadoraTarifas
{
    /**
     * El costo por kilómetro para temporada alta
     */
    protected final int COSTO_POR_KM = 1000;

    /**
     * Calcula el costo base como COSTO_POR_KM x distancia.
     */
    @Override
    public int calcularCostoBase( Vuelo vuelo, Cliente cliente )
    {
        Ruta ruta = vuelo.getRuta( );
        int distancia = calcularDistanciaVuelo( ruta );

        return COSTO_POR_KM * distancia;
    }

    @Override
    public double calcularPorcentajeDescuento( Cliente cliente )
    {
        return 0;
    }

}