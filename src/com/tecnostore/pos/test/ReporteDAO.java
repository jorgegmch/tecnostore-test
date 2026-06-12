/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tecnostore.pos.test;

import com.tecnostore.pos.modelo.Celular;
import com.tecnostore.pos.modelo.CategoriaGama;
import com.tecnostore.pos.modelo.Cliente;
import com.tecnostore.pos.modelo.SistemaOperativo;
import com.tecnostore.pos.modelo.Venta;
import com.tecnostore.pos.persistencia.ConexionDB;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación JDBC del contrato IReporteDAO.
 */
public class ReporteDAO implements IReporteDAO {

    @Override
    public BigDecimal obtenerTotalVentas() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM ventas";
        try (Connection con = ConexionDB.getInstancia().obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Map<String, Integer> obtenerCantidadVendidaPorModelo() throws SQLException {
        String sql = "SELECT c.modelo, SUM(dv.cantidad) AS total_vendido " +
                     "FROM detalle_ventas dv " +
                     "JOIN celulares c ON dv.id_celular = c.id " +
                     "GROUP BY c.modelo " +
                     "ORDER BY total_vendido DESC";

        Map<String, Integer> resultado = new LinkedHashMap<>();

        try (Connection con = ConexionDB.getInstancia().obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString("modelo"), rs.getInt("total_vendido"));
            }
        }
        return resultado;
    }

    @Override
    public List<Credito> obtenerClientesConCreditoPendiente() throws SQLException {
        String sql = "SELECT cr.id, cr.saldo_pendiente, " +
                     "c.id AS cliente_id, c.nombre, c.identificacion, c.correo, c.telefono, " +
                     "v.id AS venta_id, v.total, v.fecha " +
                     "FROM creditos cr " +
                     "JOIN clientes c ON cr.id_cliente = c.id " +
                     "JOIN ventas v ON cr.id_venta = v.id " +
                     "WHERE cr.saldo_pendiente > 0";

        List<Credito> resultado = new ArrayList<>();

        try (Connection con = ConexionDB.getInstancia().obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getLong("cliente_id"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setIdentificacion(rs.getString("identificacion"));
                cliente.setCorreo(rs.getString("correo"));
                cliente.setTelefono(rs.getString("telefono"));

                Venta venta = new Venta();
                venta.setId(rs.getLong("venta_id"));
                venta.setCliente(cliente);
                venta.setTotal(rs.getBigDecimal("total"));
                venta.setFecha(rs.getDate("fecha").toLocalDate().atStartOfDay());

                Credito credito = new Credito(
                    rs.getLong("id"),
                    cliente,
                    venta,
                    rs.getBigDecimal("saldo_pendiente")
                );
                resultado.add(credito);
            }
        }
        return resultado;
    }

    @Override
    public List<Celular> obtenerStockActual() throws SQLException {
        String sql = "SELECT id, marca, modelo, sistema_operativo, gama, precio, stock " +
                     "FROM celulares " +
                     "ORDER BY stock ASC";

        List<Celular> resultado = new ArrayList<>();

        try (Connection con = ConexionDB.getInstancia().obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Celular celular = new Celular();
                celular.setId(rs.getLong("id"));
                celular.setMarca(rs.getString("marca"));
                celular.setModelo(rs.getString("modelo"));
                celular.setPrecio(rs.getBigDecimal("precio"));
                celular.setStock(rs.getInt("stock"));
                celular.setSistemaOperativo(
                    SistemaOperativo.valueOf(rs.getString("sistema_operativo").toUpperCase()));
                celular.setCategoriaGama(
                    CategoriaGama.valueOf(rs.getString("gama").toUpperCase()));
                resultado.add(celular);
            }
        }
        return resultado;
    }
}
