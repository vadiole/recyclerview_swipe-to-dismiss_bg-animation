package c.vadiole.recyclerview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    View root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.coordinator_layout);
        // data to RecyclerView
        ArrayList<Nabor> nabory = new ArrayList<>();
        nabory.add(new Nabor("Имя 1", 100, 9));
        nabory.add(new Nabor("Имя 2", 200, 4));
        nabory.add(new Nabor("Имя 3", 300, 8));
        nabory.add(new Nabor("Имя 4", 140, 2));
        nabory.add(new Nabor("Имя 5", 155, 7));
        nabory.add(new Nabor("Имя 6", 700, 12));
        nabory.add(new Nabor("Имя 2", 50, 6));
        nabory.add(new Nabor("Имя 9", 150, 23));
        nabory.add(new Nabor("Имя 8", 109, 9));
        nabory.add(new Nabor("Имя 7", 999, 87));
        nabory.add(new Nabor("Имя 6", 100, 3));
        nabory.add(new Nabor("Имя 5", 10, 6));
        nabory.add(new Nabor("Имя 3", 110, 12));
        nabory.add(new Nabor("Имя 2", 111, 444));
        nabory.add(new Nabor("Имя 1", 199, 14));

        recyclerView = findViewById(R.id.recyclerview);

        adapter = new MyRecyclerViewAdapter(this, nabory, root );
        adapter.setClickListener(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Objects.requireNonNull(recyclerView.getItemAnimator()).setAddDuration(0);


        ItemTouchHelper.Callback callback = new SwipeToDeleteCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    private void setUpRecyclerView() {


    }
}

