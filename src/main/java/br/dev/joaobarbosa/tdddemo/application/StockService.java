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
        // TODO: not yet implemented
        throw new UnsupportedOperationException("reserve not yet implemented");
    }

    public void release(String sku, int quantity) {
        // TODO: not yet implemented
        throw new UnsupportedOperationException("release not yet implemented");
    }
}
