import java.io.*;
import java.util.*;

abstract class Account {
    protected String accountName;
    protected double accountBalance;
    protected int accountNumber;

    public Account(String name, double balance, int accountNumber) {
        this.accountName = name;
        this.accountBalance = balance;
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void deposit(double amount) {
        accountBalance += amount;
        System.out.println("Deposit Successful! New balance: " + accountBalance);
    }

    public abstract boolean withdraw(double amount);

    public int getAccountNumber() {
        return accountNumber;
    }

    public void displayAccountDetails() {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Name: " + accountName);
        System.out.println("Account Balance: " + accountBalance + " Rs");
    }
}

class SavingsAccount extends Account {
    private static final double MIN_BALANCE = 500;

    public SavingsAccount(String name, double balance, int accountNumber) {
        super(name, balance, accountNumber);
    }

    @Override
    public boolean withdraw(double amount) {
        if (accountBalance - amount >= MIN_BALANCE) {
            accountBalance -= amount;
            System.out.println("Withdrawal Successful! New balance: " + accountBalance);
            return true;
        } else {
            System.out.println("Insufficient balance. Minimum balance required: " + MIN_BALANCE);
            return false;
        }
    }
}

class CurrentAccount extends Account {
    private static final double OVERDRAFT_LIMIT = 10000;

    public CurrentAccount(String name, double balance, int accountNumber) {
        super(name, balance, accountNumber);
    }

    @Override
    public boolean withdraw(double amount) {
        if (accountBalance - amount >= -OVERDRAFT_LIMIT) {
            accountBalance -= amount;
            System.out.println("Withdrawal Successful! New balance: " + accountBalance);
            return true;
        } else {
            System.out.println("Overdraft limit exceeded. Maximum allowed overdraft: " + OVERDRAFT_LIMIT);
            return false;
        }
    }
}

public class Main {
    private static List<Account> accounts = new ArrayList<>();
    private static final String FILE_NAME = "bank_accounts.txt";
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();
        boolean condition = true;

        while (condition) {
            System.out.println("Banking Menu: ");
            System.out.println("Select any one option from below.");
            System.out.println("1) Add Customer");
            System.out.println("2) Change Customer Name");
            System.out.println("3) Check Account Balance");
            System.out.println("4) Deposit Amount");
            System.out.println("5) Withdraw Amount");
            System.out.println("6) Summary of All Accounts");
            System.out.println("7) Quit");

            System.out.print("Enter your option: ");
            int option = sc.nextInt();

            switch (option) {
                case 1:
                    addCustomer();
                    break;
                case 2:
                    changeCustomerName();
                    break;
                case 3:
                    checkBalance();
                    break;
                case 4:
                    depositAmount();
                    break;
                case 5:
                    withdrawAmount();
                    break;
                case 6:
                    summaryOfAccounts();
                    break;
                case 7:
                    saveData();
                    System.out.println("Terminating...");
                    condition = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addCustomer() {
        System.out.println("\nAdd Customer Menu");
        sc.nextLine(); // Consume newline
        System.out.print("Enter Customer Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Initial Balance: ");
        double balance = sc.nextDouble();
        System.out.println("Select Account Type (1 for Savings, 2 for Current): ");
        int accountType = sc.nextInt();

        Account account;
        if (accountType == 1) {
            account = new SavingsAccount(name, balance, accounts.size());
        } else if (accountType == 2) {
            account = new CurrentAccount(name, balance, accounts.size());
        } else {
            System.out.println("Invalid account type. Account not created.");
            return;
        }

        accounts.add(account);
        System.out.println("Account created successfully! Account Number: " + account.getAccountNumber());
    }

    private static void changeCustomerName() {
        System.out.println("\nChange Customer Name Menu");
        System.out.print("Enter your Account Number: ");
        int accountNumber = sc.nextInt();

        if (accountNumber >= accounts.size()) {
            System.out.println("Account does not exist.");
        } else {
            sc.nextLine(); // Consume newline
            System.out.print("Enter the new name: ");
            String newName = sc.nextLine();
            Account account = accounts.get(accountNumber);
            String oldName = account.getAccountName();
            account.accountName = newName;
            System.out.println("Name updated from " + oldName + " to " + newName);
        }
    }

    private static void checkBalance() {
        System.out.println("\nCheck Account Balance Menu");
        System.out.print("Enter your Account Number: ");
        int accountNumber = sc.nextInt();

        if (accountNumber >= accounts.size()) {
            System.out.println("Account does not exist.");
        } else {
            Account account = accounts.get(accountNumber);
            account.displayAccountDetails();
        }
    }

    private static void depositAmount() {
        System.out.println("\nDeposit Amount Menu");
        System.out.print("Enter your Account Number: ");
        int accountNumber = sc.nextInt();

        if (accountNumber >= accounts.size()) {
            System.out.println("Account does not exist.");
        } else {
            System.out.print("Enter amount to deposit: ");
            double amount = sc.nextDouble();
            Account account = accounts.get(accountNumber);
            account.deposit(amount);
        }
    }

    private static void withdrawAmount() {
        System.out.println("\nWithdraw Amount Menu");
        System.out.print("Enter your Account Number: ");
        int accountNumber = sc.nextInt();

        if (accountNumber >= accounts.size()) {
            System.out.println("Account does not exist.");
        } else {
            System.out.print("Enter amount to withdraw: ");
            double amount = sc.nextDouble();
            Account account = accounts.get(accountNumber);
            account.withdraw(amount);
        }
    }

    private static void summaryOfAccounts() {
        System.out.println("\nSummary of All Accounts");
        for (Account account : accounts) {
            account.displayAccountDetails();
            System.out.println();
        }
    }

    private static void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            accounts = (List<Account>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No existing data found, starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    private static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(accounts);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
}