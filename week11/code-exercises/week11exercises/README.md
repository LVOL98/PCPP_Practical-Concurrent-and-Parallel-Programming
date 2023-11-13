# Exercise 11

## 11.1
*Your task in this exercise is to implement a Mobile Payment system using Akka. The system consists of three types of actors:*
- *Mobile App: These actors send transactions to the bank corresponding to mobile payments.*
- *Bank: These actors are responsible for executing the transactions received from the mobile app. That is, substracting the money from the payer account and adding it to the payee account.*
- Account: This actor type models a single bank account. It contains the balance of the account. Also, banks should be able to send positive deposits and negative deposits (withdrawals) in the account.*

*The directory code-exercises/week11exercises contains a source code skeleton for the system that you might find helpful*

See image in pdf

*The figure above shows an example of the behavior. In this example, there is a mobile app, mb1, a bank, b1, and two accounts, a1 and a2. The arrow from mb1 to b1 models mb1 sending a transaction to b1 indicating to transfer 100 DKK from a1 to a2. Likewise, the arrows from b1 to a1 and a2 model the sending of deposits to the corresponding accounts to realise the transaction—the negative deposit can be seen as a withdrawal.*

reference to all files:

[app/src/main/java/mobilepayment/Account.java](app/src/main/java/mobilepayment/Account.java)
[app/src/main/java/mobilepayment/Bank.java](app/src/main/java/mobilepayment/Bank.java)
[app/src/main/java/mobilepayment/Guardian.java](app/src/main/java/mobilepayment/Guardian.java)
[app/src/main/java/mobilepayment/Main.java](app/src/main/java/mobilepayment/Main.java)
[app/src/main/java/mobilepayment/MobileApp.java](app/src/main/java/mobilepayment/MobileApp.java)

### Mandatory

In the interest of readability only selected parts of code will be shown, but a link to files modified will always be included

#### 11.1.1
***
*Design and implement the guardian actor (in Guardian.java) and complete the Main.java class to start the system. The Main class must send a kick-off message to the guardian. For now, when the guardian receives the kick-off message, it should spawn an MobileApp actor. Finally, explain the design of the guardian, e.g., state (if any), purpose of messages, etc. Also, briefly explain the purpose of the program statements added to the Main.java to start off the actor system.*

*Note: In this exercise you should only modify the files Main.java and Guardian.java. The code skeleton already contains the minimal actor code to start the MobileApp actor. If your implementation is correct, you will observe a message INFO mobilepaymentsolution.MobileApp - Mobile app XXX started! or similar when running the system.*


The file Main.java had a `ActionSystem.create` command added, that would kick off the guardian. Further, to ensure proper shut down a call to `ActionSystem.terminate` was added to the final part of the try-catch block

[app/src/main/java/mobilepayment/Main.java](app/src/main/java/mobilepayment/Main.java)
```java
public class Main {
    public static void main(String[] args) {
        final var system = ActorSystem.create(Guardian.create(), "mobile-payment");

        ...

		try {
            ...
		}
		catch (IOException e) {
            ...
		} finally {
            system.terminate();
		}
    }    
}
```

The file Guardian.java had a `create()` function added, in order for the `ActorSystem` to be able to create the guardian. The constructor also had two lines added, one for logging the startup and another to create the mobile app actor

[app/src/main/java/mobilepayment/Guardian.java](app/src/main/java/mobilepayment/Guardian.java)
```java
public class Guardian extends AbstractBehavior<Guardian.GuardianCommand> {
  public static Behavior<Guardian.GuardianCommand> create() {
    return Behaviors.setup(Guardian::new);
  }

  private Guardian(ActorContext<GuardianCommand> context) {
    super(context);
    context.getLog().info("Guardian started");

    getContext().spawn(MobileApp.create(), "mobile-app");
  }
}
```

***

#### 11.1.2
***
*Design and implement the Account actor (see the file Account.java in the skeleton), including the messages it must handle. Explain your design choices, e.g., elements of the state, how it is initialized, purpose of messages, etc.*

