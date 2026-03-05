package br.dev.joaobarbosa.tdddemo.application;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String sku, int requested, int available) {
        super("Insufficient stock for SKU '" + sku + "': requested " + requested + ", available " + available);
    }
}
