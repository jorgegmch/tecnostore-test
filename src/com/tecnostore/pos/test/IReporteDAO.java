/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tecnostore.pos.test;

import com.tecnostore.pos.modelo.Celular;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Contrato para las consultas de datos del reporte global.
 */
public interface IReporteDAO {

    /**
     * Retorna la suma total de todos los montos facturados en el sistema.
     */
    BigDecimal obtenerTotalVentas() throws SQLException;

    /**
     * Retorna la cantidad total vendida agrupada por modelo de celular.
     */
    Map<String, Integer> obtenerCantidadVendidaPorModelo() throws SQLException;

    /**
     * Retorna los clientes que tienen créditos con saldo pendiente mayor a cero.
     */
    List<Credito> obtenerClientesConCreditoPendiente() throws SQLException;

    /**
     * Retorna todos los celulares con su stock actual.
     */
    List<Celular> obtenerStockActual() throws SQLException;
}
