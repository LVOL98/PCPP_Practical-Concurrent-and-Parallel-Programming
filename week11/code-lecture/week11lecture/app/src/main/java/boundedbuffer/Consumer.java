package boundedbuffer;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Consumer extends AbstractBehavior<Consumer.ConsumerCommand> {

    /* --- Messages ------------------------------------- */
    public interface ConsumerCommand {}
    public static final class Get implements ConsumerCommand {
		public final int elem;
		public Get(int elem) { this.elem = elem; }
    }
    public static final class BufferEmpty implements ConsumerCommand { }

    /* --- State ---------------------------------------- */
    private final ActorRef<BoundedBuffer.BoundedBufferCommand> boundedBuffer;

    /* --- Constructor ---------------------------------- */
    private Consumer(ActorContext<ConsumerCommand> context, 
					 ActorRef<BoundedBuffer.BoundedBufferCommand> boundedBuffer) {
		super(context);
		this.boundedBuffer = boundedBuffer;
		this.boundedBuffer.tell(new BoundedBuffer.Get(context.getSelf()));
    }

    /* --- Actor initial behavior ----------------------- */
    public static Behavior<ConsumerCommand> create(ActorRef<BoundedBuffer.BoundedBufferCommand> boundedBuffer) {
		return Behaviors.setup(context -> new Consumer(context,boundedBuffer));
    }    

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<ConsumerCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(Get.class, this::onGet)
			.onMessage(BufferEmpty.class, this::onBufferEmpty)
			.build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<ConsumerCommand> onGet(Get msg) {
		this.getContext().getLog().info("Element {} obtained!", msg.elem);
		return this;
    }

    public Behavior<ConsumerCommand> onBufferEmpty(BufferEmpty msg) {
		this.getContext().getLog().info("Trying to get again");
		boundedBuffer.tell(new BoundedBuffer.Get(getContext().getSelf()));
		return this;
    }
}
