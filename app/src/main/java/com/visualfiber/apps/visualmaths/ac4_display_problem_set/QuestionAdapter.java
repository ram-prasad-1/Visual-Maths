package com.visualfiber.apps.visualmaths.ac4_display_problem_set;


import android.content.Context;

import com.visualfiber.apps.visualmaths.circle.cv.C10_12_e6;
import com.visualfiber.apps.visualmaths.fraction.EquationViewTest;
import com.visualfiber.apps.visualmaths.model.ProblemSet;
import com.visualfiber.apps.visualmaths.model.RequestCvPipe;
import com.visualfiber.apps.visualmaths.percent.cv.Percent01;
import com.visualfiber.apps.visualmaths.temp.CustomView3;

public class QuestionAdapter extends zBaseQuestionAdapter {


    public QuestionAdapter(Context context, ProblemSet problemSet) {
        super(context, problemSet);
    }

    @Override
    protected RequestCvPipe getCircleCV(int pSetNo, int qNo) {


        switch (pSetNo){

            case 1:

                if (qNo == 1) return new C10_12_e6(context);
                if (qNo == 2) return new CustomView3(context);

            case 2:

                if (qNo == 1) return new C10_12_e6(context);
                if (qNo == 2) return new C10_12_e6(context);


        }

        return null;
    }

    @Override
    protected RequestCvPipe getRectangleCV(int pSetNo, int qNo) {
        return null;
    }

    @Override
    protected RequestCvPipe getTriangleCV(int problemSetNo, int questionNo) {
        return null;
    }

    @Override
    protected RequestCvPipe getPercentCV(int pSetNo, int qNo) {


        switch (pSetNo){

            case 1:

                if (qNo == 1) return new EquationViewTest(context);

//                if (qNo == 1) return new Percent02(context);
                if (qNo == 2) return new Percent01(context);

            case 2:

                if (qNo == 1) return new C10_12_e6(context);
                if (qNo == 2) return new C10_12_e6(context);

        }


        return null;
    }


}
