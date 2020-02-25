package tech.cassandre.trading.bot.batch;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.BaseFlux;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Order flux.
 */
@Lazy
@Component
public class OrderFlux extends BaseFlux<OrderDTO> {

	/** Trade service. */
	private final TradeService tradeService;

	/** Previous values. */
	private final Map<String, OrderDTO> previousValues = new LinkedHashMap<>();

	/**
	 * Constructor.
	 *
	 * @param newTradeService trade service
	 */
	public OrderFlux(final TradeService newTradeService) {
		this.tradeService = newTradeService;
	}

	@Override
	@SuppressWarnings("unused")
	protected final Set<OrderDTO> getNewValues() {
		getLogger().debug("OrderFlux - Retrieving new values");
		Set<OrderDTO> newValues = new LinkedHashSet<>();

		// Finding which order has been updated.
		tradeService.getOpenOrders().forEach(order -> {
			getLogger().debug("OrderFlux - Treating order : {}", order.getId());
			OrderDTO existingOrder = previousValues.get(order.getId());
			// If it does not exists or something changed, we do it.
			if (existingOrder == null || !existingOrder.equals(order)) {
				getLogger().debug("OrderFlux - order {} has changed : {}", order.getId(), order);
				previousValues.put(order.getId(), order);
				newValues.add(order);
			}
		});
		// TODO Removing all the orders no more returned by the exchange.
		getLogger().debug("OrderFlux - {} order(s) updated", newValues.size());
		return newValues;
	}

}
