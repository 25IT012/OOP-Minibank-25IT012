# Object-Oriented Mini Bank System (oop-minibank-25IT002)

MiniBank is a layered, console-based banking application built in Java following core Object-Oriented Programming (OOP) principles, clean architectural patterns, concurrency controls, custom annotations with reflection, NIO-based persistence, and automated unit testing.

---

## Architecture & Practical Progression

MiniBank has been developed progressively across Practicals 1 to 14 (Part B):

- **Practical 1 (lab-01):** Application shell, BankInfo record, MenuOption enum, and menu loop.
- **Practical 2 (lab-02):** Core Customer and Account entities with encapsulation and static id generation.
- **Practical 3 (lab-03):** Account equality and hashing (`equals`/`hashCode`), string representation (`toString`), nested `Address` class, and cloning.
- **Practical 4 (lab-04):** Regular expression validation (`Validator` for mobile, email, PAN, IFSC), Command parser, and StringBuilder statement formatter.
- **Practical 5 (lab-05):** Polymorphic account hierarchy with abstract `Account` and concrete subtypes `SavingsAccount`, `CurrentAccount`, and `FixedDepositAccount`.
- **Practical 6 (lab-06):** Interface decoupling (`Transactable`, `InterestBearing`, `WithdrawRule`, `Premium`), package restructuring (`model`, `util`, `service`), and runnable JAR packaging.
- **Practical 7 (lab-07):** Custom runtime annotations (`@Id`, `@Positive`, `@MaxLength`) and reflection-based validator (`AnnotationValidator`).
- **Practical 8 (lab-08):** Custom exception hierarchy (`BankException`, `InsufficientFundsException`, `InvalidAmountException`, `AccountNotFoundException`) and try-with-resources resource management (`BankSession`).
- **Practical 9 (lab-09):** Multithreading and synchronization against shared account state (`AccountWorker`, `synchronized` blocks and methods).
- **Practical 10 (lab-10):** Thread pool execution (`TransactionProcessor`), producer-consumer buffer with `wait()` and `notify()`, and deadlock prevention using ordered locking.
- **Practical 11 (lab-11):** Object serialization persistence (`StatePersister`), transaction logs (`TransactionLog`), and NIO directory stream reporting (`ReportGenerator`).
- **Practical 12 (lab-12):** Java Collections Framework storage (`AccountStore` backed by `HashMap`), balance sorting via `Comparator`, and `Comparable` natural sorting.
- **Practical 13 (lab-13):** Reusable generic `Repository<T>` with reflection-based `@Id` extraction, varargs batch saving, and bounded generic calculations.
- **Practical 14 (lab-14):** Capstone integration (`BankService`), interactive menu wiring, automated persistence across restarts, safe concurrent processing, and JUnit 5 unit tests.

---

## Project Structure

```
oop-minibank-25IT002/
├── bin/                       # Compiled class files
├── lib/
│   └── junit-platform-console-standalone.jar
├── src/
│   ├── exception/
│   │   ├── AccountNotFoundException.java
│   │   ├── BankException.java
│   │   ├── InsufficientFundsException.java
│   │   └── InvalidAmountException.java
│   ├── model/
│   │   ├── annotation/
│   │   │   ├── Audit.java
│   │   │   ├── Id.java
│   │   │   ├── MaxLength.java
│   │   │   └── Positive.java
│   │   ├── Account.java
│   │   ├── Command.java
│   │   ├── CurrentAccount.java
│   │   ├── Customer.java
│   │   ├── FixedDepositAccount.java
│   │   ├── InterestBearing.java
│   │   ├── Premium.java
│   │   ├── SavingsAccount.java
│   │   ├── Transactable.java
│   │   ├── TransactionType.java
│   │   └── WithdrawRule.java
│   ├── service/
│   │   ├── AccountStore.java
│   │   ├── AccountWorker.java
│   │   ├── BankService.java
│   │   ├── BankServiceTest.java
│   │   ├── Driver.java
│   │   ├── MiniBank.java
│   │   ├── TransactionProcessor.java
│   │   └── TransactionQueue.java
│   └── util/
│       ├── AnnotationValidator.java
│       ├── AuditLogger.java
│       ├── BankSession.java
│       ├── CommandParser.java
│       ├── ReportGenerator.java
│       ├── Repository.java
│       ├── StatePersister.java
│       ├── StatementFormatter.java
│       ├── TransactionLog.java
│       └── Validator.java
├── .gitignore
├── minibank.jar
└── README.md
```

---

## Build and Execution Guide

### Prerequisites
- JDK 17 or higher
- Git

### 1. Compile the Project

```bash
mkdir -p bin
javac -cp "lib/*" -d bin $(find src -name "*.java")
```

### 2. Run the Interactive Application

Run via classpath:
```bash
java -cp bin service.MiniBank
```

Or package and run the runnable JAR:
```bash
jar cfe minibank.jar service.MiniBank -C bin .
java -jar minibank.jar
```

### 3. Run the Unit Tests

Using the built-in test runner:
```bash
java -cp "bin:lib/*" service.BankServiceTest
```

Using JUnit Platform Standalone Console:
```bash
java -jar lib/junit-platform-console-standalone.jar -cp bin --select-class service.BankServiceTest
```

### 4. Run the Practical Drivers

To test practical-specific components:
```bash
java -cp bin service.Driver
```

---

## Version Control Tags

Each practical milestone is tracked with an official git tag:

- `lab-01`: Menu shell and BankInfo record
- `lab-02`: Account & Customer encapsulation
- `lab-03`: Equals, hashCode, Address, and Cloneable
- `lab-04`: Regex validation, Command parsing, and StatementFormatter
- `lab-05`: Account abstract hierarchy and polymorphism
- `lab-06`: Interfaces, modular package layout, and runnable JAR
- `lab-07`: Metadata annotations and reflection validation
- `lab-08`: Custom checked exceptions and AutoCloseable session
- `lab-09`: Thread synchronization and race condition mitigation
- `lab-10`: Thread pool execution, producer-consumer queue, and deadlock avoidance
- `lab-11`: Object serialization and NIO report generation
- `lab-12`: Collections framework storage and Comparator sorting
- `lab-13`: Generic Repository with reflective @Id resolution
- `lab-14`: Capstone integration, BankService wiring, and JUnit 5 suite
