package tech.cassandre.trading.bot.util.base;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Set;

/**
 * Base flux.
 *
 * @param <T> Event type
 */
@SuppressWarnings("unused")
public abstract class BaseFlux<T> extends Base {

	/** Flux. */
	private final Flux<T> flux;

	/** Flux sink. */
	private FluxSink<T> fluxSink;

	/**
	 * Constructor.
	 */
	public BaseFlux() {
		flux = Flux.create(newFluxSink -> this.fluxSink = newFluxSink, getOverflowStrategy());
	}

	/**
	 * Set the default overflow strategy - override to change it.
	 *
	 * @return overflow strategy
	 */
	@SuppressWarnings("SameReturnValue")
	protected FluxSink.OverflowStrategy getOverflowStrategy() {
		return FluxSink.OverflowStrategy.LATEST;
	}

	/**
	 * Implements this method to return all the new values. Those values will be send to the strategy.
	 *
	 * @return list of new values
	 */
	protected abstract Set<T> getNewValues();

	/**
	 * Emit a new value.
	 *
	 * @param newValue new value
	 */
	protected void emitValue(final T newValue) {
		getLogger().debug("{} flux emits a new value : {}", this.getClass().getName(), newValue);
		fluxSink.next(newValue);
	}

	/**
	 * This method is called when values must be updated (usually called by the Scheduler).
	 */
	public final void update() {
		getNewValues().forEach(this::emitValue);
	}

	/**
	 * Getter flux.
	 *
	 * @return flux
	 */
	public final Flux<T> getFlux() {
		return flux;
	}

}
