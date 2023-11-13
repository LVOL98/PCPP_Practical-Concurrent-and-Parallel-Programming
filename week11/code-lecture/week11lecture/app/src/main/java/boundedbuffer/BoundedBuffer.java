package boundedbuffer;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.LinkedList;
import java.util.Queue;

public class BoundedBuffer extends AbstractBehavior<BoundedBuffer.BoundedBufferCommand> {

    /* --- Messages ------------------------------------- */
    public interface BoundedBufferCommand {}
    public static final class Get implements BoundedBufferCommand {
		public final ActorRef<Consumer.ConsumerCommand> sender;

		public Get(ActorRef<Consumer.ConsumerCommand> sender) {
			this.sender = sender;
		}
    }

    public static final class Put implements BoundedBufferCommand { 
		public final ActorRef<Producer.ProducerCommand> sender;
		public final int elem;
		public Put(ActorRef<Producer.ProducerCommand> sender, int elem) {
			this.sender = sender;
			this.elem  = elem;
		}
    }


    /* --- State ---------------------------------------- */
    private final int size;
    private final Queue<Integer> buffer;
    private final Queue<ActorRef<Producer.ProducerCommand>> waitingProducers;
    private final Queue<ActorRef<Consumer.ConsumerCommand>> waitingConsumers;

    /* --- Constructor ---------------------------------- */
    private BoundedBuffer(ActorContext<BoundedBufferCommand> context, 
						  int size) {
		super(context);
		this.buffer = new LinkedList<Integer>();
		this.waitingProducers = new LinkedList<ActorRef<Producer.ProducerCommand>>();
		this.waitingConsumers = new LinkedList<ActorRef<Consumer.ConsumerCommand>>();
		this.size   = size;
    }

    /* --- Actor initial behavior ----------------------- */
    public static Behavior<BoundedBufferCommand> create(int size) {
		return Behaviors.setup(context -> new BoundedBuffer(context,size));
    }    

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<BoundedBufferCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(Get.class, this::onGet)
			.onMessage(Put.class, this::onPut)
			.build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<BoundedBufferCommand> onGet(Get msg) {
		this.getContext()
			.getLog()
			.info("Consumer {} tries to get an element", 
				  msg.sender.path().name());

		if(buffer.size() > 0) {
			msg.sender.tell(new Consumer.Get(buffer.remove()));
			if(waitingProducers.size() > 0)
				waitingProducers.remove().tell(new Producer.BufferFull());
		}
		else {
			waitingConsumers.add(msg.sender);
		} 

		return this;
    }

    public Behavior<BoundedBufferCommand> onPut(Put msg) {
		this.getContext()
			.getLog()
			.info("Producer {} wants to add: {}", 
				  msg.sender.path().name(),
				  msg.elem);


		if(buffer.size() < size) {
			buffer.add(msg.elem);
			msg.sender.tell(new Producer.ElementAdded());
			if(waitingConsumers.size() > 0)
				waitingConsumers.remove().tell(new Consumer.BufferEmpty());
		} else {
			waitingProducers.add(msg.sender);
		}

		return this;
    }
}