See [app/src/main/java/mobilepayment/Account.java](app/src/main/java/mobilepayment/Account.java)

##### State
- `long balance`: represents the amount of money in the account
- `bankRef`: reference to the bank the account belongs to (not used currently)

##### Messages
- `Debit`: debits a given amount of money from the account
- `Credit`: credits a given amount of money from the account
- `PrintBalance`: prints the balance to the info log

Note, the `bankRef` was kept to represent the relationship of an account to a specific bank

Further, no response messaging is implemented as it wasn't specified in the exercise, but could have been implemented

***

#### 11.1.3
***
*Design and implement the Bank actor (see the file Bank.java in the skeleton), including the messages it must handle. Explain your design choices, e.g., elements of the state, how it is initialized, purpose of messages, etc.*

See [app/src/main/java/mobilepayment/Bank.java](app/src/main/java/mobilepayment/Bank.java)

##### State
As the task didn't require any logic in case transferring money to an account in a different bank, it was decided that the bank actor should simply just relay the transactions to the account, and receive the accounts in the message

This decision was made in the interest of time, as the exercise is learning the Akka framework and not how to implement a bank

##### Messages
- `Transaction`: contains how much money should be transferred from one account to another (credit one account and debit one account the same amount)

***

#### 11.1.4
***
*Design and implement the Mobile App actor (see the file MobileApp.java in the skeleton), including the messages it must handle. Explain your design choices, e.g., elements of the state, how it is initialized, purpose of messages, etc*

##### State
- `myBankRef`: reference to the bank account of the mobile app (user)
- `myAccountRef`: reference to the account of the mobile app (user)

As the mobile app represents the account of one user in one bank, it was included in the state of the actor

The account was also included as we have no mechanism for getting a user's account from a specific bank

##### Messages
- `Transfer`: transfer money from the mobile app's account to an account specified in the message. The amount is also specified in the message
- `MakePayments`: used for exercise 11.1.6

***

#### 11.1.5
***
*Update the guardian so that it starts 2 mobile apps, 2 banks, and 2 accounts. The guardian must be able to send a message to mobile app actors to execute a set of payments between 2 accounts in a specified bank. Finally, the guardian must send two payment messages: 1) from a1 to a2 via b1, and 2) from a2 to a1 via b2. The amount in these payments is a constant of your choice*

See [app/src/main/java/mobilepayment/Guardian.java](app/src/main/java/mobilepayment/Guardian.java)

***

#### 11.1.6
***
*Modify the mobile app actor so that, when it receives a “make payments” message from the guardian, it sends 100 transactions between the specified accounts and bank with a random amount. Hint: You may use Random::ints to generate a stream of random integers. The figure below illustrates the computation*

See [app/src/main/java/mobilepayment/MobileApp.java](app/src/main/java/mobilepayment/MobileApp.java)

***

#### 11.1.7
***
*Update the Account actor so that it can handle a message PrintBalance. The message handler should print the current balance in the target account. Also, update the guardian actor so that it sends PrintBalance messages to accounts 1 and 2 after sending the make payments messages in the previous item.*

*What balance will be printed? The one before all payments are made, or the one after all payments are made, or anything in between, or anything else? Explain your answer*

See [app/src/main/java/mobilepayment/Account.java](app/src/main/java/mobilepayment/Account.java)

While we've only observed cases of the two calls to `PrintBalance` executing before the `MakePayments` this is only by coincidence. Due to the asynchronous nature of Akka the `PrintBalance` could just as well have appeared in the `Account` actor's mailbox after it started - or finished - all its `Transfer`s

Now due to the size of the `PrintBalance` and the start up time of `MakePayment` it is highly likely that `PrintBalance` will execute before `MakePayments` but there's no guarantees

***

#### 11.1.8
***
*Consider a case where two different bank actors send two deposit exactly at the same time to the same account actor. Can data races occur when updating the balance? Explain your answer*

No data races can occur as only one message can be processed at a time, while all others wait in the mailbox

What can happen is data loss - if not handled properly - as the mailbox can overflow leading to lost messages 

***
