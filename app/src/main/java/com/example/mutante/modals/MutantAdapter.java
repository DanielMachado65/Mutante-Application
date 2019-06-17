package com.example.mutante.modals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mutante.R;

import java.util.ArrayList;

public class MutantAdapter extends ArrayAdapter<Mutant> {

    private final Context context;
    private final ArrayList<Mutant> mutants;

    public MutantAdapter(Context context, ArrayList<Mutant> mutants) {
        super(context, R.layout.mutant_linha, mutants);
        this.context = context;
        this.mutants = mutants;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.mutant_linha, parent, false);

        TextView nameMutant = rowView.findViewById(R.id.nameMutantTextView);
        TextView habilityMutant = rowView.findViewById(R.id.habilityMutantTextView);
        ImageView image = rowView.findViewById(R.id.imagemMutant);

        nameMutant.setText(mutants.get(position).getName());
        habilityMutant.setText(mutants.get(position).getHability());
        image.setImageResource(R.drawable.ic_launcher_background);

        return rowView;
    }
}
