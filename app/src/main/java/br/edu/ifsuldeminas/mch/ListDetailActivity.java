package br.edu.ifsuldeminas.mch;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListDetailActivity extends AppCompatActivity {

    private LinearLayout itemsLayout;
    private Button backButton;
    private DatabaseHelper dbHelper;
    private int listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        itemsLayout = findViewById(R.id.itemsLayout);
        backButton = findViewById(R.id.backButton);
        dbHelper = new DatabaseHelper(this);

        listId = getIntent().getIntExtra("LIST_ID", -1);

        loadItemsFromDatabase();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Voltar para a tela DisplayListActivity
                Intent intent = new Intent(ListDetailActivity.this, DisplayListActivity.class);
                startActivity(intent);
                finish(); // Opcional: Finalizar a atividade atual se não for mais necessária
            }
        });
    }

    private void loadItemsFromDatabase() {
        Cursor cursor = dbHelper.getItemsByListId(listId);
        if (cursor.moveToFirst()) {
            do {
                String item = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM));
                boolean completed = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMPLETED)) == 1;
                addItemToLayout(item, completed);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void addItemToLayout(String item, boolean completed) {
        TextView textView = new TextView(this);
        textView.setText(item);
        textView.setTextSize(16);
        textView.setPadding(0, 8, 0, 8);
        if (completed) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView clickedTextView = (TextView) v;
                // Alterar o estilo do texto quando clicado (marcar como concluído)
                if ((clickedTextView.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
                    clickedTextView.setPaintFlags(clickedTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    updateItemCompletion(clickedTextView.getText().toString(), false);
                } else {
                    clickedTextView.setPaintFlags(clickedTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    updateItemCompletion(clickedTextView.getText().toString(), true);
                }
            }
        });
        itemsLayout.addView(textView);
    }

    private void updateItemCompletion(String item, boolean completed) {
        dbHelper.updateItemCompletion(listId, item, completed);
    }
}
