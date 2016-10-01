package com.visualfiber.apps.visualmaths.ac3_list_tools;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.ac4_display_tools.DisplayToolsActivity;
import com.visualfiber.apps.visualmaths.model.Topic;

import java.util.List;


/**
 * 22/05/16.  Top level Adapter
 */
public class ToolRvAdapter extends RecyclerView.Adapter<ToolRvAdapter.TitleViewHolder> {


    private List<String> list;
    private Topic topic;
    Context context;


    // constructor
    public ToolRvAdapter(Context context) {
        this.context = context;
    }

    // set titles (called from main Activity)
    public void setToolList(Topic topic, List<String> topicTitles) {
        list = topicTitles;
        this.topic = topic;
        notifyDataSetChanged();

    }


    // Provide a reference to the views for each data item
    public class TitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // each data item is just a string in this case
        CardView cardView;
        TextView title;


        public TitleViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.problem_text_view);

            cardView.setOnClickListener(this);

        }

        // start category selection activity
        @Override
        public void onClick(View v) {

            // start tool display activity
            DisplayToolsActivity.start(v.getContext(), topic, getAdapterPosition()+1);


        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public TitleViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_problem_list, parent, false);

        // set the view's size, margins, paddings and layout parameters here

        TitleViewHolder vh = new TitleViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TitleViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        holder.title.setText(list.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }
}

