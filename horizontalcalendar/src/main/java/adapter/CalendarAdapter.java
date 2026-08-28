package adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.horizontalcalendar.R;

import java.util.List;

import model.DayModel;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder>{
    private List<DayModel> localDataSet;
    private OnDayClickListener listener;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private final TextView numberView;

        public ViewHolder(View view){
            super(view);
            textView = (TextView) view.findViewById(R.id.textView);
            numberView = (TextView) view.findViewById(R.id.numberView);
        }

        public TextView getNumberView() {
            return numberView;
        }

        public TextView getTextView() {
            return textView;
        }
    }
    public CalendarAdapter(List<DayModel> dataSet){
        this.localDataSet=dataSet;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.calendar_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        DayModel current_day = localDataSet.get(position);
        Log.d("test","selected:"+position+current_day.isSelected());
        if(current_day.isSelected()){
            viewHolder.itemView.setAlpha(1.0F);
        }else{
            viewHolder.itemView.setAlpha(0.3F);
        }

        viewHolder.getTextView().setText(current_day.getDayOfWeekName());
        viewHolder.getNumberView().setText(String.valueOf(current_day.getDayOfMonth()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onClick(position,current_day);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void setOnClickListener(OnDayClickListener listener){
        this.listener = listener;
    }
    public interface OnDayClickListener{
        public void onClick(int position,DayModel day);
    }
}
