# Object Oriented Programming (CEUE203) — Laboratory & Capstone Repository

**Author:** Aryan Amdavadi  
**Student ID:** 25IT002  
**Department:** Information Technology, CSPIT — CHARUSAT  
**Academic Year:** 2026–27 | Semester 3  

---

## 📁 Repository Overview

This repository is organized into two primary components:

```
oop-minibank-25IT002/
├── MINI-BANK/                # Part B: Enterprise Banking Console Application (Milestones lab-01 to lab-14)
│   ├── src/                  # Source code (model, service, util, exception packages)
│   ├── lib/                  # External dependencies (JUnit 5 Platform Standalone)
│   ├── data/                 # Serialized accounts & EOD transaction summaries
│   ├── logs/                 # Transaction & audit log outputs
│   └── README.md             # Dedicated documentation for the MiniBank project
│
└── Part-A/                   # Part A: Practical List Practice Programs (Practicals 01 to 14)
    ├── Practical_01/         # Vending Machine (Enums) & Toll Booth (Records & Switch Expressions)
    ├── Practical_02/         # Smart Thermostat (Encapsulation) & Cinema Show (Class Modeling)
    ├── Practical_03/         # Distinct Points (equals/hashCode) & Duplicate Card Check
    ├── Practical_04/         # Password Strength Checker (Regex) & Chat-log Filter (StringBuilder)
    ├── Practical_05/         # Shape Areas (Abstract Classes/Polymorphism) & Payroll System
    ├── Practical_06/         # Remote Control (Interfaces/Lambdas) & Notification Senders
    ├── Practical_07/         # Form Validator (@NotBlank Reflection) & Mini Test Runner (@Run)
    ├── Practical_08/         # Guarded Calculator (Exception Handling) & Stock Issue System
    ├── Practical_09/         # Counter Race (Thread Synchronization) & Cinema Seat Booking Race
    ├── Practical_10/         # Thread Pool Runner (ExecutorService) & Producer-Consumer Buffer
    ├── Practical_11/         # Save/Load Serialization & NIO.2 Log File Analyzer
    ├── Practical_12/         # Word Frequency Map (HashMap/Comparator) & Unique Visitors Set
    ├── Practical_13/         # Generic Containers (Box<T>, Pair<A,B>) & Bounded Statistics
    └── Practical_14/         # Layered App Architecture & Automated JUnit 5 Unit Testing
```

---

## 🚀 Running the Projects

### Part A: Practice Programs
Each practical in `Part-A` is organized into modular task subfolders containing self-contained source files, a runnable `Driver.java`, and the corresponding lab submission report PDF.

To compile and run any practical task:
```bash
# Example: Practical 1 - Task 1 (Vending Machine)
cd Part-A/Practical_01/Task1_VendingMachine
javac *.java
java Driver

# Example: Practical 12 - Task 1 (Word Frequency)
cd Part-A/Practical_12/Task1_WordFrequency
javac *.java
java Driver
```

### Part B: MiniBank Capstone
The MiniBank enterprise banking console application resides inside the `MINI-BANK` directory.

To run MiniBank:
```bash
cd MINI-BANK
# Compile:
javac -d bin -cp "src:lib/*" src/service/MiniBank.java

# Run console application:
java -cp "bin:lib/*" service.MiniBank

# Run automated JUnit 5 test suite:
java -jar lib/junit-platform-console-standalone.jar -cp bin --scan-classpath
```

---

## 📌 Technology Stack
- **Language:** Java 17+ / Modern Java
- **Testing:** JUnit 5 (Jupiter Engine)
- **Persistence:** Java Object Binary Serialization & NIO.2
- **Version Control:** Git & GitHub
