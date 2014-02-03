/**
 * Copyright (C) 2012 - 2014 Xeiam LLC http://xeiam.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.xeiam.xchange.examples.mtgox.v2.service.marketdata.polling;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.mtgox.v2.MtGoxAdapters;
import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import com.xeiam.xchange.mtgox.v2.dto.marketdata.MtGoxDepthWrapper;
import com.xeiam.xchange.mtgox.v2.dto.marketdata.MtGoxTicker;
import com.xeiam.xchange.mtgox.v2.service.polling.MtGoxMarketDataServiceRaw;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * Example showing the following:
 * </p>
 * <ul>
 * <li>Connecting to Mt Gox BTC exchange</li>
 * <li>Retrieving the last tick</li>
 * <li>Retrieving the current order book</li>
 * <li>Retrieving the current full order book</li>
 * <li>Retrieving trades</li>
 * </ul>
 */
public class MtGoxMarketdataDemo {

    public static void main(String[] args) throws IOException {

        // Demonstrate the public market data service
        // Use the factory to get the version 2 MtGox exchange API using default settings
        Exchange mtGoxExchange = ExchangeFactory.INSTANCE.createExchange(MtGoxExchange.class.getName());

        // Interested in the public market data feed (no authentication)
        PollingMarketDataService marketDataService = mtGoxExchange.getPollingMarketDataService();
        generic(marketDataService);
        raw(marketDataService);


    }

    private static void raw(PollingMarketDataService marketDataService) throws IOException {
        // Get the latest ticker data showing BTC to USD
        MtGoxTicker ticker = ((MtGoxMarketDataServiceRaw) marketDataService).getMtGoxTicker(Currencies.BTC, Currencies.USD);
        String btcusd = ticker.getLast().toString();
        System.out.println("Current exchange rate for BTC / USD: " + btcusd);

        // Get the current orderbook
        Object[] args = {};
        MtGoxDepthWrapper orderBook = ((MtGoxMarketDataServiceRaw) marketDataService).getMtGoxOrderBook(Currencies.BTC, Currencies.USD, args);
        List<LimitOrder> asks = MtGoxAdapters.adaptOrders(orderBook.getMtGoxDepth().getAsks(), Currencies.USD, "ask", "");
        List<LimitOrder> bids = MtGoxAdapters.adaptOrders(orderBook.getMtGoxDepth().getBids(), Currencies.USD, "bid", "");
        System.out.println("Current Order Book size for BTC / USD: " + asks.size() + bids.size());

        // Get the current full orderbook
        Object[] argsWithFull = {PollingMarketDataService.OrderBookType.FULL};
        MtGoxDepthWrapper fullOrderBook = ((MtGoxMarketDataServiceRaw) marketDataService).getMtGoxOrderBook(Currencies.BTC, Currencies.USD, argsWithFull);
        asks = MtGoxAdapters.adaptOrders(orderBook.getMtGoxDepth().getAsks(), Currencies.USD, "ask", "");
        bids = MtGoxAdapters.adaptOrders(orderBook.getMtGoxDepth().getBids(), Currencies.USD, "bid", "");
        System.out.println("Current Full Order Book size for BTC / USD: " + asks.size() + bids.size());

        // Get trades
        Trades trades = marketDataService.getTrades(Currencies.BTC, Currencies.PLN);
        System.out.println("Current trades size for BTC / PLN: " + trades.getTrades().size());
    }

    private static void generic(PollingMarketDataService marketDataService) throws IOException {
        // Get the latest ticker data showing BTC to USD
        Ticker ticker = marketDataService.getTicker(Currencies.BTC, Currencies.USD);
        String btcusd = ticker.getLast().toString();
        System.out.println("Current exchange rate for BTC / USD: " + btcusd);

        // Get the current orderbook
        OrderBook orderBook = marketDataService.getOrderBook(Currencies.BTC, Currencies.USD);
        System.out.println("Current Order Book size for BTC / USD: " + orderBook.getAsks().size() + orderBook.getBids().size());

        // Get the current full orderbook
        OrderBook fullOrderBook = marketDataService.getOrderBook(Currencies.BTC, Currencies.USD, PollingMarketDataService.OrderBookType.FULL);
        System.out.println("Current Full Order Book size for BTC / USD: " + fullOrderBook.getAsks().size() + fullOrderBook.getBids().size());

        // Get trades
        Trades trades = marketDataService.getTrades(Currencies.BTC, Currencies.PLN);
        System.out.println("Current trades size for BTC / PLN: " + trades.getTrades().size());
    }

}
