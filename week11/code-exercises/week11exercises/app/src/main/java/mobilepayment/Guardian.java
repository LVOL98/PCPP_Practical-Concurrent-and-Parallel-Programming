package mobilepayment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Guardian extends AbstractBehavior<Guardian.GuardianCommand> {
  public interface GuardianCommand {
  }

  public static final class Exercise_11_1_5 implements GuardianCommand {
    long amount;

    public Exercise_11_1_5(long amount) {
      this.amount = amount;
    }
  }

  public static final class Exercise_11_1_6 implements GuardianCommand {
  }

  public static final class Exercise_11_1_7 implements GuardianCommand {
  }

  public static Behavior<Guardian.GuardianCommand> create() {
    return Behaviors.setup(Guardian::new);
  }

  final int numberOfMobileApps = 2;
  final int numberOfBanks = 2;
  final int numberOfAccounts = 2;

  final List<ActorRef<MobileApp.MobileAppCommand>> mobileApps = new ArrayList<ActorRef<MobileApp.MobileAppCommand>>();
  final List<ActorRef<Bank.BankCommand>> banks = new ArrayList<ActorRef<Bank.BankCommand>>();
  final List<ActorRef<Account.AccountCommand>> accounts = new ArrayList<ActorRef<Account.AccountCommand>>();

  private Guardian(ActorContext<GuardianCommand> context) {
    super(context);
    context.getLog().info("Guardian started");

    for (var i = 0; i < numberOfBanks; i++) {
      banks.add(getContext().spawn(Bank.create(), "bank-" + i));
    }

    for (var i = 0; i < numberOfAccounts; i++) {
      accounts.add(getContext().spawn(Account.create(0, banks.get(i)), "account-" + i));
    }

    for (var i = 0; i < numberOfAccounts; i++) { 
      mobileApps
          .add(getContext().spawn(MobileApp.create(banks.get(i), accounts.get(i)), "mobile-app-" + i));
    }    
  }

  @Override
  public Receive<GuardianCommand> createReceive() {
    return newReceiveBuilder()
        .onMessage(Exercise_11_1_5.class, this::onExercise_11_1_5)
        .onMessage(Exercise_11_1_6.class, this::onExercise_11_1_6)
        .build();
  }

  public Behavior<GuardianCommand> onExercise_11_1_5(Exercise_11_1_5 exercise_11_1_5) {
    mobileApps.get(0).tell(new MobileApp.Transfer(accounts.get(1), exercise_11_1_5.amount));
    mobileApps.get(1).tell(new MobileApp.Transfer(accounts.get(0), exercise_11_1_5.amount));
    return this;
  }

  public Behavior<GuardianCommand> onExercise_11_1_6(Exercise_11_1_6 exercise_11_1_6) {
    mobileApps.get(0).tell(new MobileApp.MakePayments(accounts.get(1)));
    mobileApps.get(1).tell(new MobileApp.MakePayments(accounts.get(0)));

    accounts.get(0).tell(new Account.PrintBalance());
    accounts.get(1).tell(new Account.PrintBalance());

    return this;
  }
}
