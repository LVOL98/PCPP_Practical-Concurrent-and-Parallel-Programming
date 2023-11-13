package turnstile;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.stream.IntStream;

public class Turnstile extends AbstractBehavior<Turnstile.TurnstileCommand> {

    /* --- Messages ------------------------------------- */
    public interface TurnstileCommand {}
    public static final class Start implements TurnstileCommand { }


    /* --- State ---------------------------------------- */
    private final ActorRef<Counter.CounterCommand> countActor;


    /* --- Constructor ---------------------------------- */
    private Turnstile(ActorContext<TurnstileCommand> context,
					  ActorRef<Counter.CounterCommand> countActor) {
		super(context);
		this.countActor = countActor;
    }


    /* --- Actor initial behavior ----------------------- */
    public static Behavior<TurnstileCommand> create(ActorRef<Counter.CounterCommand> countActor) {
		return Behaviors.setup(context -> new Turnstile(context, countActor));
    }


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<TurnstileCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(Start.class, this::onStart)
			.build();
    }


    /* --- Handlers ------------------------------------- */
    private Behavior<TurnstileCommand> onStart(Start msg) {
		// send 20 increments to the counter
		IntStream.range(0,20)
			.forEach( i -> {
					countActor.tell(new Counter.Increment());
				});
		countActor.tell(new Counter.PrintTotal());
		// continue with the same behavior
		return this;
    }
}
