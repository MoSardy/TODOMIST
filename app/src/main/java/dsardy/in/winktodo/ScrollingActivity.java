package dsardy.in.winktodo;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    public static List<Task> taskList;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    private DataAdapter adapter;
    private RecyclerView recyclerView;
    private AlertDialog.Builder alertDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Paint p = new Paint();
    private static Gson gson;
    private SliderLayout sliderLayout;
    private AdView mAdView;
    LinearLayout linearLayoutrv;

    public static TextView c,t,d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //bannerad
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference();

        DatabaseReference imgmapref = myRef.child("imgmap");

        //set sliderlayout
        sliderLayout = (SliderLayout)findViewById(R.id.slider);

        imgmapref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DefaultSliderView defaultSliderView =new DefaultSliderView(ScrollingActivity.this);
                defaultSliderView.image(dataSnapshot.getValue(String.class)).setScaleType(BaseSliderView.ScaleType.Fit);
                sliderLayout.addSlider(defaultSliderView);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                DefaultSliderView defaultSliderView =new DefaultSliderView(ScrollingActivity.this);
                defaultSliderView.image(dataSnapshot.getValue(String.class)).setScaleType(BaseSliderView.ScaleType.Fit);
                sliderLayout.addSlider(defaultSliderView);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DefaultSliderView defaultSliderView =new DefaultSliderView(ScrollingActivity.this);
        defaultSliderView.image(R.drawable.fbtm1).setScaleType(BaseSliderView.ScaleType.Fit);
        sliderLayout.addSlider(defaultSliderView);

        //set prefs
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        gson = new Gson();

        //get url arrey
       /* HashMap<String,String> url_maps = new HashMap<String, String>();
        url_maps.put("Hannibal", "https://s3-us-west-1.amazonaws.com/powr/defaults/image-slider2.jpg");
        url_maps.put("Big Bang Theory", "https://firebasestorage.googleapis.com/v0/b/winktodo.appspot.com/o/images%2FAdobe_Illustrator_CC_icon.svg.png?alt=media&token=fec33c61-12b4-44d3-a963-3efc1e5a5d8c");
        url_maps.put("House of Cards", "http://www.w3schools.com/css/img_fjords.jpg");
        url_maps.put("Game of Thrones", "https://firebasestorage.googleapis.com/v0/b/winktodo.appspot.com/o/images%2Fandroid-boot-logo_634639.jpg?alt=media&token=5105769e-e5b1-473d-b190-a5072bdfd095");


        //set sliderlayout
        sliderLayout = (SliderLayout)findViewById(R.id.slider);

        for(String s :url_maps.keySet()){
            DefaultSliderView defaultSliderView =new DefaultSliderView(this);
            defaultSliderView.image(url_maps.get(s)).setScaleType(BaseSliderView.ScaleType.Fit);
            sliderLayout.addSlider(defaultSliderView);

        }*/


        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Fade);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(8000);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDialog();
            }
        });

        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.newTask) {

            initDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(){

        recyclerView = (RecyclerView)findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        c= (TextView)findViewById(R.id.textViewc);
        t= (TextView)findViewById(R.id.textViewt);
        d= (TextView)findViewById(R.id.textViewd);



        initSwipe();

    }

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                updatelist();


                //if (direction == ItemTouchHelper.LEFT){
                    adapter.removeItem(position);
                editor.putInt("c",sharedPreferences.getInt("c",0)-1);
                editor.apply();

                updatestats();
                updatelist();




            }


        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private void initDialog(){
        alertDialog = new AlertDialog.Builder(this);
        view = getLayoutInflater().inflate(R.layout.dialog_layout,null);
        alertDialog.setView(view);
        final EditText editText = (EditText)view.findViewById(R.id.editTexttaskinput);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                    String titleinp = editText.getText().toString().trim();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
                    Date now = new Date();
                    String time = format.format(now);

                if(titleinp.isEmpty()||time.isEmpty()){
                    Toast.makeText(ScrollingActivity.this,"Type somthing there",Toast.LENGTH_LONG).show();


                }else{
                    Toast.makeText(ScrollingActivity.this,"Task Added ",Toast.LENGTH_LONG).show();
                    Task task = new Task(titleinp,time);
                    adapter.addItem(task);
                    editor.putInt("t",sharedPreferences.getInt("t",0)+1);
                    editor.putInt("c",sharedPreferences.getInt("c",0)+1);
                    editor.apply();
                    updatestats();
                    updatelist();
                    dialog.dismiss();
                }

                recyclerView.smoothScrollToPosition(0);






            }
        });

        alertDialog.show();
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onStart() {
        super.onStart();

        String tasksjson = sharedPreferences.getString("tasks","");
        Log.e(",,,,,,,,,,,",tasksjson);
        Type type = new TypeToken<List<Task>>(){}.getType();
        taskList = new ArrayList<Task>();
        if(!tasksjson.isEmpty()){
            taskList = gson.fromJson(tasksjson, type);

        }
        adapter = new DataAdapter();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        updatestats();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static  void updatelist(){

        String tasksjson = sharedPreferences.getString("tasks","");
        Log.e(",,,,,,,,,,,",tasksjson);
        Type type = new TypeToken<List<Task>>(){}.getType();
        if(!tasksjson.isEmpty()){
            taskList = gson.fromJson(tasksjson, type);

        }
    }

    public static void updatestats(){
        int ic = sharedPreferences.getInt("c",0);
        int it = sharedPreferences.getInt("t",0);
        int id =sharedPreferences.getInt("d",0);

        c.setText("Current Tasks : "+ic);
        t.setText("Total Tasks : "+it);
        d.setText("Done Tasks : "+id);




    }
}
