package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Account extends AbstractBehavior<Account.AccountCommand> {
    public interface AccountCommand {
    }

    public static final class Debit implements AccountCommand {
        final long amount;

        public Debit(long amount) {
            this.amount = amount;
        }
    }

    public static final class Credit implements AccountCommand {
        final long amount;

        public Credit(long amount) {
            this.amount = amount;
        }
    }

    public static final class PrintBalance implements AccountCommand {
    }

    // public static final class RespondDebit implements AccountCommand {
    //     final long balance;

    //     public RespondDebit(long balance) {
    //         this.balance = balance;
    //     }
    // }

    // public static final class RespondCredit implements AccountCommand {
    //     final long balance;

    //     public RespondCredit(long balance) {
    //         this.balance = balance;
    //     }
    // }

    public static Behavior<AccountCommand> create(long balance, ActorRef<Bank.BankCommand> bankRef) {
        return Behaviors.setup(context -> new Account(context, balance, bankRef));
    }

    private long balance;
    private ActorRef<Bank.BankCommand> bankRef;

    private Account(ActorContext<AccountCommand> context, long balance, ActorRef<Bank.BankCommand> bankRef) {
        super(context);

        this.balance = balance;
        this.bankRef = bankRef;

        context.getLog().info("Account started " + context.getSelf().path().name());
    }

    @Override
    public Receive<AccountCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Debit.class, this::onDebit)
                .onMessage(Credit.class, this::onCredit)
                .onMessage(PrintBalance.class, this::onPrintBalance)
                .build();
    }

    private Behavior<AccountCommand> onDebit(Debit debit) {
        getContext().getLog().info(String.format("Account %s credit %d. Old balance: %d, new balance %d",
                getContext().getSelf().path().name(), debit.amount, balance, balance + debit.amount));

        balance += debit.amount;

        return this;
    }

    private Behavior<AccountCommand> onCredit(Credit credit) {
        getContext().getLog().info(String.format("Account %s credit %d. Old balance: %d, new balance %d",
                getContext().getSelf().path().name(), credit.amount, balance, balance + credit.amount));

        balance += credit.amount;

        return this;
    }

    private Behavior<AccountCommand> onPrintBalance(PrintBalance printBalance) {
        getContext().getLog().info(String.format("Account %s balance: %d", getContext().getSelf().path().name(), balance));

        return this;
    }
}
