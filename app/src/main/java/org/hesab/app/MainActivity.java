package org.hesab.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TransactionAdapter.OnItemActionListener {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private TransactionDao dao;
    private TextView tvBalance;
    private CheckBox cbOnlyExpenses, cbOnlyIncomes;
    private Spinner bankFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = TransactionDatabase.getInstance(this).transactionDao();

        recyclerView = findViewById(R.id.recyclerView);
        tvBalance = findViewById(R.id.tvBalance);
        cbOnlyExpenses = findViewById(R.id.cbOnlyExpenses);
        cbOnlyIncomes = findViewById(R.id.cbOnlyIncomes);
        bankFilter = findViewById(R.id.bankFilter);

        loadTransactions();

        Button btnAdd = findViewById(R.id.btnAddTransaction);
        btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(i);
        });
    }

    private void loadTransactions() {
        transactionList = dao.getAll();
        adapter = new TransactionAdapter(this, transactionList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        long balance = dao.getBalance();
        tvBalance.setText("مانده: " + balance + " ریال");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions();
    }

    @Override
    public void onEdit(Transaction t) {
        Intent i = new Intent(this, AddTransactionActivity.class);
        i.putExtra("editTransactionId", t.id);
        startActivity(i);
    }

    @Override
    public void onDelete(Transaction t) {
        dao.delete(t);
        loadTransactions();
    }

    @Override
    public void onMoveMode(Transaction t) {
        // بعداً قابلیت جابجایی فعال می‌شود (Drag & Drop)
    }
}
