package org.example;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class BudgetManagerGUI extends Application {
    private BudgetManager budgetManager = new BudgetManager();
    private Label balanceLabel;
    private TableView<Transaction> transactionTable;
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private PieChart expenseChart;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Budget Manager");

        balanceLabel = new Label("Current Balance: 0 PLN");
        transactionTable = new TableView<>();
        setupTable();

        expenseChart = new PieChart();
        expenseChart.setTitle("Expense Breakdown");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Home", "Work", "Entertainment", "Food", "Transport", "Health", "Education", "Savings", "Other");
        categoryBox.setValue("Food");

        Button addIncomeButton = new Button("Add Income");
        addIncomeButton.setOnAction(e -> addTransaction(amountField, categoryBox, true));

        Button addExpenseButton = new Button("Add Expense");
        addExpenseButton.setOnAction(e -> addTransaction(amountField, categoryBox, false));

        Button deleteButton = new Button("Delete Selected Transaction");
        deleteButton.setOnAction(e -> deleteTransaction());

        Button clearTransactionsButton = new Button("Clear Transactions");
        clearTransactionsButton.setOnAction(e -> clearTransactions());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(balanceLabel, transactionTable, amountField, categoryBox, addIncomeButton, addExpenseButton, deleteButton, clearTransactionsButton, expenseChart);

        primaryStage.setScene(new Scene(layout, 600, 700));
        primaryStage.show();

        loadTransactions();
        updatePieChart();
    }

    private void setupTable() {
        TableColumn<Transaction, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(cellData ->
                cellData.getValue().isIncomeProperty().get()
                        ? new SimpleStringProperty("Income")
                        : new SimpleStringProperty("Expense")
        );

        TableColumn<Transaction, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        TableColumn<Transaction, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        TableColumn<Transaction, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateTimeProperty());

        transactionTable.getColumns().addAll(typeColumn, categoryColumn, amountColumn, dateColumn);
        transactionTable.setItems(transactionList);
    }

    private void loadTransactions() {
        transactionList.clear();
        transactionList.addAll(budgetManager.getTransactions());
        transactionTable.setItems(transactionList);
        updateBalance();
        updatePieChart();
    }

    private void updateBalance() {
        balanceLabel.setText("Current Balance: " + budgetManager.getBalance() + " PLN");
    }

    private void updatePieChart() {
        expenseChart.getData().clear();
        Map<String, Double> categorySums = new HashMap<>();
        double totalExpenses = 0;

        for (Transaction t : budgetManager.getTransactions()) {
            if (!t.isIncome()) {
                categorySums.put(t.getCategory(), categorySums.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
                totalExpenses += t.getAmount();
            }
        }

        for (Map.Entry<String, Double> entry : categorySums.entrySet()) {
            double percentage = (entry.getValue() / totalExpenses) * 100;
            String formattedLabel = String.format("%s (%.1f%%)", entry.getKey(), percentage);
            PieChart.Data slice = new PieChart.Data(formattedLabel, entry.getValue());
            expenseChart.getData().add(slice);
        }
    }

    private void addTransaction(TextField amountField, ComboBox<String> categoryBox, boolean isIncome) {
        String amountText = amountField.getText().trim();

        if (amountText.isEmpty()) {
            showAlert("Input Error", "Please enter an amount.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            String category = categoryBox.getValue();

            budgetManager.addTransaction(amount, category, isIncome);
            amountField.clear();
            loadTransactions();
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Invalid amount. Please enter a valid number.");
        }
    }

    private void deleteTransaction() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            budgetManager.deleteTransaction(selected.getId());
            loadTransactions();
            updateBalance();
            updatePieChart();
        } else {
            showAlert("Delete Error", "No transaction selected.");
        }
    }

    private void clearTransactions() {
        budgetManager.clearAllTransactions();
        loadTransactions();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}