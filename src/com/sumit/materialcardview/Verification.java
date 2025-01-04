package com.sumit.materialcardview;

import androidx.cardview.widget.CardView;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.Notifier;
import com.google.appinventor.components.runtime.ReplForm;

public class Verification {

    static boolean verifyListViewComponent(Form form) {
        if (form instanceof ReplForm)
            return true;
        else {
            try {
                Class.forName("androidx.cardview.widget.CardView").getName();
                return true;
            } catch (ClassNotFoundException e) {
                // showing warning
                Notifier.oneButtonAlert(form,
                        "This extension requires card view library to work which is present in AI2 ListView component, please drag one list view component in your project so that card view library could be compiled in your app",
                        "ListView Missing",
                        "Ok");
                return false;
            }
        }
    }
}
