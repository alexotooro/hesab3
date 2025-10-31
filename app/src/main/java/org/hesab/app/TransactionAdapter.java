package org.hesab.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactionList;
    private Context context;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEdit(Transaction transaction);
        void onDelete(Transaction transaction);
        void onMoveMode(Transaction transaction);
    }

    public TransactionAdapter(Context context, List<Transaction> transactionList, OnItemActionListener listener) {
        this.context = context;
        this.transactionList = transactionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = transactionList.get(position);

        holder.tvDate.setText(t.date);
        DecimalFormat df = new DecimalFormat("#,###");
        holder.tvAmount.setText(df.format(t.amount));

        holder.tvAmount.setTextColor(context.getResources().getColor(
                t.isIncome ? R.color.income_green : R.color.expense_red));

        holder.tvCategory.setText(t.category);
        holder.tvDescription.setText(t.description);

        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_transaction_item, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_edit:
                        listener.onEdit(t);
                        return true;
                    case R.id.menu_delete:
                        listener.onDelete(t);
                        return true;
                    case R.id.menu_move:
                        listener.onMoveMode(t);
                        return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvAmount, tvCategory, tvDescription;
        ImageButton btnMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }
    }
}
