package br.edu.ifsuldeminas.mch;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DisplayListActivity extends AppCompatActivity implements View.OnCreateContextMenuListener {

    private ListView listView;
    private List<String> listas;
    private ArrayAdapter<String> adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);

        listView = findViewById(R.id.shopping_list);
        listas = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listas);

        listView.setAdapter(adapter);
        listView.setOnCreateContextMenuListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtém o título da lista selecionada
                String listaSelecionada = listas.get(position);

//                Recupera o ID da lista selecionada do banco de dados
                int listaId = dbHelper.getListIdByTitle(listaSelecionada);
//
//                // Inicia a ListDetailActivity e passa o ID da lista como extra
//                Intent intent = new Intent(DisplayListActivity.this, ListDetailActivity.class);
//                intent.putExtra("listId", listaId);
//                startActivity(intent);
                    Log.i("AQUI",adapter.getItem(0));
            }
        });

        dbHelper = new DatabaseHelper(this);
        loadListsFromDatabase();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayListActivity.this, EditListActivity.class);
                startActivityForResult(intent, 1); // Inicia a EditListActivity com um código de solicitação
            }
        });
    }

    // Método para adicionar uma nova lista à ListView
    public void adicionarLista(String nomeLista) {
        listas.add(nomeLista);
        adapter.notifyDataSetChanged();
    }

    // Método para carregar as listas salvas no banco de dados
    private void loadListsFromDatabase() {
        listas.clear(); // Limpa a lista atual antes de carregar as listas do banco de dados

        Cursor cursor = dbHelper.getAllLists();
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                adicionarLista(title);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // Verifica se o resultado é da EditListActivity
            if (resultCode == RESULT_OK) { // Verifica se a operação foi concluída com sucesso
                loadListsFromDatabase(); // Atualizar a lista após a criação de uma nova lista
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        String selectedList = listas.get(position);

        menu.setHeaderTitle(selectedList);
        menu.add(0, position, 0, "Excluir");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = item.getItemId();
        String selectedList = listas.get(position);
        deleteList(selectedList); // Excluir a lista selecionada
        return true;
    }

    public void deleteList(String title) {
        dbHelper.deleteList(title);
        Toast.makeText(this, "Lista excluída: " + title, Toast.LENGTH_SHORT).show();
        loadListsFromDatabase(); // Atualizar a lista após a exclusão
    }
}
