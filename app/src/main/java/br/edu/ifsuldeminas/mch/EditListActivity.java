package br.edu.ifsuldeminas.mch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditListActivity extends AppCompatActivity {

    private EditText listTitleEditText;
    private EditText addItemText;
    private TextView itemsAdded;
    private Button addButton;
    private Button deleteButton;
    private Button finishButton;
    private List<String> items;
    private ArrayAdapter<String> adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        listTitleEditText = findViewById(R.id.listTitleEditText);
        addItemText = findViewById(R.id.addItemText);
        itemsAdded = findViewById(R.id.itemsAdded);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        finishButton = findViewById(R.id.finishButton);

        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = addItemText.getText().toString().trim();
                if (!item.isEmpty()) {
                    items.add(item);
                    adapter.notifyDataSetChanged();
                    itemsAdded.setText(getItemsText());
                    Toast.makeText(EditListActivity.this, "Item adicionado", Toast.LENGTH_SHORT).show();
                    addItemText.setText("");
                }
            }
        });

        ImageView backButton = findViewById(R.id.ic_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!items.isEmpty()) {
                    items.remove(items.size() - 1);
                    adapter.notifyDataSetChanged();
                    itemsAdded.setText(getItemsText());
                    Toast.makeText(EditListActivity.this, "Último item removido", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditListActivity.this, "A lista está vazia", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dbHelper = new DatabaseHelper(this);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = listTitleEditText.getText().toString().trim();
                if (!title.isEmpty() && !items.isEmpty()) {
                    long listId = saveList(title); // Salvar a lista e obter o ID
                    saveItems(listId); // Salvar os itens da lista
                    Toast.makeText(EditListActivity.this, "Lista criada com sucesso", Toast.LENGTH_SHORT).show();

                    // Atualizar a lista na DisplayListActivity
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);

                    finish();
                } else {
                    Toast.makeText(EditListActivity.this, "Preencha o título da lista e adicione pelo menos um item", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private long saveList(String title) {
        return dbHelper.saveList(title); // Retorna o ID da lista salva
    }

    private void saveItems(long listId) {
        Cursor cursor = dbHelper.getItemsByListId(listId); // Obter os itens existentes no banco de dados
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String item = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM));
                Log.i("SAVE ITEM","item.toString()");
                Log.i("SAVE ITEM",item.toString());
                items.add(item); // Adicionar os itens existentes à lista
            } while (cursor.moveToNext());
        }
        cursor.close();

        for (String item : items) {
            dbHelper.saveItem(listId, item); // Salvar cada item da lista no banco de dados
        }
    }

    private String getItemsText() {
        StringBuilder sb = new StringBuilder();
        for (String item : items) {
            sb.append(item).append("\n");
        }
        return sb.toString();
    }
}
