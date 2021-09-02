package com.example.pibalstametbwi;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Angin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Angin extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText anginInput;
    private Button enter;
    private Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public AnginListener anginListener;

    public interface AnginListener {
        public void sendInput (int besarAngin);
    }
    public Angin() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Angin newInstance() {
        Angin fragment = new Angin();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            anginListener = (AnginListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() +
                    "Implement AnginFragment Listener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_angin, container, false);
        anginInput = view.findViewById(R.id.angin);
        enter = view.findViewById(R.id.enterAngin);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int input = Integer.parseInt(anginInput.getText().toString());
                    anginListener.sendInput(input);
                    getDialog().dismiss();
                }
                catch (NumberFormatException e){
                    Toast.makeText(context, "Harap Masukan dddff", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}