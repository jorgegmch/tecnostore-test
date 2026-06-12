/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tecnostore.pos.test;

import com.tecnostore.pos.modelo.Celular;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Servicio principal del reporte global. Patrón Singleton.
 */
public class ReporteService {

    private static final int STOCK_MINIMO = 5;
    private static final String NOMBRE_ARCHIVO = "reporte_global.txt";

    private static volatile ReporteService instancia;
    private final IReporteDAO reporteDAO;

    private ReporteService() {
        this.reporteDAO = new ReporteDAO();
    }
    
    public BigDecimal obtenerTotalVentas() throws SQLException {
        BigDecimal total = reporteDAO.obtenerTotalVentas();
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("No hay ventas registradas en el sistema.");
        }
        return total;
    }

    public Map<String, Integer> obtenerCantidadVendidaPorModelo() throws SQLException {
        Map<String, Integer> resultado = reporteDAO.obtenerCantidadVendidaPorModelo();
        if (resultado.isEmpty()) {
            System.out.println("No hay celulares vendidos registrados.");
        }
        return resultado;
    }

    public List<Credito> obtenerClientesConCreditoPendiente() throws SQLException {
        List<Credito> creditos = reporteDAO.obtenerClientesConCreditoPendiente();
        if (creditos.isEmpty()) {
            System.out.println("No hay clientes con creditos pendientes.");
        }
        return creditos;
    }

    public List<Celular> obtenerProductosConStockBajo() throws SQLException {
        List<Celular> celulares = reporteDAO.obtenerStockActual();
        return celulares.stream()
                .filter(c -> c.getStock() < STOCK_MINIMO)
                .collect(Collectors.toList());
    }

    public static ReporteService getInstancia() {
        if (instancia == null) {
            synchronized (ReporteService.class) {
                if (instancia == null) {
                    instancia = new ReporteService();
                }
            }
        }
        return instancia;
    }
    
    public void generarReporteGlobal() throws SQLException, IOException {
        BigDecimal totalVentas = reporteDAO.obtenerTotalVentas();
        Map<String, Integer> ventasPorModelo = reporteDAO.obtenerCantidadVendidaPorModelo();
        List<Credito> creditos = reporteDAO.obtenerClientesConCreditoPendiente();
        List<Celular> todosLosCelulares = reporteDAO.obtenerStockActual();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO))) {

            writer.write("========================================");
            writer.newLine();
            writer.write("   REPORTE GLOBAL DE GESTION - TECNOSTORE");
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.newLine();

            // Seccion 1
            writer.write("1. TOTAL DE VENTAS REALIZADAS");
            writer.newLine();
            writer.write("----------------------------------------");
            writer.newLine();
            if (totalVentas.compareTo(BigDecimal.ZERO) == 0) {
                writer.write("No hay ventas registradas en el sistema.");
            } else {
                writer.write("Total facturado: $" + totalVentas);
            }
            writer.newLine();
            writer.newLine();

            // Seccion 2
            writer.write("2. CELULARES VENDIDOS POR MODELO");
            writer.newLine();
            writer.write("----------------------------------------");
            writer.newLine();
            if (ventasPorModelo.isEmpty()) {
                writer.write("No hay celulares vendidos registrados.");
                writer.newLine();
            } else {
                for (Map.Entry<String, Integer> entry : ventasPorModelo.entrySet()) {
                    writer.write(String.format("  %-25s %d unidades", entry.getKey(), entry.getValue()));
                    writer.newLine();
                }
            }
            writer.newLine();

            // Seccion 3
            writer.write("3. CLIENTES CON CREDITOS PENDIENTES");
            writer.newLine();
            writer.write("----------------------------------------");
            writer.newLine();
            if (creditos.isEmpty()) {
                writer.write("No hay clientes con creditos pendientes.");
                writer.newLine();
            } else {
                for (Credito c : creditos) {
                    writer.write(String.format("  %-25s Saldo: $%s",
                            c.getCliente().getNombre(),
                            c.getSaldoPendiente()));
                    writer.newLine();
                }
            }
            writer.newLine();

            // Seccion 4
            writer.write("4. STOCK ACTUAL POR PRODUCTO");
            writer.newLine();
            writer.write("----------------------------------------");
            writer.newLine();
            for (Celular c : todosLosCelulares) {
                String alerta = c.getStock() < STOCK_MINIMO ? "  <<< STOCK BAJO" : "";
                writer.write(String.format("  %-25s Stock: %d%s",
                        c.getMarca() + " " + c.getModelo(),
                        c.getStock(),
                        alerta));
                writer.newLine();
            }
            writer.newLine();

            writer.write("========================================");
            writer.newLine();
            writer.write("Reporte generado correctamente.");
            writer.newLine();
            writer.write("========================================");
        }

        System.out.println("Reporte generado: " + NOMBRE_ARCHIVO);
    }
}
