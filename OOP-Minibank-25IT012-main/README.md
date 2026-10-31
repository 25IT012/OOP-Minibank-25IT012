# MiniBank (OOP Lab)

Console banking application made for Java OOP lab.

Roll No: 25IT012
Subject: Object-Oriented Programming with Java  

---

## Lab Practicals Done

- **Practical 1: Basic Menu Setup**
  - Setup basic project and main menu with Scanner.
  - Used record `BankInfo` and enum `MenuOption` with switch expression.

- **Practical 2: Customer and Account Classes**
  - Created `Customer` and `Account` classes.
  - Used private fields and getters for encapsulation.
  - Balance can only be changed via `deposit()` and `withdraw()`.

- **Practical 3: toString, equals and Nested Class**
  - Added `toString()`, `equals()`, and `hashCode()` in `Account`.
  - Added static nested class `Address` in `Customer`.
  - Implemented `Cloneable` interface in `Customer`.

- **Practical 4: Validation and Command Parser**
  - Created regex validation for mobile, email, PAN and IFSC in `Validator`.
  - Added `CommandParser` to split and read string commands like "DEPOSIT AC0001 500".
  - Used `StringBuilder` in `StatementFormatter`.

- **Practical 5: Inheritance and Polymorphism**
  - Made `Account` abstract with `interestRate()` and `canWithdraw()`.
  - Created subclasses: `SavingsAccount`, `CurrentAccount`, and `FixedDepositAccount`.
  - Used polymorphism and `instanceof` check in main.

- **Practical 6: Interfaces and Packages**
  - Created interfaces: `Transactable`, `InterestBearing`, `WithdrawRule`, and marker interface `Premium`.
  - Divided classes into packages: `model`, `service`, `util`.
  - Created runnable `MiniBank.jar`.

- **Practical 7: Custom Annotations and Reflection**
  - Created annotations: `@Id`, `@Positive`, `@MaxLength` in `model.annotation`.
  - Created `AnnotationValidator` using reflection to validate fields at runtime.

- **Practical 8: Exception Handling**
  - Created custom checked exceptions: `BankException`, `InsufficientFundsException`, `AccountNotFoundException`, `InvalidAmountException`.
  - Used `try-catch-finally` and `try-with-resources`.

- **Practical 9: Multithreading and Synchronization**
  - Created `AccountWorker` implementing `Runnable`.
  - Used `synchronized` on deposit/withdraw to prevent race conditions.

- **Practical 10: Thread Pool and Deadlock Prevention**
  - Used `ExecutorService` thread pool in `TransactionProcessor`.
  - Used `wait()` and `notifyAll()` in `TransactionBuffer`.
  - Fixed deadlock in `TransferService` by locking accounts in fixed order by account number.

---

## Folder Structure

```
MINIBANK/
│── exception/
│   ├── AccountNotFoundException.java
│   ├── BankException.java
│   ├── InsufficientFundsException.java
│   └── InvalidAmountException.java
│── model/
│   ├── annotation/
│   │   ├── Id.java
│   │   ├── MaxLength.java
│   │   └── Positive.java
│   ├── Account.java
│   ├── BankInfo.java
│   ├── Command.java
│   ├── CurrentAccount.java
│   ├── Customer.java
│   ├── FixedDepositAccount.java
│   ├── InterestBearing.java
│   ├── MenuOption.java
│   ├── Premium.java
│   ├── SavingsAccount.java
│   ├── Transactable.java
│   └── TransactionType.java
│── service/
│   ├── AccountWorker.java
│   ├── CommandParser.java
│   ├── TransactionProcessor.java
│   ├── TransferService.java
│   └── WithdrawRule.java
│── util/
│   ├── AnnotationValidator.java
│   ├── StatementFormatter.java
│   ├── TransactionBuffer.java
│   ├── TransactionSession.java
│   └── Validator.java
│── MiniBank.java
│── MiniBank.jar
└── README.md
```

---

## How to Run

1. Compile:
```bash
javac -d bin MiniBank.java model/*.java model/annotation/*.java util/*.java service/*.java exception/*.java
```

2. Run:
```bash
java -cp bin MiniBank
```

Or run the jar file:
```bash
java -jar MiniBank.jar
```
