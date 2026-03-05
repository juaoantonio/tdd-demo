package br.dev.joaobarbosa.tdddemo.infra.stock;

import br.dev.joaobarbosa.tdddemo.application.InsufficientStockException;
import br.dev.joaobarbosa.tdddemo.application.port.StockPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptador de estoque em memória para desenvolvimento e testes.
 * Em produção, substituir por integração com sistema de WMS/ERP.
 *
 * Estoque inicial pré-configurado com SKUs de demonstração.
 */
@Component
public class InMemoryStockAdapter implements StockPort {

    private final Map<String, Integer> stock = new ConcurrentHashMap<>();

    public InMemoryStockAdapter() {
        // Estoque inicial para o ambiente de demo
        stock.put("SKU-001", 100);
        stock.put("SKU-002", 50);
        stock.put("SKU-003", 20);
        stock.put("SKU-010", 75);
        stock.put("SKU-100", 30);
    }

    @Override
    public void reserve(String sku, int quantity) {
        int available = stock.getOrDefault(sku, 0);
        if (available < quantity) {
            throw new InsufficientStockException(sku, quantity, available);
        }
        stock.put(sku, available - quantity);
    }

    @Override
    public void release(String sku, int quantity) {
        stock.merge(sku, quantity, Integer::sum);
    }

    /** Exposto para testes de integração que precisam configurar o estoque. */
    public void setStock(String sku, int quantity) {
        stock.put(sku, quantity);
    }

    public int getAvailable(String sku) {
        return stock.getOrDefault(sku, 0);
    }
}
