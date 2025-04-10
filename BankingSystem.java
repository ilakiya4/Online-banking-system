import java.util.*;

class Account {
    String userName;
    String password;
    String accountNumber;
    double balance;
    List<String> transactions;

    Account(String userName, String password, String accountNumber) {
        this.userName = userName;
        this.password = password;
        this.accountNumber = accountNumber;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        transactions.add("Account created with balance ₹0");
    }

    void deposit(double amount) {
        balance += amount;
        transactions.add("Deposited ₹" + amount);
        System.out.println("Deposited: ₹" + amount);
    }

    void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            transactions.add("Withdrew ₹" + amount);
            System.out.println("Withdrawn: ₹" + amount);
        } else {
            System.out.println("Insufficient balance!");
        }
    }

    void viewBalance() {
        System.out.println("Current Balance: ₹" + balance);
    }

    void showTransactions() {
        System.out.println("Transaction History:");
        for (String t : transactions) {
            System.out.println(" - " + t);
        }
    }

    void receiveTransfer(double amount, String fromUser) {
        balance += amount;
        transactions.add("Received ₹" + amount + " from " + fromUser);
    }

    boolean sendTransfer(double amount, Account recipient) {
        if (amount <= balance) {
            balance -= amount;
            transactions.add("Sent ₹" + amount + " to " + recipient.userName);
            recipient.receiveTransfer(amount, this.userName);
            return true;
        } else {
            System.out.println("Transfer failed: Insufficient balance.");
            return false;
        }
    }
}

public class BankingSystem {
    static Scanner sc = new Scanner(System.in);
    static Map<String, Account> accounts = new HashMap<>(); // username → account
    static Map<String, Account> accountNumbers = new HashMap<>(); // accNo → account
    static Account currentUser = null;

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n===== Online Banking System =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            System.out.print("Select option: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> adminLogin();
                case 4 -> System.out.println("Exiting. Thank you!");
                default -> System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 4);
    }

    static void register() {
        System.out.print("Enter username: ");
        String user = sc.next();
        if (accounts.containsKey(user)) {
            System.out.println("Username already exists.");
            return;
        }
        System.out.print("Create password: ");
        String pass = sc.next();

        String accNo = generateAccountNumber();
        Account acc = new Account(user, pass, accNo);
        accounts.put(user, acc);
        accountNumbers.put(accNo, acc);

        System.out.println("Registered successfully!");
        System.out.println("Your Account Number: " + accNo);
    }

    static void login() {
        System.out.print("Enter username: ");
        String user = sc.next();
        System.out.print("Enter password: ");
        String pass = sc.next();

        if (accounts.containsKey(user) && accounts.get(user).password.equals(pass)) {
            currentUser = accounts.get(user);
            System.out.println("Login successful!");
            userMenu();
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    static void userMenu() {
        int option;
        do {
            System.out.println("\nWelcome, " + currentUser.userName + " (Acc#: " + currentUser.accountNumber + ")");
            System.out.println("1. View Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer to Another Account");
            System.out.println("5. View Transactions");
            System.out.println("6. View Statement of Receipt");
            System.out.println("7. Logout");
            System.out.print("Select option: ");
            option = sc.nextInt();

            switch (option) {
                case 1 -> currentUser.viewBalance();
                case 2 -> {
                    System.out.print("Enter amount: ");
                    double amt = sc.nextDouble();
                    currentUser.deposit(amt);
                }
                case 3 -> {
                    System.out.print("Enter amount: ");
                    double amt = sc.nextDouble();
                    currentUser.withdraw(amt);
                }
                case 4 -> transferFunds();
                case 5 -> currentUser.showTransactions();
                case 6 -> printStatement();
                case 7 -> System.out.println("Logged out.");
                default -> System.out.println("Invalid option.");
            }
        } while (option != 7);
    }

    static void transferFunds() {
        System.out.print("Enter recipient's account number: ");
        String accNo = sc.next();
        if (!accountNumbers.containsKey(accNo)) {
            System.out.println("No user found with that account number.");
            return;
        }
        Account recipient = accountNumbers.get(accNo);
        if (recipient.accountNumber.equals(currentUser.accountNumber)) {
            System.out.println("Cannot transfer to your own account.");
            return;
        }
        System.out.print("Enter amount to transfer: ");
        double amt = sc.nextDouble();
        boolean success = currentUser.sendTransfer(amt, recipient);
        if (success) {
            System.out.println("Transfer successful to " + recipient.userName);
        }
    }

    static void printStatement() {
        System.out.println("\nBANK STATEMENT RECEIPT ");
        System.out.println("Date/Time     : " + new Date());
        System.out.println("Username      : " + currentUser.userName);
        System.out.println("Account Number: " + currentUser.accountNumber);
        System.out.println("Current Balance: ₹" + currentUser.balance);
        System.out.println("\n--- Transaction History ---");
        for (String txn : currentUser.transactions) {
            System.out.println("• " + txn);
        }
        System.out.println("===============================================");
    }

    static void adminLogin() {
        System.out.print("Enter admin password: ");
        String password = sc.next();
        if (password.equals("admin123")) {
            System.out.println("\n--- All User Accounts ---");
            for (Account acc : accounts.values()) {
                System.out.println("\nUsername: " + acc.userName);
                System.out.println("Account Number: " + acc.accountNumber);
                System.out.println("Balance: ₹" + acc.balance);
                System.out.println("--- Transactions ---");
                for (String txn : acc.transactions) {
                    System.out.println("  > " + txn);
                }
            }
        } else {
            System.out.println("Incorrect admin password.");
        }
    }

    static String generateAccountNumber() {
        Random rand = new Random();
        String accNo;
        do {
            accNo = String.valueOf(1000000000L + (long)(rand.nextDouble() * 9000000000L));
        } while (accountNumbers.containsKey(accNo));
        return accNo;
    }
}
