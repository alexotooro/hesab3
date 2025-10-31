package org.hesab.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.util.Calendar;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etDate, etAmount, etCategory, etDescription;
    private Spinner spBank;
    private RadioButton rbIncome, rbExpense;
    private Button btnSave;
    private TransactionDao dao;
    private Transaction editTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        dao = TransactionDatabase.getInstance(this).transactionDao();

        etDate = findViewById(R.id.etDate);
        etAmount = findViewById(R.id.etAmount);
        etCategory = findViewById(R.id.etCategory);
        etDescription = findViewById(R.id.etDescription);
        spBank = findViewById(R.id.spBank);
        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);
        btnSave = findViewById(R.id.btnSave);

        etDate.setOnClickListener(v -> showDatePicker());
        etAmount.addTextChangedListener(new NumberTextWatcher(etAmount));

        int editId = getIntent().getIntExtra("editTransactionId", -1);
        if (editId != -1) {
            for (Transaction t : dao.getAll()) {
                if (t.id == editId) {
                    editTransaction = t;
                    etDate.setText(t.date);
                    etAmount.setText(String.valueOf(t.amount));
                    etCategory.setText(t.category);
                    etDescription.setText(t.description);
                    rbIncome.setChecked(t.isIncome);
                    rbExpense.setChecked(!t.isIncome);
                    break;
                }
            }
        }

        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        String date = etDate.getText().toString();
        long amount = Long.parseLong(etAmount.getText().toString().replace(",", ""));
        String category = etCategory.getText().toString();
        String description = etDescription.getText().toString();
        boolean isIncome = rbIncome.isChecked();
        String bank = spBank.getSelectedItem().toString();

        if (editTransaction != null) {
            editTransaction.date = date;
            editTransaction.amount = amount;
            editTransaction.category = category;
            editTransaction.description = description;
            editTransaction.isIncome = isIncome;
            editTransaction.bankName = bank;
            dao.update(editTransaction);
        } else {
            int orderIndex = dao.getAll().size();
            Transaction t = new Transaction(date, amount, category, description, isIncome, bank, orderIndex);
            dao.insert(t);
        }

        finish();
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    PersianDate pDate = new PersianDate();
                    pDate.setGrgDate(year, month + 1, day);
                    PersianDateFormat pdForm = new PersianDateFormat("Y/m/d");
                    etDate.setText(pdForm.format(pDate));
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }
}
