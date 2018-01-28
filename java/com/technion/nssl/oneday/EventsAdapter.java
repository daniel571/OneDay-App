package com.technion.nssl.oneday;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.facebook.FacebookSdk.getApplicationContext;

/*The ListView is shown by it's adapter.
  Here we implement how to show each event in the eventlist  */
public class EventsAdapter extends ArrayAdapter<Events> {
    private ArrayList<Events> eventsList;
    private LayoutInflater vi;
    private int Resource;
    private ViewHolder holder;
    private Map registerd_events;
    private Map waitingList_events;
    private boolean IsRegisterd;
    private boolean IsWaitingList;
    private String fbid;
    private Activity A;
    int ActId;
    private boolean[] flag_arr;
    private BitmapDrawable[] image_arr;


    public EventsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Events> objects, Activity _A) {
        super(context, resource, objects);
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        eventsList = objects;
        A = _A;
        registerd_events = ((OneDay)A.getApplication()).registeredEvents;
        waitingList_events = ((OneDay)A.getApplication()).waitingListEvents;
        fbid = ((OneDay)A.getApplication()).getfbID();
        if (A.getApplicationContext() instanceof waiting_vol) {

        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        flag_arr = new boolean[eventsList.size()];
        image_arr = new BitmapDrawable[eventsList.size()];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (!parent.isEnabled()) parent.setEnabled(true);

        IsRegisterd = eventsList.get(position).getRegistered();
        IsWaitingList = eventsList.get(position).isWaitingList();

        Log.d("buttonAdapter","the event list size is "+ eventsList.size());
        Log.d("buttonAdapter","the position we see now is "+position);
        //initialize
        if (v == null) {
            Log.d("buttonAdapter","the position  "+position+" and we are in the null section");
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);


            holder.tvName = (TextView) v.findViewById(R.id.tvName);
            holder.tvDescription = (TextView) v.findViewById(R.id.tvDescription);
            holder.tvStartTime = (TextView) v.findViewById(R.id.tvStartTime);
            holder.tvEndTime = (TextView) v.findViewById(R.id.tvEndTime);
            holder.tvOccupancy = (TextView) v.findViewById(R.id.tvOccupancy);
            holder.tvIsActive = (TextView) v.findViewById(R.id.tvIsActive);
            holder.imageview = (ImageView) v.findViewById(R.id.ivImage);
            holder.tvRegUnReg = (Button) v.findViewById(R.id.tvRegUnReg);
            holder.tvActivityID = eventsList.get(position).getActivityID();
            holder.tvRegUnReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("buttonAdapter2","the button of number "+ position);
                    Log.d("buttonAdapter2","the button of activity "+ eventsList.get(position).getActivityID());
                    if(eventsList.get(position).getRegistered()){
                        //the user decided to cancel his register
                        new EventRegAsyncTask(A).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventUnregister","fb_userID="+fbid+"&eventID="+String.valueOf(eventsList.get(position).getActivityID()));
                        registerd_events.put(eventsList.get(position).getActivityID(),false);
                    }
                    else if(eventsList.get(position).isWaitingList()){
                        // The event cancel his ascendancy from the waiting list
                        new EventRegAsyncTask(A).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventUnregister","fb_userID="+fbid+"&eventID="+String.valueOf(eventsList.get(position).getActivityID()));
                        waitingList_events.put(eventsList.get(position).getActivityID(), false);
                    }
                    else {
                        new EventRegAsyncTask(A).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventRegister","fb_userID="+fbid+"&eventID="+String.valueOf(eventsList.get(position).getActivityID()));
                        StringBuilder mailContent = new StringBuilder();
                        mailContent.append("Hi " +  ((OneDay)A.getApplication()).my_user.getFirstName()+", you have successfully registered to "+eventsList.get(position).getName()+"\n");
                        mailContent.append("Event details: " +eventsList.get(position).getName()+"start at: " +eventsList.get(position).getStartTime() +" end at: "+eventsList.get(position).getEndTime()+"\n");
                        mailContent.append("People who come with a car: " +eventsList.get(position).getRegCarPoolUsers());
                        mailContent.append("See you in the event, OneDay team.");
                        sendEmail(((OneDay)A.getApplication()).my_user.getEmail(), mailContent.toString());
                    }
                }
            });



            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
            holder.tvActivityID = eventsList.get(position).getActivityID();
            Log.d("buttonAdapter2","the position is: "+ position);
            holder.tvRegUnReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("buttonAdapter2","the button of number "+ position);
                    Log.d("buttonAdapter2","the button of activity "+ eventsList.get(position).getActivityID());
                    if(eventsList.get(position).getRegistered()){
                        //the user decided to cancel his register
                        new EventRegAsyncTask(A).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventUnregister","fb_userID="+fbid+"&eventID="+String.valueOf(eventsList.get(position).getActivityID()));
                        registerd_events.put(eventsList.get(position).getActivityID(),false);
                    }
                    else if(eventsList.get(position).isWaitingList()){
                        // The event cancel his ascendancy from the waiting list
                        new EventRegAsyncTask(A).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventUnregister","fb_userID="+fbid+"&eventID="+String.valueOf(eventsList.get(position).getActivityID()));
                        waitingList_events.put(eventsList.get(position).getActivityID(), false);
                    }
                    else {
                        new EventRegAsyncTask(A).execute("http://oneday-test.eu-central-1.elasticbeanstalk.com/api/eventRegister","fb_userID="+fbid+"&eventID="+String.valueOf(eventsList.get(position).getActivityID()));
                        StringBuilder mailContent = new StringBuilder();
                        mailContent.append("Hi " +  ((OneDay)A.getApplication()).my_user.getFirstName()+", you have successfully registered to "+eventsList.get(position).getName()+"\n");
                        mailContent.append("Event details: " +eventsList.get(position).getName()+" start at: " +eventsList.get(position).getStartTime() +"end at: "+eventsList.get(position).getEndTime()+"\n");
                        mailContent.append("People who come with a car: " +eventsList.get(position).getRegCarPoolUsers());
                        mailContent.append("See you in the event, OneDay team.");
                        sendEmail(((OneDay)A.getApplication()).my_user.getEmail(), mailContent.toString());
                    }
                }
            });
            Log.d("buttonAdapter","the position  "+position+" and we are in the NOT null section");
        }
        if (eventsList.get(position).getImage().isEmpty()) {
            holder.imageview.setImageResource(R.drawable.default_photo_event);
        }
        else {
            if (!flag_arr[position]) {
                new DownloadImageTask(holder.imageview, position).execute(eventsList.get(position).getImage());
            }
            else {
                holder.imageview.setImageDrawable(image_arr[position]);
            }
        }

        holder.tvName.setText(eventsList.get(position).getName());
        holder.tvName.setTextColor(Color.BLUE);
        holder.tvDescription.setText(eventsList.get(position).getDescription()); //eventsList.get(position).getDescription());
        holder.tvDescription.setTextColor(Color.BLACK);
        holder.tvStartTime.setText("Start time: " + eventsList.get(position).getStartTime());
        holder.tvStartTime.setTextColor(Color.BLACK);
        holder.tvEndTime.setText("End time: " + eventsList.get(position).getEndTime());
        holder.tvEndTime.setTextColor(Color.BLACK);
        holder.tvOccupancy.setText("Capacity: "+ eventsList.get(position).getCurrentRegistered()+"/"+ eventsList.get(position).getCapacity());
        holder.tvOccupancy.setTextColor(Color.BLACK);
        if (eventsList.get(position).getIsActive().equals("1")) {
            holder.tvIsActive.setText("Active");
            holder.tvIsActive.setTextColor(Color.GREEN);
        }
        else {
            holder.tvIsActive.setText("Not Active");
            holder.tvIsActive.setTextColor(Color.RED);
        }

        if (IsRegisterd) {
            holder.tvRegUnReg.setText("Unregister");
        }
        else if (IsWaitingList){
            holder.tvRegUnReg.setText("Remove from waiting list");
        }
        else  {
            holder.tvRegUnReg.setText("Register");
        }
        Log.i("Carpool", eventsList.get(position).getRegCarPoolUsers());
        return v;

    }

    private void sendEmail(String email, String message) {
        Log.d("debug",((OneDay)A.getApplication()).my_user.getEmail());
        try {
            SendMail sm = new SendMail(email, "OneDay event registration", message);
            //Executing sendmail to send email
            sm.execute();
        }
        catch (NullPointerException ex){
            Log.e("UserError","User is not fully recognize in the server");
        }
        //Creating SendMail object

    }

    static class ViewHolder {
        public int tvActivityID;
        public TextView tvName;
        public TextView tvStartTime;
        public TextView tvEndTime;
        public TextView tvDescription;
        public TextView tvOccupancy;
        public TextView tvIsActive;
        public ImageView imageview;
        public Button tvRegUnReg;

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        int position;

        public DownloadImageTask(ImageView bmImage, int pos) {
            this.bmImage = bmImage;
            position = pos;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            scaleImage(bmImage);
            image_arr[position] = ((BitmapDrawable) bmImage.getDrawable());
            flag_arr[position] = true;
        }

        private void scaleImage(ImageView view) throws NoSuchElementException {
            WindowManager wm = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            // Get bitmap from the the ImageView.
            Bitmap bitmap = null;

            try {
                Drawable drawing = view.getDrawable();
                bitmap = ((BitmapDrawable) drawing).getBitmap();
            } catch (NullPointerException e) {
                throw new NoSuchElementException("No drawable on given view");
            } catch (ClassCastException e) {
                // Check bitmap is Ion drawable
                // bitmap = Ion.with(view).getBitmap();
            }

            // Get current dimensions AND the desired bounding box
            int width = 0;

            try {
                width = bitmap.getWidth();
            } catch (NullPointerException e) {
                throw new NoSuchElementException("Can't find bitmap on given view/drawable");
            }

            int height = bitmap.getHeight();
            int bounding = dpToPx(150);
            Log.i("Test", "original width = " + Integer.toString(width));
            Log.i("Test", "original height = " + Integer.toString(height));
            Log.i("Test", "bounding = " + Integer.toString(bounding));

            // Determine how much to scale: the dimension requiring less scaling is
            // closer to the its side. This way the image always stays inside your
            // bounding box AND either x/y axis touches it.

            float xScale = ((float)size.x-dpToPx(30))/width;
            float yScale = ((float) bounding) / height;
            float scale = (xScale <= yScale) ? xScale : yScale;
            Log.i("Test", "xScale = " + Float.toString(xScale));
            Log.i("Test", "yScale = " + Float.toString(yScale));
            Log.i("Test", "scale = " + Float.toString(scale));

            // Create a matrix for the scaling and add the scaling data
            Matrix matrix = new Matrix();
//            matrix.postScale(scale, scale);
            matrix.postScale(xScale, yScale);

            // Create a new bitmap and convert it to a format understood by the ImageView
            Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            width = scaledBitmap.getWidth(); // re-use
            height = scaledBitmap.getHeight(); // re-use
            BitmapDrawable result = new BitmapDrawable(scaledBitmap);
            Log.i("Test", "scaled width = " + Integer.toString(width));
            Log.i("Test", "scaled height = " + Integer.toString(height));

            // Apply the scaled bitmap
            view.setImageDrawable(result);

            // Now change ImageView's dimensions to match the scaled image
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();

            params.width = width;
            params.height = height;
            view.setLayoutParams(params);


            Log.i("Test", "done");
        }

        private int dpToPx(int dp) {
            float density = getApplicationContext().getResources().getDisplayMetrics().density;
            return Math.round((float)dp * density);
        }


    }


    public class EventRegAsyncTask extends AsyncTask<String,Void,String> {
        private Activity A;
        private StringBuilder mailContent;
        public EventRegAsyncTask(Activity _A) {
            A = _A;
            mailContent = new StringBuilder("Successfully registered!\nEvent details:\n ");
        }

        @Override
        protected String doInBackground(String... params) {
            String result;
            Handle_post_http http_handle = new Handle_post_http();
            result =  http_handle.handle(params[0],params[1]);
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            A.recreate();
        }
    }

}
