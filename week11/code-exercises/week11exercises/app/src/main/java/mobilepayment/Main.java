package mobilepayment;

import akka.actor.typed.ActorSystem;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
		final var system = ActorSystem.create(Guardian.create(), "mobile-payment");

		// system.tell(new Guardian.Exercise_11_1_5(100));
		system.tell(new Guardian.Exercise_11_1_6());

		try {
			System.out.println(">>> Press ENTER to exit <<<");
			System.in.read();
		}
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
		} finally {
			system.terminate();
		}
    }    
}
