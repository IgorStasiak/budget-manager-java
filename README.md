# 💰 Budget Manager

Budget Manager is a simple **JavaFX** application for managing personal finances. It allows users to add, view, and delete transactions, calculate balance, and visualize expenses with a pie chart.

## 🚀 Features

- Add transactions (**income and expenses**)
- View the transaction list
- Calculate current balance
- **Pie chart visualization** of expenses with percentages
- Delete a single transaction or clear the entire history
- Simple and clean **JavaFX UI**
- Uses **SQLite database** for transaction storage

## 📥 Installation

### 📌 Prerequisites:
- **Java 17+** (e.g., OpenJDK 17)
- **Maven** (for dependency management)

### 📦 Clone the repository:
```sh
git clone https://github.com/YourUsername/budget-manager.git
cd budget-manager
```
### ▶️ Run the application:
```sh
mvn clean package
java -jar target/budget-manager-1.0-SNAPSHOT.jar
```
## 🛠️ Usage
1.	**Add transactions** (income or expenses).
2.	**View the list** of all transactions.
3.	**Check the balance** after each transaction.
4.	**Analyze spending** with the pie chart visualization.
5.	**Delete** a single transaction or clear all transactions.
6.	**Exit** the application and reload previous transactions from the database.

## 📜 License
This project is licensed under the MIT License. You are free to use, modify, and distribute it. See the LICENSE file for details.