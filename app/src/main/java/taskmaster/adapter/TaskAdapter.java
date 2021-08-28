package taskmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taskmaster.app.R;

import amplifyframework.datastore.generated.model.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final List<Task> teamItems;
    private final OnTaskItemClickListener listener;


    public TaskAdapter(List<Task> teamItems , OnTaskItemClickListener listener) {
        this.teamItems = teamItems;
        this.listener = listener;
    }

    public interface OnTaskItemClickListener{
        void OnItemClicked(int position);
        void OnDeleteItem(int position);

    }


    @NonNull
    @Override
    public ViewHolder OnCreateViewHolder(@NonNull ViewGroup parent , int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout , parent , false);
        return new ViewHolder(view , listener);
    }

    @Override
    public void OnBindViewHolder(@NonNull TaskAdapter.ViewHolder holder , int position){
        Task item = taskItems.get(position);
        holder.title.setText(item.getTitle());
        holder.body.setText(item.getDescription());
        holder.status.setText(item.getStatus());
    }

    @Override
    public int getItemCount(){
        return teamItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView title;
        private final TextView body;
        private  final TextView status;

        ViewHolder(@NonNull View itemView , onTaskItemClickListener listener){
            super(itemView);
            title = itemView.findViewById(R.id.title_label);
            body = itemView.findViewById(R.id.body_label);
            status = itemView.findViewById(R.id.status_label);
            TextView delete = itemView.findViewById(R.id.delete);


            itemView.setOnClickListener(v -> listener.OnItemClicked(getBindingAdapterPosision()));

            delete.setOnClickListener(v -> listener.OnDeleteItem(getBindingAdapterPosision()));
        }
    }

}
