package org.example;

import javafx.beans.property.*;

public class Transaction {
    private final SimpleIntegerProperty id;
    private final SimpleDoubleProperty amount;
    private final SimpleStringProperty category;
    private final SimpleBooleanProperty isIncome;
    private final SimpleStringProperty dateTime;

    public Transaction(int id, double amount, String category, boolean isIncome, String dateTime) {
        this.id = new SimpleIntegerProperty(id);
        this.amount = new SimpleDoubleProperty(amount);
        this.category = new SimpleStringProperty(category);
        this.isIncome = new SimpleBooleanProperty(isIncome);
        this.dateTime = new SimpleStringProperty(dateTime);
    }

    public IntegerProperty idProperty() { return id; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty categoryProperty() { return category; }
    public BooleanProperty isIncomeProperty() { return isIncome; }
    public StringProperty dateTimeProperty() { return dateTime; }

    public int getId() { return id.get(); }
    public double getAmount() { return amount.get(); }
    public String getCategory() { return category.get(); }
    public boolean isIncome() { return isIncome.get(); }
    public String getDateTime() { return dateTime.get(); }

    public String getType() {
        return isIncome.get() ? "Income" : "Expense";
    }

    @Override
    public String toString() {
        return (isIncome.get() ? "💰 Income: " : "💸 Expense: ") + amount.get() + " | " + category.get() + " | " + dateTime.get();
    }
}