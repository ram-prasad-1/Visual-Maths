package com.visualfiber.apps.visualmaths.ac3_list_problem_set;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.ac4_display_problem_set.DisplayQuestionActivity;
import com.visualfiber.apps.visualmaths.model.Topic;

import java.util.ArrayList;
import java.util.List;


/**
 * 22/05/16.  Adapter for displaying Problem list
 */
public class ProblemRvAdapter extends RecyclerView.Adapter<ProblemRvAdapter.TitleViewHolder> {


    private List<String> list;
    Context context;


    public Topic topic = null;

    public ProblemRvAdapter(Context context) {
        this.context = context;
    }

    // call from the activity
    public void setProblemSetList(Topic topic) {
        this.topic = topic;

        list = generateListData(topic);
        notifyDataSetChanged();

    }


    // NOTE THAT HERE DATA IS GENERATED INTERNALLY
    // SO NO NEED OF A PRESENTER
    private List<String> generateListData(Topic topic) {
        List<String> psList = new ArrayList<>();

        for (int i = 1; i <= topic.getTotalProblemSets(); i++ ){
            psList.add("Problem Set "+ i);

        }

        return psList;

    }



    public class TitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        TextView problemItemTxt;

        public TitleViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            problemItemTxt = (TextView) itemView.findViewById(R.id.problem_text_view);

            cardView.setOnClickListener(this);



        }



        // start display activity
        @Override
        public void onClick(View v) {

            //
            DisplayQuestionActivity.start(v.getContext(), topic, getAdapterPosition()+1);

        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public TitleViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_problem_list, parent, false);


        return new TitleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TitleViewHolder holder, int position) {

        holder.problemItemTxt.setText(list.get(position));

    }



    @Override
    public int getItemCount() {
        return list.size();
    }
}

