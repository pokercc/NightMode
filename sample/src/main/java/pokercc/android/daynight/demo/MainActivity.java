package pokercc.android.daynight.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import pokercc.android.nightmodel.ActivityNightModelHelper;


public class MainActivity extends AppCompatActivity {

    private ActivityNightModelHelper activityNightModelHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityNightModelHelper = new ActivityNightModelHelper(this, R.style.AppTheme_NoActionBar, R.style.AppTheme_NoActionBar_Night);
        activityNightModelHelper.onCreate();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNightModel();
//                setTheme(R.style.AppTheme_NoActionBar_Night);

            }
        });
        Button open = (Button) findViewById(R.id.open);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new SimpleAdapter());


        setTitle("白天模式");

    }


    private void changeNightModel() {
        if (activityNightModelHelper.isNightMode()) {
            activityNightModelHelper.applyDayNight(false);
            setTitle("白天模式");
        } else {
            activityNightModelHelper.applyDayNight(true);
            setTitle("夜间模式");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onShowDialogClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle("今天下雨了吗？")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }


    private static class SimpleAdapter extends BaseAdapter {
        private List<String> list = new ArrayList<>();

        public SimpleAdapter() {
            for (int i = 0; i < 50; i++) {
                list.add("鹅鹅鹅");
                list.add("曲项向天歌");
                list.add("白毛浮绿水");
                list.add("红掌拨清波");
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            }
            ((TextView) convertView).setText(getItem(position));
            return convertView;
        }
    }
}
