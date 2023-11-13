package turnstile;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Counter extends AbstractBehavior<Counter.CounterCommand> {

    /* --- Messages ------------------------------------- */
    public interface CounterCommand {}
    public static final class Increment implements CounterCommand { }
    public static final class PrintTotal implements CounterCommand { }


    /* --- State ---------------------------------------- */
    private int total;


    /* --- Constructor ---------------------------------- */
    private Counter(ActorContext<CounterCommand> context) {
		super(context);
		this.total = 0;
    }


    /* --- Actor initial behavior ----------------------- */
    public static Behavior<CounterCommand> create() {
		return Behaviors.setup(Counter::new);
    }


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<CounterCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(Increment.class, this::onIncrement)
			.onMessage(PrintTotal.class, this::onPrintTotal)
			.build();
    }


    /* --- Handlers ------------------------------------- */
    public Behavior<CounterCommand> onIncrement(Increment msg) {
		this.getContext()
			.getLog()
			.info("A visitor arrived!");
		total++;
		return this;
    }

    public Behavior<CounterCommand> onPrintTotal(PrintTotal msg) {
		this.getContext()
			.getLog()
			.info("Total people in the park: {}", total);
		return this;
    }
}
