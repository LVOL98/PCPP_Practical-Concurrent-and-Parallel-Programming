package boundedbuffer;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.stream.IntStream;

public class Guardian extends AbstractBehavior<Guardian.KickOff> {
    /* --- Messages ------------------------------------- */
    public static final class KickOff { }

    /* --- State ---------------------------------------- */
    // empty

    
    /* --- Constructor ---------------------------------- */
    private Guardian(ActorContext<KickOff> context) {
		super(context);
    }

    /* --- Actor initial behavior ----------------------- */
    public static Behavior<KickOff> create() {
		return Behaviors.setup(Guardian::new);
    }

    
    /* --- Message handling ----------------------------- */
    @Override
    public Receive<KickOff> createReceive() {
		return newReceiveBuilder()
			.onMessage(KickOff.class, this::onKickOff)
			.build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<KickOff> onKickOff(KickOff msg) {
		final ActorRef<BoundedBuffer.BoundedBufferCommand> bb = 
			getContext().spawn(BoundedBuffer.create(2), "bounded_buffer");

		for (int i = 0; i < 5; i++) {
			final ActorRef<Consumer.ConsumerCommand> c = 
				getContext().spawn(Consumer.create(bb), "consumer_"+i);
		}

		for (int i = 0; i < 5; i++) {
			final ActorRef<Producer.ProducerCommand> p = 
				getContext().spawn(Producer.create(bb,i), "producer_"+i);
		}

		return this;
    }
}
