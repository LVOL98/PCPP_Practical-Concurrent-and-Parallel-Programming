package turnstile;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Guardian extends AbstractBehavior<Guardian.KickOff> {
    public static final class KickOff { }

    private Guardian(ActorContext<KickOff> context) {
		super(context);
    }

    public static Behavior<Guardian.KickOff> create() {
		return Behaviors.setup(Guardian::new);
    }

    @Override
    public Receive<KickOff> createReceive() {
		return newReceiveBuilder()
			.onMessage(KickOff.class, this::onKickOff)
			.build();
    }

    private Behavior<KickOff> onKickOff(KickOff msg) {
		// spawn the counter actor
		ActorRef<Counter.CounterCommand> counter = 
			getContext().spawn(Counter.create(), "counter_actor");

		// spawn two turnstile actors
		ActorRef<Turnstile.TurnstileCommand> t1 = 
			getContext().spawn(Turnstile.create(counter), "t1");
		t1.tell(new Turnstile.Start());

		ActorRef<Turnstile.TurnstileCommand> t2 = 
			getContext().spawn(Turnstile.create(counter), "t2");
		t2.tell(new Turnstile.Start());

		// The behaviour stays the same
		return this;
    }
}
