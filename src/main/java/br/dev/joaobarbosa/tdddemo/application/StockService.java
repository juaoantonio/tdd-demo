package br.dev.joaobarbosa.tdddemo.application;

import br.dev.joaobarbosa.tdddemo.application.port.StockPort;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final StockPort stockPort;

    public StockService(StockPort stockPort) {
        this.stockPort = stockPort;
    }

    public void reserve(String sku, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        stockPort.reserve(sku, quantity);
    }

    public void release(String sku, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        stockPort.release(sku, quantity);
    }
}
