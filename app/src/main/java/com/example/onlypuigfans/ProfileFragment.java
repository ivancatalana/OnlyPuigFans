package com.example.onlypuigfans;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFragment extends Fragment {
    String photoOK=null;
    ImageView postPhotoImageView;
    MainActivity mainActivity;
    public AppViewModel appViewModel;
    String uidPostProfile;
    TextView contadorPosts;
    int numberOfPosts;
    ImageView photoImageView;
    TextView displayNameTextView, emailTextView;
    NavController navController;
    private Button editarFoto;
    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        navController = Navigation.findNavController(view);

        RecyclerView postsRecyclerView = view.findViewById(R.id.postsRecyclerView);

        Query query = FirebaseFirestore.getInstance().collection("posts").whereEqualTo("uid",uid).orderBy("ordenadaDateTime", Query.Direction.DESCENDING).limit(50);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .setLifecycleOwner(this)
                .build();

        postsRecyclerView.setAdapter(new ProfileFragment.PostsAdapter(options));

        appViewModel = new
                ViewModelProvider(requireActivity()).get(AppViewModel.class);


        photoImageView = view.findViewById(R.id.photoImageView);
        displayNameTextView = view.findViewById(R.id.displayNameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        editarFoto =  view.findViewById(R.id.button2);
        contadorPosts= view.findViewById(R.id.contador2);
        // Boton Floating Listener

        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.editProfilePage);

            }
        });




/*
        editarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.profilePictureFragment);

            }
        });



 */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            if (user.getDisplayName()==null){
                displayNameTextView.setText("Imagen No Disponible");
                emailTextView.setText(user.getEmail());

            }
            else {
                displayNameTextView.setText(user.getDisplayName());
                emailTextView.setText(user.getEmail());
            }
            if(user.getPhotoUrl()==null){
                Glide.with(this)
                        .load(getResources().getDrawable(R.drawable.profile))
                        .circleCrop()
                        .into(photoImageView);
            }
              else{
                Glide.with(requireView()).load(user.getPhotoUrl()).circleCrop().into(photoImageView);
            }
        }
        FirebaseFirestore.getInstance()
                .collection("posts")
                .whereEqualTo("uid", uid)
                .orderBy("ordenadaDateTime", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        System.out.println("-------------------------------querySnapshot.size()     -- "  + querySnapshot.size());
                        numberOfPosts = (int)querySnapshot.size();

                    }
                });
        navController = Navigation.findNavController(view);

        // Consulta con indice compuesto que obliga a estar  habilitada en firebase

        ProfileFragment.PostsAdapter adapter = new ProfileFragment.PostsAdapter(options);
        postsRecyclerView.setAdapter(adapter);
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        final Handler handler = new Handler(Looper.getMainLooper());

        //Temporizador para actualizar la variable antes de mostrarla (Si no se muestra a 0)

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 3000ms
                contadorPosts.setText(""+numberOfPosts);

            }
        }, 200);

    }
    class PostsAdapter extends FirestoreRecyclerAdapter<Post, ProfileFragment.PostsAdapter.PostViewHolder> {
        // Metodo para contar los posts
        @Override
        public int getItemCount() {
            return super.getItemCount(); // Retorna el número de items del adaptador
        }

        public PostsAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
            super(options);
        }
        @NonNull
        @Override
        public ProfileFragment.PostsAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ProfileFragment.PostsAdapter.PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_post, parent, false));
        }


        @Override

        protected void onBindViewHolder(@NonNull ProfileFragment.PostsAdapter.PostViewHolder holder, int position, @NonNull final Post post) {


            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //FirebaseAuth.getInstance();
            System.out.println("------------"+post.authorPhotoUrl);

            if(post.authorPhotoUrl.contains("http")||post.authorPhotoUrl.contains("content")){
                photoOK= post.authorPhotoUrl;

            }
           // if (photoOK==null)
                Glide.with(requireView()).load(getResources().getDrawable(R.drawable.image10)).circleCrop().into(holder.authorPhotoImageView);
           // else
              //  Glide.with(getContext()).load(photoImageView).circleCrop().into(postPhotoImageView);


            if(post.authorPhotoUrl!=null){
                Glide.with(getContext()).load(post.authorPhotoUrl).circleCrop().into(holder.authorPhotoImageView);
            }
            holder.authorTextView.setText(post.author);
            holder.dateTimeTextView.setText(post.dateTimePost);

            holder.contentTextView.setText(post.content);

            //   holder.contentTextView.setText(post.dateTimePost);
            // Gestion de likes
            final String postKey = getSnapshots().getSnapshot(position).getId();
            if (post.likes.containsKey(uid))
                holder.likeImageView.setImageResource(R.drawable.like_on);
            else holder.likeImageView.setImageResource(R.drawable.like_off);
            holder.numLikesTextView.setText(String.valueOf(post.likes.size()));
            holder.likeImageView.setOnClickListener(view -> {
                FirebaseFirestore.getInstance().collection("posts")
                        .document(postKey)
                        .update("likes." + uid, post.likes.containsKey(uid) ? FieldValue.delete() : true);
            });

            //Gestion de borrados
            holder.deleteImageView.setOnClickListener(view -> {
                FirebaseFirestore.getInstance().collection("posts")
                        .document(postKey)
                        .delete();
            });



            // Miniatura de media
            if (post.mediaUrl != null) {
                holder.mediaImageView.setVisibility(View.VISIBLE);
                if ("audio".equals(post.mediaType)) {
                    Glide.with(requireView()).load(R.drawable.audio).centerCrop().into(holder.mediaImageView);
                } else {
                    Glide.with(requireView()).load(post.mediaUrl).centerCrop().into(holder.mediaImageView);
                }
                holder.mediaImageView.setOnClickListener(view -> {
                    appViewModel.postSeleccionado.setValue(post);
                    navController.navigate(R.id.mediaFragment);
                });
            } else {
                holder.mediaImageView.setVisibility(View.GONE);
            }
        }


        class PostViewHolder extends RecyclerView.ViewHolder {
            ImageView authorPhotoImageView, likeImageView,deleteImageView,mediaImageView;
            TextView authorTextView, dateTimeTextView, contentTextView, numLikesTextView;

            PostViewHolder(@NonNull View itemView) {
                super(itemView);
                //   postPhotoImageView=itemView.findViewById(R.id.photoImageView);
                authorPhotoImageView = itemView.findViewById(R.id.photoImageView);
                likeImageView = itemView.findViewById(R.id.likeImageView);
                mediaImageView = itemView.findViewById(R.id.mediaImage);
                dateTimeTextView =  itemView.findViewById(R.id.dateTimeTextView);
                authorTextView = itemView.findViewById(R.id.authorTextView);
                contentTextView = itemView.findViewById(R.id.contentTextView);
                numLikesTextView = itemView.findViewById(R.id.numLikesTextView);
                deleteImageView = itemView.findViewById(R.id.deleteImageView);

            }
        }
    }

}

