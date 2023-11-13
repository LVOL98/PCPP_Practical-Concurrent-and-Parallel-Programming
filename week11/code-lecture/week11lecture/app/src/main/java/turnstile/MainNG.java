package turnstile;

import akka.actor.typed.ActorSystem;

import java.io.IOException;

import java.util.stream.IntStream;

public class MainNG {

    public static void main(String[] args) {    
		// start the counter actor
		final ActorSystem<Counter.CounterCommand> counter =
			ActorSystem.create(Counter.create(), "counter_actor");

		// simulate 5 people entering the park
		IntStream.range(0,5)
			.forEach(i -> {
					counter.tell(new Counter.Increment());
				});
		counter.tell(new Counter.PrintTotal());

		// wait until user presses enter
		try {
			System.out.println(">>> Press ENTER to exit <<<");
			System.in.read();
		}
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
		} finally {
			counter.terminate();
		}
    }	
}
