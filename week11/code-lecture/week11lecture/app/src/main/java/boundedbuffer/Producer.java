package boundedbuffer;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Producer extends AbstractBehavior<Producer.ProducerCommand> {

    /* --- Messages ------------------------------------- */
    public interface ProducerCommand {}
    public static final class ElementAdded implements ProducerCommand { }
    public static final class BufferFull implements ProducerCommand { }

    /* --- State ---------------------------------------- */
    // empty
    private final ActorRef<BoundedBuffer.BoundedBufferCommand> boundedBuffer;
    private final int toAdd;

    /* --- Constructor ---------------------------------- */
    private Producer(ActorContext<ProducerCommand> context, 
					 ActorRef<BoundedBuffer.BoundedBufferCommand> boundedBuffer,
					 int toAdd) {
		super(context);
		this.boundedBuffer = boundedBuffer;
		this.toAdd         = toAdd;
		this.boundedBuffer.tell(new BoundedBuffer.Put(context.getSelf(), toAdd));
	
    }

    /* --- Actor initial behavior ----------------------- */
    public static Behavior<ProducerCommand> create(ActorRef<BoundedBuffer.BoundedBufferCommand> boundedBuffer, int toAdd) {
		return Behaviors.setup(context -> new Producer(context,boundedBuffer,toAdd));
    }    

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<ProducerCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(ElementAdded.class, this::onElementAdded)
			.onMessage(BufferFull.class, this::onBufferFull)
			.build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<ProducerCommand> onElementAdded(ElementAdded msg) {
		this.getContext().getLog().info("Element added!");
		return this;
    }

    public Behavior<ProducerCommand> onBufferFull(BufferFull msg) {
		this.getContext().getLog().info("Trying to add {} again", toAdd);
		boundedBuffer.tell(new BoundedBuffer.Put(getContext().getSelf(), toAdd));
		return this;
    }
}
