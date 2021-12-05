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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyRiddleAdapter extends RecyclerView.Adapter<MyRiddleAdapter.MyViewHolder> {

    Context context;
    ArrayList<MyRiddleProfile> myRiddleProfiles;

    public MyRiddleAdapter(Context c, ArrayList<MyRiddleProfile> p){
        context = c;
        myRiddleProfiles = p;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_my_riddles, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyRiddleAdapter.MyViewHolder holder, final int position) {
        //setar cardview
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditRiddle.class);
                intent.putExtra("Name", myRiddleProfiles.get(position).getName());
                intent.putExtra("Id", myRiddleProfiles.get(position).getId());
                intent.putExtra("Fases", myRiddleProfiles.get(position).getFases());
                intent.putExtra("Desc", myRiddleProfiles.get(position).getDesc());
                intent.putExtra("Date", myRiddleProfiles.get(position).getDate());
                intent.putExtra("Completed", myRiddleProfiles.get(position).getCompleted());
                intent.putExtra("Author", myRiddleProfiles.get(position).getAuthor());
                intent.putExtra("Added", myRiddleProfiles.get(position).getAdded());
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
        //Setar games Added
        holder.txtAdded.setText(myRiddleProfiles.get(position).getAdded());
        //Setar games Completed
        holder.txtCompleted.setText(myRiddleProfiles.get(position).getCompleted()); ;
        //Setar games Created Date
        holder.txtCreated.setText(DateFormat.format("dd/MM/yyyy", Long.parseLong(myRiddleProfiles.get(position).getDate()))); ;
        //Setar games Phases
        holder.txtFases.setText(myRiddleProfiles.get(position).getFases()); ;
        //Setar games Code
        String code = context.getResources().getString(R.string.code_riddle)+": "+myRiddleProfiles.get(position).getId();
        holder.txtCode.setText(code);
    }

    @Override
    public int getItemCount() {
        return myRiddleProfiles.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        CircularImageView profilePic;
        TextView txtGameName;
        TextView txtAdded ;
        TextView txtCompleted ;
        TextView txtCreated ;
        TextView txtFases ;
        TextView txtCode;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            cardView = itemView.findViewById(R.id.cardMyRiddles);
            profilePic = itemView.findViewById(R.id.myRiddleCoverCardMyRiddles);
            txtGameName = itemView.findViewById(R.id.txtGameNameCardMyRiddles);
            txtAdded = itemView.findViewById(R.id.txtAddedCardMyRiddles);
            txtCompleted = itemView.findViewById(R.id.txtCompletedCardMyRiddles);
            txtCreated = itemView.findViewById(R.id.txtDateCardMyRiddles);
            txtFases = itemView.findViewById(R.id.txtFasesCardMyRiddles);
            txtCode = itemView.findViewById(R.id.txtCodeCardMyRiddles);
        }
}
}
