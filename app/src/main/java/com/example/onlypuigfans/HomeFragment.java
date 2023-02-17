package com.example.onlypuigfans;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment {
    NavController navController;   // <-----------------
    public AppViewModel appViewModel;
    private Parcelable recyclerViewState;
    RecyclerView postsRecyclerView;
    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

// Evita volver atras

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                //  Handle the back button even
                // Aqui podemos configurar el comprotamiento del boton back
                Log.d("BACKBUTTON", "Back button clicks");
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
//

    }
    //    //Codigo que activa El drawer desplegable Pero con Un Main(guarrete)
    private MainActivity main;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        main = (MainActivity) activity;
        main.unlockDrawer();

    }

    /*
    //Codigo que activa El drawer desplegable
    private MyInterface myInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            myInterface = (MyInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement MyInterface");
        }
    }
//

     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the RecyclerView in the layout
        RecyclerView recyclerView = view.findViewById(R.id.postsRecyclerView);

        // Set the layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //myInterface.unlockDrawer();
        return view;

    }

    //
  /*  @Override
    public void onDestroyView() {
        super.onDestroyView();
      //  myInterface.lockDrawer();
        main.lockDrawer();

    }


   */



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Restaurar el estado del RecyclerView
        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable("recycler_state");
            try {

                postsRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }
            catch ( NullPointerException n ){
                System.out.println("error");
            }
        }
        view.findViewById(R.id.gotoNewPostFragmentButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.newPostFragment);
            }
        });

        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        Query query = FirebaseFirestore.getInstance().collection("posts").orderBy("ordenadaDateTime", Query.Direction.DESCENDING).limit(50);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .setLifecycleOwner(this)
                .build();

        postsRecyclerView.setAdapter(new PostsAdapter(options));

        appViewModel = new
                ViewModelProvider(requireActivity()).get(AppViewModel.class);


    }


    class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.PostViewHolder> {
        public PostsAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
            super(options);
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_post, parent, false));
        }

        @Override
        protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull final Post post) {
            Glide.with(getContext()).load(post.authorPhotoUrl).circleCrop().into(holder.authorPhotoImageView);
            holder.authorTextView.setText(post.author);
            holder.dateTimeTextView.setText(post.dateTimePost);

            holder.contentTextView.setText(post.content);
            holder.deleteImageView.setVisibility(View.GONE);

         //   holder.contentTextView.setText(post.dateTimePost);
            // Gestion de likes
            final String postKey = getSnapshots().getSnapshot(position).getId(); final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(post.likes.containsKey(uid)) holder.likeImageView.setImageResource(R.drawable.like_on);
            else holder.likeImageView.setImageResource(R.drawable.like_off);
            holder.numLikesTextView.setText(String.valueOf(post.likes.size()));
            holder.likeImageView.setOnClickListener(view -> { FirebaseFirestore.getInstance().collection("posts")
                    .document(postKey)
                    .update("likes."+uid, post.likes.containsKey(uid) ? FieldValue.delete() : true);
            });
/*
            //Gestion de borrados
            if(!post.uid.equals(uid)) {
                holder.deleteImageView.setVisibility(View.GONE);
            }
            else

            holder.deleteImageView.setOnClickListener(view -> { FirebaseFirestore.getInstance().collection("posts")
                    .document(postKey)
                    .delete();
            });


 */



            // Miniatura de media
            if (post.mediaUrl != null) {
                holder.mediaImageView.setVisibility(View.VISIBLE);
                if ("audio".equals(post.mediaType)) {
                    Glide.with(requireView()).load(R.drawable.audio).centerCrop().into(holder.mediaImageView);
                } else {
                    Glide.with(requireView()).load(post.mediaUrl).centerCrop().into(holder.mediaImageView);
                }
                holder.mediaImageView.setOnClickListener(view -> { appViewModel.postSeleccionado.setValue(post); navController.navigate(R.id.mediaFragment);
                });
            } else { holder.mediaImageView.setVisibility(View.GONE);
            }

        }

        class PostViewHolder extends RecyclerView.ViewHolder {
                ImageView authorPhotoImageView, likeImageView,deleteImageView,mediaImageView;
                TextView authorTextView, dateTimeTextView, contentTextView, numLikesTextView;

                PostViewHolder(@NonNull View itemView) {
                    super(itemView);
                    authorPhotoImageView =
                            itemView.findViewById(R.id.photoImageView);
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
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerViewState = postsRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("recycler_state", recyclerViewState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (postsRecyclerView != null && postsRecyclerView.getLayoutManager() != null) {
            postsRecyclerView.getLayoutManager().removeAllViews(); // Limpia el RecyclerView
        }
    }
}

