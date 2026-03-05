package br.dev.joaobarbosa.tdddemo.application.port;

/**
 * Porta de saída para gerenciamento de estoque.
 * Permite reservar e liberar unidades de um produto pelo seu SKU.
 */
public interface StockPort {

    /**
     * Reserva {@code quantity} unidades do produto identificado por {@code sku}.
     *
     * @throws br.dev.joaobarbosa.tdddemo.application.InsufficientStockException
     *         se o estoque disponível for menor que a quantidade solicitada
     */
    void reserve(String sku, int quantity);

    /**
     * Libera {@code quantity} unidades previamente reservadas do produto {@code sku}.
     */
    void release(String sku, int quantity);
}
