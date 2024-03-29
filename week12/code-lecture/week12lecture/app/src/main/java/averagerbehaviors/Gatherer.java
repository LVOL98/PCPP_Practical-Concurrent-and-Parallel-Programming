package averagerbehaviors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.List;
import java.util.stream.IntStream;
import java.util.Random;

public class Gatherer extends AbstractBehavior<Gatherer.GathererCommand> {
    /* --- Messages ------------------------------------- */
    public static final class GathererCommand {
		public final double number;
	
		public GathererCommand(double number) {
			this.number = number;
		}
    }
    
    /* --- State ---------------------------------------- */
    ActorRef<Gatherer.GathererCommand> topGatherer;
    private double result;

    //Just for timing
    private long initTime;

    
    /* --- Constructor ---------------------------------- */
    // Note: Two initial states    
    private Gatherer(ActorContext<GathererCommand> context,
					 ActorRef<Gatherer.GathererCommand> topGatherer) {
		super(context);
		this.topGatherer = topGatherer;
		this.result = 0;
		this.initTime = 0; // here we simply ignore it
    }

    private Gatherer(ActorContext<GathererCommand> context,
					 ActorRef<Gatherer.GathererCommand> topGatherer,
					 long initTime) {
		super(context);
		this.topGatherer = topGatherer;
		this.result = 0;
		this.initTime = initTime;
    }

    /* --- Actor initial behavior ----------------------- */
    // Note: two create methods one per initial state
    public static Behavior<GathererCommand> create(ActorRef<Gatherer.GathererCommand> topGatherer) {
		return Behaviors.setup(context -> new Gatherer(context, topGatherer));
    }

    public static Behavior<GathererCommand> create(ActorRef<Gatherer.GathererCommand> topGatherer,
												   long initTime) {
		return Behaviors.setup(context -> new Gatherer(context, topGatherer, initTime));
    }

    
    /* --- Message handling ----------------------------- */
    // Changing behaviour: Reacting differently to GathererCommand
    // messages depending on whether it is the first or second message
    
    @Override
    public Receive<GathererCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(GathererCommand.class, this::onGathererCommand)
			.build();
    }

    public Receive<GathererCommand> waitForSecond() {
    	return newReceiveBuilder()
    	    .onMessage(GathererCommand.class, this::onGathererCommandSecond)
    	    .build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<GathererCommand> onGathererCommand(GathererCommand msg) {
		result += msg.number;
		// Debugging
		// printInfo(getActorName() + ": avg state = " + result);
		return waitForSecond();
    }

    public Behavior<GathererCommand> onGathererCommandSecond(GathererCommand msg) {
		result += msg.number;
		if (topGatherer != null) {
			// Debugging
			// printInfo(getActorName() + ": send " + result +
			// 	  ". Gatherer: " + topGatherer.path().name());
			topGatherer.tell(new GathererCommand(result));		
		} else {
			printInfo("Average =  " + result +
					  " | Time: " + (System.currentTimeMillis()-initTime)+"ms");
		}
		return Behaviors.stopped();
    }

    /* --- Auxiliary methods----------------------------- */
    private void printInfo(String str) {
		getContext().getLog().info(str);
    }

    private String getActorName() {
		return getContext().getSelf().path().name();
    }

}
