package dsardy.in.winktodo;

import java.io.Serializable;

/**
 * Created by Shubham on 1/19/2017.
 */

public class Task implements Serializable {

    String title;
    String time;

    public Task(String title,String time){
        this.time=time;
        this.title=title;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

}
