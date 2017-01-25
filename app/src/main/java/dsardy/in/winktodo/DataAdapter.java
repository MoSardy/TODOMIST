package dsardy.in.winktodo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uniquestudio.library.CircleCheckBox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static dsardy.in.winktodo.ScrollingActivity.editor;
import static dsardy.in.winktodo.ScrollingActivity.sharedPreferences;
import static dsardy.in.winktodo.ScrollingActivity.taskList;
import static dsardy.in.winktodo.ScrollingActivity.updatelist;
import static dsardy.in.winktodo.ScrollingActivity.updatestats;

/**
 * Created by Shubham on 1/19/2017.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    Gson gson;
    Task t;
    int p ;


    public DataAdapter() {

    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.taskrow, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataAdapter.ViewHolder viewHolder,  int position) {


        p = position;

        t = taskList.get(position);

        //set time
        String agotime = getago(t.time);
        viewHolder.ago.setText(agotime);

        //set title
        viewHolder.textViewTitle.setText(t.getTitle());
    }




    private String getago(String time) {
        String result ="";

        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            Date past = format.parse(time);
            Date now = new Date();
            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if(seconds<60)
            {
                result= seconds+" seconds ago";
            }
            else if(minutes<60)
            {
                result = minutes+" minutes ago";
            }
            else if(hours<24)
            {
                result = hours+" hours ago";
            }
            else
            {
                result = days+" days ago";
            }
        }
        catch (Exception j){
            j.printStackTrace();
        }
        return result;
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void addItem(Task task) {
        taskList.add(0,task);
        notifyItemInserted(0);
        gson = new Gson();


        editor.putString("tasks",gson.toJson(taskList));
        editor.apply();
        updatelist();


    }

    public void removeItem(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, taskList.size());

        gson = new Gson();


        editor.putString("tasks",gson.toJson(taskList));
        editor.apply();
        updatelist();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle, ago , guide ;
        CircleCheckBox c;


        public ViewHolder(View view) {
            super(view);

            textViewTitle = (TextView)view.findViewById(R.id.textViewtitle);
            ago = (TextView)view.findViewById(R.id.textViewAgo);
            guide = (TextView)view.findViewById(R.id.textViewguide);
            c = (CircleCheckBox)view.findViewById(R.id.checkBox);

            c.setListener(new CircleCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(boolean isChecked) {
                    if(isChecked){
                        editor.putInt("d",sharedPreferences.getInt("d",0)+1);
                        editor.apply();
                        updatestats();
                        guide.setVisibility(View.VISIBLE);

                    }else{

                        editor.putInt("d",sharedPreferences.getInt("d",0)-1);
                        editor.apply();
                        updatestats();
                        guide.setVisibility(View.INVISIBLE);

                    }
                }
            });

            /*if(c.isChecked()){
                c.post(new Runnable() {
                    @Override
                    public void run() {
                        c.setChecked(false);
                        guide.setVisibility(View.INVISIBLE);
                    }
                });
            }*/



        }
    }


}
