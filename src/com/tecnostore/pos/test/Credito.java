/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tecnostore.pos.test;

import com.tecnostore.pos.modelo.Cliente;
import com.tecnostore.pos.modelo.Venta;
import java.math.BigDecimal;

/**
 *
 * @author camper
 */
public class Credito {

    private Long id;
    private Cliente cliente;
    private Venta venta;
    private BigDecimal saldoPendiente;

    public Credito(Long id, Cliente cliente, Venta venta, BigDecimal saldoPendiente) {
        if (cliente == null) throw new IllegalArgumentException("El cliente no puede ser nulo");
        if (venta == null) throw new IllegalArgumentException("La venta no puede ser nula");
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El saldo pendiente no puede ser nulo ni negativo");
        this.id = id;
        this.cliente = cliente;
        this.venta = venta;
        this.saldoPendiente = saldoPendiente;
    }

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Venta getVenta() { return venta; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
}

