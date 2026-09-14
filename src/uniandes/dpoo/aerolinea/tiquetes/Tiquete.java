package uniandes.dpoo.aerolinea.tiquetes;

import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;

/**
 * Esta clase agrupa la información de un tiquete, expedido para un vuelo específico en una cierta fecha, y que fue comprado por un cliente.
 * 
 * Cuando se crea, un tiquete no está usado. Después de que se haya realizado el vuelo, el tiquete debe quedar marcado como usado.
 */
public class Tiquete
{
    /**
     * El cliente que compró el tiquete
     */
    private Cliente cliente;

    /**
     * El vuelo en el que se usará el tiquete
     */
    private Vuelo vuelo;

    /**
     * El código para el tiquete, el cual debe ser único
     */
    private String codigo;

    /**
     * El valor que debió pagar el cliente por este tiquete
     */
    private int tarifa;

    /**
     * Este atributo indica si un tiquete ya fue usado o no
     */
    private boolean usado;

    /**
     * Crea un nuevo tiquete con los parámetros recibidos y se lo agrega al cliente que lo compró
     * @param codigo El código del tiquete
     * @param vuelo El vuelo para el que se compró el tiquete
     * @param clienteComprador El cliente que compró el tiquete
     * @param tarifa La tarifa pagada por el tiquete
     */
    public Tiquete( String codigo, Vuelo vuelo, Cliente clienteComprador, int tarifa )
    {
        this.codigo = codigo;
        this.vuelo = vuelo;
        this.cliente = clienteComprador;
        this.tarifa = tarifa;
        this.usado = false;

        clienteComprador.agregarTiquete( this );
    }

    public Cliente getCliente( )
    {
        return cliente;
    }

    public Vuelo getVuelo( )
    {
        return vuelo;
    }

    public String getCodigo( )
    {
        return codigo;
    }

    public int getTarifa( )
    {
        return tarifa;
    }

    /**
     * Cambia el estado del tiquete para marcarlo como usado
     */
    public void marcarComoUsado( )
    {
        usado = true;
    }

    /**
     * Indica si el tiquete ya fue usado
     * @return
     */
    public boolean esUsado( )
    {
        return usado;
    }

}