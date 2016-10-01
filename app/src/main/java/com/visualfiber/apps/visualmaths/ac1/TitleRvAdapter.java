package com.visualfiber.apps.visualmaths.ac1;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.ac2.ContentTypeActivity;
import com.visualfiber.apps.visualmaths.model.Topic;

import java.util.List;


/**
 * 22/05/16.  Top level Adapter
 */
public class TitleRvAdapter extends RecyclerView.Adapter<TitleRvAdapter.TitleViewHolder> {


    private List<Topic> list;
    Context context;


    // constructor
    public TitleRvAdapter(Context context) {
        this.context = context;
    }

    // set titles (called from main Activity)
    public void setTitles(List<Topic> topicTitles) {
        list = topicTitles;
    }


    // Provide a reference to the views for each data item
    public class TitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // each data item is just a string in this case
        CardView cardView;
        TextView title;
        TextView topicId;

        public TitleViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            topicId = (TextView) itemView.findViewById(R.id.description);

            cardView.setOnClickListener(this);

        }

        // start category selection activity
        @Override
        public void onClick(View v) {

            ContentTypeActivity.start(v.getContext(), list.get(getAdapterPosition()));

        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public TitleViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_main, parent, false);

        // set the view's size, margins, paddings and layout parameters here

        TitleViewHolder vh = new TitleViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TitleViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        holder.title.setText(list.get(position).title);
        holder.topicId.setText(list.get(position).topicId);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }
}

