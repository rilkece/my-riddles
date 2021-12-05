package novasclasses;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cria.sphinxroom.EditRiddle;
import com.cria.sphinxroom.R;
import com.cria.sphinxroom.RiddleActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder>{
    Context context;
    ArrayList<MyRiddleProfile> myRiddleProfiles;

    public GameAdapter(Context c, ArrayList<MyRiddleProfile> p){
        context = c;
        myRiddleProfiles = p;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GameViewHolder(LayoutInflater.from(context).inflate(R.layout.card_riddles_added, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, final int position) {
        //setar cardview
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = "not";
                Integer pTotais = Integer.parseInt(myRiddleProfiles.get(position).getFases());
                Integer pCompleted = Integer.parseInt(myRiddleProfiles.get(position).getCompleted());
                if (pTotais.equals(pCompleted)){
                    status = "yes";
                }
                Intent intent = new Intent(context, RiddleActivity.class);
                intent.putExtra("Name", myRiddleProfiles.get(position).getName());
                intent.putExtra("Id", myRiddleProfiles.get(position).getId());
                intent.putExtra("Fases", myRiddleProfiles.get(position).getFases());
                intent.putExtra("Desc", myRiddleProfiles.get(position).getDesc());
                intent.putExtra("Date", myRiddleProfiles.get(position).getDate());
                intent.putExtra("Completed", myRiddleProfiles.get(position).getCompleted());
                intent.putExtra("Author", myRiddleProfiles.get(position).getAuthor());
                intent.putExtra("Added", myRiddleProfiles.get(position).getAdded());
                intent.putExtra("Finalized", status);
                context.startActivity(intent);
                //Toast.makeText(context, myRiddleProfiles.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
        });
        //setar img
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Games/"+"Covers/").child(myRiddleProfiles.get(position).getId());
        Glide.with(context)
                .load(storageReference)
                .into(holder.profilePic);

        //Setar game name
        holder.txtGameName.setText(myRiddleProfiles.get(position).getName());
        //Setar games Phases
        String totalFases = myRiddleProfiles.get(position).getCompleted()+"/"+myRiddleProfiles.get(position).getFases();
        holder.txtFases.setText(totalFases); ;
        //Setar games Code
        String code = context.getResources().getString(R.string.code_riddle)+": "+myRiddleProfiles.get(position).getId();
        holder.txtCode.setText(code);
        //setar card color if completed
        Integer pTotais = Integer.parseInt(myRiddleProfiles.get(position).getFases());
        Integer pCompleted = Integer.parseInt(myRiddleProfiles.get(position).getCompleted());
        if (pTotais.equals(pCompleted)){
            holder.cardView.setBackgroundTintList(context.getResources().getColorStateList(R.color.green_200));
        }
    }


    @Override
    public int getItemCount() {
        return myRiddleProfiles.size();
    }

    class GameViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        CircularImageView profilePic;
        TextView txtGameName;
        TextView txtFases ;
        TextView txtCode;

        public GameViewHolder(@NonNull View itemView){
            super(itemView);
            cardView = itemView.findViewById(R.id.cardRiddlesAdded);
            profilePic = itemView.findViewById(R.id.myRiddleCoverCardRiddlesAdded);
            txtGameName = itemView.findViewById(R.id.txtGameNameCardRiddlesAdded);
            txtFases = itemView.findViewById(R.id.txtGamePhasesCardRiddlesAdded);
            txtCode = itemView.findViewById(R.id.txtCodeCardRiddlesAdded);
        }
    }

}
