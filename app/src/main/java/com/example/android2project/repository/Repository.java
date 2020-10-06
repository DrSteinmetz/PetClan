package com.example.android2project.repository;

import android.content.Context;
import android.location.Address;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.example.android2project.model.Advertisement;
import com.example.android2project.model.ChatMessage;
import com.example.android2project.model.Comment;
import com.example.android2project.model.Conversation;
import com.example.android2project.model.LocationUtils;
import com.example.android2project.model.Pet;
import com.example.android2project.model.Post;
import com.example.android2project.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Repository {

    private FirebaseAuth mAuth;

    private Context mContext;

    private static Repository repository;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDBChats;

    private FirebaseFirestore mCloudDB = FirebaseFirestore.getInstance();
    private CollectionReference mCloudUsers = mCloudDB.collection("users");
    private CollectionReference mCloudAds = mCloudDB.collection("advertisements");

    private final String MESSAGES = "Messages";
    private final String CONVERSATION = "Conversation";
    private final String POSTS = "posts";
    private final String COMMENTS = "comments";

    private final String TAG = "Repository";


    /**<-------Posts Interfaces------->**/
    /**<-------Post Downloading interface------->**/
    private MutableLiveData<List<Post>> mRepositoryPostDownloadSucceedMLD;
    private MutableLiveData<String> mRepositoryPostDownloadFailedMLD;

    public MutableLiveData<List<Post>> getRepositoryPostDownloadSucceedMLD() {
        if (mRepositoryPostDownloadSucceedMLD == null) {
            mRepositoryPostDownloadSucceedMLD = new MutableLiveData<>();
        }
        return mRepositoryPostDownloadSucceedMLD;
    }

    public MutableLiveData<String> getRepositoryPostDownloadFailedMLD() {
        if (mRepositoryPostDownloadFailedMLD == null) {
            mRepositoryPostDownloadFailedMLD = new MutableLiveData<>();
        }
        return mRepositoryPostDownloadFailedMLD;
    }

    /**<-------User's Posts Downloading interface------->**/

    private MutableLiveData<List<Post>> mRepositoryUserPostDownloadSucceedMLD;
    private MutableLiveData<String> mRepositoryUserPostDownloadFailedMLD;

    public MutableLiveData<List<Post>> getRepositoryUserPostDownloadSucceedMLD() {
        if (mRepositoryUserPostDownloadSucceedMLD == null) {
            mRepositoryUserPostDownloadSucceedMLD = new MutableLiveData<>();
        }
        return mRepositoryUserPostDownloadSucceedMLD;
    }

    public MutableLiveData<String> getRepositoryUserPostDownloadFailedMLD() {
        if (mRepositoryUserPostDownloadFailedMLD == null) {
            mRepositoryUserPostDownloadFailedMLD = new MutableLiveData<>();
        }
        return mRepositoryUserPostDownloadFailedMLD;
    }


    /**<-------Post Uploading interface------->**/

    private MutableLiveData<Post> mRepositoryPostUploadSucceedMLD;
    private MutableLiveData<String> mRepositoryPostUploadFailedMLD;

    public MutableLiveData<Post> getRepositoryPostUploadSucceedMLD() {
        if (mRepositoryPostUploadSucceedMLD == null) {
            mRepositoryPostUploadSucceedMLD = new MutableLiveData<>();
        }
        return mRepositoryPostUploadSucceedMLD;
    }

    public MutableLiveData<String> getRepositoryPostUploadFailedMLD() {
        if (mRepositoryPostUploadFailedMLD == null) {
            mRepositoryPostUploadFailedMLD = new MutableLiveData<>();
        }
        return mRepositoryPostUploadFailedMLD;
    }

    /**<-------Post Updating interface------->**/

    private MutableLiveData<Post> mRepositoryPostUpdateSucceedMLD;
    private MutableLiveData<String> mRepositoryPostUpdateFailedMLD;

    public MutableLiveData<Post> getRepositoryPostUpdateSucceedMLD() {
        if (mRepositoryPostUpdateSucceedMLD == null) {
            mRepositoryPostUpdateSucceedMLD = new MutableLiveData<>();
        }
        return mRepositoryPostUpdateSucceedMLD;
    }

    public MutableLiveData<String> getRepositoryPostUpdateFailedMLD() {
        if (mRepositoryPostUpdateFailedMLD == null) {
            mRepositoryPostUpdateFailedMLD = new MutableLiveData<>();
        }
        return mRepositoryPostUpdateFailedMLD;
    }

    /**<-------Post Likes Updating interface------->**/

    private MutableLiveData<Post> mRepositoryPostLikesUpdateSucceedMLD;
    private MutableLiveData<String> mRepositoryPostLikesUpdateFailedMLD;

    public MutableLiveData<Post> getRepositoryPostLikesUpdateSucceedMLD() {
        if (mRepositoryPostLikesUpdateSucceedMLD == null) {
            mRepositoryPostLikesUpdateSucceedMLD = new MutableLiveData<>();
        }
        return mRepositoryPostLikesUpdateSucceedMLD;
    }

    public MutableLiveData<String> getRepositoryPostLikesUpdateFailedMLD() {
        if (mRepositoryPostLikesUpdateFailedMLD == null) {
            mRepositoryPostLikesUpdateFailedMLD = new MutableLiveData<>();
        }
        return mRepositoryPostLikesUpdateFailedMLD;
    }

    /**<-------Post Deleting interface------->**/

    private MutableLiveData<String> mRepositoryPostDeletionSucceedMLD;
    private MutableLiveData<String> mRepositoryPostDeletionFailedMLD;

    public MutableLiveData<String> getRepositoryPostDeletionSucceedMLD() {
        if (mRepositoryPostDeletionSucceedMLD == null) {
            mRepositoryPostDeletionSucceedMLD = new MutableLiveData<>();
        }
        return mRepositoryPostDeletionSucceedMLD;
    }

    public MutableLiveData<String> getRepositoryPostDeletionFailedMLD() {
        if (mRepositoryPostDeletionFailedMLD == null) {
            mRepositoryPostDeletionFailedMLD = new MutableLiveData<>();
        }
        return mRepositoryPostDeletionFailedMLD;
    }


    /**<-------Comments Interfaces------->**/
    /**<-------Comment Downloading interface------->**/
    public interface RepositoryCommentDownloadInterface {
        void onCommentDownloadSucceed(List<Comment> comments);

        void onCommentDownloadFailed(String error);
    }

    private RepositoryCommentDownloadInterface mCommentDownloadListener;

    public void setCommentDownloadListener(RepositoryCommentDownloadInterface repositoryCommentDownloadInterface) {
        this.mCommentDownloadListener = repositoryCommentDownloadInterface;
    }

    /**<-------Comment Uploading interface------->**/
    public interface RepositoryCommentUploadInterface {
        void onCommentUploadSucceed(Comment comment);

        void onCommentUploadFailed(String error);
    }

    private RepositoryCommentUploadInterface mCommentUploadListener;

    public void setCommentUploadListener(RepositoryCommentUploadInterface repositoryCommentUploadInterface) {
        this.mCommentUploadListener = repositoryCommentUploadInterface;
    }

    /**<-------Comment Updating interface------->**/
    public interface RepositoryCommentUpdatingInterface {
        void onCommentUpdatingSucceed(String updatedCommentContent);

        void onCommentUpdatingFailed(String error);
    }

    private RepositoryCommentUpdatingInterface mCommentUpdatingListener;

    public void setCommentUpdatingListener(RepositoryCommentUpdatingInterface repositoryCommentUpdatingInterface) {
        this.mCommentUpdatingListener = repositoryCommentUpdatingInterface;
    }

    /**<-------Comment Deleting interface------->**/
    public interface RepositoryCommentDeletingInterface {
        void onCommentDeletingSucceed(String commentId);

        void onCommentDeletingFailed(String error);
    }

    private RepositoryCommentDeletingInterface mCommentDeletingListener;

    public void setCommentDeletingListener(RepositoryCommentDeletingInterface repositoryCommentDeletingInterface) {
        this.mCommentDeletingListener = repositoryCommentDeletingInterface;
    }

    /**<-------Settings Interfaces------->**/
    /**<-------Download User interface------->**/
    public interface RepositoryDownloadUserInterface {
        void onDownloadUserSucceed(User user);

        void onDownloadUserFailed(String error);
    }

    private RepositoryDownloadUserInterface mDownloadUserListener;

    public void setDownloadUserListener(RepositoryDownloadUserInterface repositoryDownloadUserInterface) {
        this.mDownloadUserListener = repositoryDownloadUserInterface;
    }

    /**<-------Update User Name interface------->**/
    public interface RepositoryUpdateUserNameInterface {
        void onUpdateUserNameSucceed(String newUserName);

        void onUpdateUserNameFailed(String error);
    }

    private RepositoryUpdateUserNameInterface mUpdateUserNameListener;

    public void setUpdateUserNameListener(RepositoryUpdateUserNameInterface repositoryUpdateUserNameInterface) {
        this.mUpdateUserNameListener = repositoryUpdateUserNameInterface;
    }

    /**<-------Update User Location interface------->**/
    public interface RepositoryUpdateUserLocationInterface {
        void onUpdateUserLocationSucceed(Address address);

        void onUpdateUserLocationFailed(String error);
    }

    private RepositoryUpdateUserLocationInterface mUpdateUserLocationListener;

    public void setUpdateUserLocationListener(RepositoryUpdateUserLocationInterface repositoryUpdateUserLocationInterface) {
        this.mUpdateUserLocationListener = repositoryUpdateUserLocationInterface;
    }


    /**<-------Update User Image interface------->**/
    public interface RepositoryUpdateUserImageInterface {
        void onUpdateUserImageSucceed(String newUserProfilePic);

        void onUpdateUserImageFailed(String error);
    }

    private RepositoryUpdateUserImageInterface mUpdateUserImageListener;

    public void setUpdateUserImageListener(RepositoryUpdateUserImageInterface repositoryUpdateUserImageInterface) {
        this.mUpdateUserImageListener = repositoryUpdateUserImageInterface;
    }

    /**<-------User Deletion interface------->**/
    public interface RepositoryUserDeletionInterface {
        void onUserDeletionSucceed(String userId);

        void onUserDeletionFailed(String error);
    }

    private RepositoryUserDeletionInterface mUserDeletionListener;

    public void setUserDeletionListener(RepositoryUserDeletionInterface repositoryUserDeletionInterface) {
        this.mUserDeletionListener = repositoryUserDeletionInterface;
    }

    /**<-------Chats Interfaces------->**/
    /**<-------Download All Users interface------->**/
    public interface RepositoryDownloadAllUsersInterface {
        void onDownloadAllUsersSucceed(List<User> value);

        void onDownloadAllUsersFailed(String error);
    }

    private RepositoryDownloadAllUsersInterface mDownloadAllUsersListener;

    public void setDownloadAllUsersListener(RepositoryDownloadAllUsersInterface repositoryDownloadAllUsersInterface) {
        this.mDownloadAllUsersListener = repositoryDownloadAllUsersInterface;
    }

    /**<-------Download Conversation interface------->**/
    public interface RepositoryDownloadConversationInterface {
        void onDownloadConversationSucceed(List<ChatMessage> conversation);

        void onDownloadConversationFailed(String error);
    }

    private RepositoryDownloadConversationInterface mDownloadConversationListener;

    public void setDownloadConversationListener(RepositoryDownloadConversationInterface repositoryDownloadConversationInterface) {
        this.mDownloadConversationListener = repositoryDownloadConversationInterface;
    }

    /**<-------Upload Message interface------->**/
    public interface RepositoryUploadMessageInterface {
        void onUploadMessageSucceed(ChatMessage message);

        void onUploadMessageFailed(String error);
    }

    private RepositoryUploadMessageInterface mUploadMessageListener;

    public void setUploadMessageListener(RepositoryUploadMessageInterface repositoryUploadMessageInterface) {
        this.mUploadMessageListener = repositoryUploadMessageInterface;
    }

    /**<-------Download Active Chats interface------->**/
    public interface RepositoryDownloadActiveChatsInterface {
        void onDownloadActiveChatsSucceed(List<Conversation> conversations);

        void onDownloadActiveChatsFailed(String error);
    }

    private RepositoryDownloadActiveChatsInterface mDownloadActiveChatsListener;

    public void setDownloadActiveChatsListener(RepositoryDownloadActiveChatsInterface repositoryDownloadActiveChatsInterface) {
        this.mDownloadActiveChatsListener = repositoryDownloadActiveChatsInterface;
    }

    /**<-------Upload Pet Interface------->**/
    public interface RepositoryPetUploadInterface {
        void onPetUploadFailed(String error);
    }

    private RepositoryPetUploadInterface mPetUploadListener;

    public void setPetUploadListener(RepositoryPetUploadInterface repositoryPetUploadInterface) {
        this.mPetUploadListener = repositoryPetUploadInterface;
    }

    /**<-------MarketPlace interfaces------->**/
    /**<-------Upload Advertisement interface------->**/
    public interface RepositoryUploadAdInterface {
        void onUploadAdSucceed(Advertisement advertisement);

        void onUploadAdFailed(String error);
    }

    private RepositoryUploadAdInterface mUploadAdListener;

    public void setUploadAdListener(RepositoryUploadAdInterface repositoryUploadAdInterface) {
        this.mUploadAdListener = repositoryUploadAdInterface;
    }

    /**<-------Download Advertisements interface------->**/
    public interface RepositoryDownloadAdInterface {
        void onDownloadAdSucceed(List<Advertisement> adList);

        void onDownloadAdFailed(String error);
    }

    private RepositoryDownloadAdInterface mDownloadAdListener;

    public void setDownloadAdListener(RepositoryDownloadAdInterface downloadAdListener) {
        this.mDownloadAdListener = downloadAdListener;
    }

    /**<-------Delete Advertisement interface------->**/
    public interface RepositoryAdDeletingInterface {
        void onAdDeletingSucceed(String adId);

        void onAdDeletingFailed(String error);
    }

    private RepositoryAdDeletingInterface mAdDeletingListener;

    public void setAdDeletingListener(RepositoryAdDeletingInterface repositoryAdDeletingInterface) {
        this.mAdDeletingListener = repositoryAdDeletingInterface;
    }


    public static Repository getInstance(final Context context) {
        if (repository == null) {
            repository = new Repository(context);
        }
        return repository;
    }

    private Repository(final Context context) {
        this.mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mDatabase.setPersistenceEnabled(true);
        mDBChats = mDatabase.getReference().child("chats");
        mDBChats.keepSynced(true);
    }

    /**<-------Posts methods------->**/
    public void downloadPosts() {
        final List<Post> posts = new ArrayList<>();
        final FirebaseUser user = mAuth.getCurrentUser();

        final int distance = PreferenceManager.getDefaultSharedPreferences(mContext).getInt("distance_sb", 500);

        if (user != null) {
            mCloudDB.collectionGroup(POSTS)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    Post post = document.toObject(Post.class);
                                    if (post.getGeoPoint() != null) {
                                        if (LocationUtils.getDistance(post.getGeoPoint()) <= distance) {
                                            posts.add(post);
                                        }
                                    } else if (distance == 500) {
                                        posts.add(post);
                                    }
                                }
                                if (mRepositoryPostDownloadSucceedMLD != null) {
                                    Collections.sort(posts);
                                    mRepositoryPostDownloadSucceedMLD.setValue(posts);
                                }
                            } else {
                                Log.wtf(TAG, "onComplete: ", task.getException());

                                if (mRepositoryPostDownloadFailedMLD != null) {
                                    mRepositoryPostDownloadFailedMLD.setValue(Objects
                                            .requireNonNull(task.getException()).getMessage());
                                }
                            }
                        }
                    });
        }
    }

    public void downloadUserPosts(final String userEmail) {
        final List<Post> posts = new ArrayList<>();

        mCloudUsers.document(userEmail)
                .collection(POSTS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                posts.add(document.toObject(Post.class));
                            }

                            if (mRepositoryUserPostDownloadSucceedMLD != null) {
                                Collections.sort(posts);
                                mRepositoryUserPostDownloadSucceedMLD.setValue(posts);
                            }
                        } else {
                            Log.wtf(TAG, "onComplete: ", task.getException());

                            if (mRepositoryUserPostDownloadFailedMLD != null) {
                                mRepositoryUserPostDownloadFailedMLD.setValue(Objects
                                        .requireNonNull(task.getException()).getMessage());
                            }
                        }
                    }
                });
    }

    public void uploadNewPost(final Post post) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .collection(POSTS)
                    .document(post.getPostId())
                    .set(post)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            if (mRepositoryPostUploadSucceedMLD != null) {
                                mRepositoryPostUploadSucceedMLD.setValue(post);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if (mRepositoryPostUploadFailedMLD != null) {
                                mRepositoryPostUploadFailedMLD.setValue(e.getMessage());
                            }
                        }
                    });
        }
    }

    public void updatePost(final Post updatedPost) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final String postId = updatedPost.getPostId();
            final String updatedPostContent = updatedPost.getAuthorContent();

            Map<String, Object> updatePostMap = new HashMap<>();
            updatePostMap.put("authorContent", updatedPostContent);

            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .collection(POSTS)
                    .document(postId)
                    .update(updatePostMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            if (mRepositoryPostUpdateSucceedMLD != null) {
                                mRepositoryPostUpdateSucceedMLD.setValue(updatedPost);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if (mRepositoryPostUpdateFailedMLD != null) {
                                mRepositoryPostUpdateFailedMLD.setValue(e.getMessage());
                            }
                        }
                    });
        }
    }

    public void updatePostLikes(final Post post, final boolean isLike) {
        int likesAmount = post.getLikesCount() + (isLike ? 1 : -1);
        post.setLikesCount(likesAmount);

        if (isLike) {
            post.getLikesMap().put(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), true);
        } else {
            post.getLikesMap().remove(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
        }

        Map<String, Object> updateLikesMap = new HashMap<>();
        updateLikesMap.put("likesCount", likesAmount);
        updateLikesMap.put("likesMap", post.getLikesMap());

        mCloudUsers.document(post.getAuthorEmail())
                .collection(POSTS)
                .document(post.getPostId())
                .update(updateLikesMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if (mRepositoryPostLikesUpdateSucceedMLD != null) {
                            mRepositoryPostLikesUpdateSucceedMLD.setValue(post);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (mRepositoryPostLikesUpdateFailedMLD != null) {
                            mRepositoryPostLikesUpdateFailedMLD.setValue(e.getMessage());
                        }
                    }
                });
    }

    public void deletePost(final String postId) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .collection(POSTS)
                    .document(postId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            if (mRepositoryPostDeletionSucceedMLD != null) {
                                mRepositoryPostDeletionSucceedMLD.setValue(postId);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if (mRepositoryPostDeletionFailedMLD != null) {
                                mRepositoryPostDeletionFailedMLD.setValue(e.getMessage());
                            }
                        }
                    });
        }
    }

    public void downloadComments(final Post post) {
        final List<Comment> comments = new ArrayList<>();
        final FirebaseUser user = mAuth.getCurrentUser();

        final String postId = post.getPostId();
        final String authorEmail = post.getAuthorEmail();

        if (user != null) {
            mCloudUsers.document(authorEmail)
                    .collection(POSTS)
                    .document(postId)
                    .collection(COMMENTS)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    comments.add(document.toObject(Comment.class));
                                    Log.d(TAG, "onComplete: " + document.toObject(Post.class).toString());
                                }
                                if (mCommentDownloadListener != null) {
                                    Collections.sort(comments);
                                    mCommentDownloadListener.onCommentDownloadSucceed(comments);
                                }
                            } else {
                                if (mCommentDownloadListener != null) {
                                    Log.wtf(TAG, "onComplete: ", task.getException());
                                    mCommentDownloadListener.onCommentDownloadFailed(Objects
                                            .requireNonNull(task.getException()).getMessage());
                                }
                            }
                        }
                    });
        }
    }

    /**<-------Comments methods------->**/
    public void uploadComment(final Post post, final String commentContent) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final String postId = post.getPostId();
            final String authorEmail = post.getAuthorEmail();

            final Comment comment = new Comment(user.getEmail(), user.getDisplayName(),
                    Objects.requireNonNull(user.getPhotoUrl()).toString(),
                    commentContent);

            comment.setCommentId(user.getEmail() + System.nanoTime());

            mCloudUsers.document(authorEmail)
                    .collection(POSTS)
                    .document(postId)
                    .collection(COMMENTS)
                    .document(comment.getCommentId())
                    .set(comment)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mCommentUploadListener != null) {
                                mCommentUploadListener.onCommentUploadSucceed(comment);
                            }
                            post.setCommentsCount(post.getCommentsCount() + 1);
                            updateCommentsAmountOfPost(post);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mCommentUploadListener != null) {
                                mCommentUploadListener.onCommentUploadFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    public void updateComment(final Post post, final String commentId, final String updatedComment) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final String postId = post.getPostId();
            final String postAuthorEmail = post.getAuthorEmail();

            Map<String, Object> updateCommentMap = new HashMap<>();
            updateCommentMap.put("authorContent", updatedComment);

            mCloudUsers.document(postAuthorEmail)
                    .collection(POSTS)
                    .document(postId)
                    .collection(COMMENTS)
                    .document(commentId)
                    .update(updateCommentMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mCommentUpdatingListener != null) {
                                mCommentUpdatingListener.onCommentUpdatingSucceed(updatedComment);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mCommentUpdatingListener != null) {
                                mCommentUpdatingListener.onCommentUpdatingFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    private void updateCommentsAmountOfPost(final Post post) {
        final String postId = post.getPostId();
        final String authorEmail = post.getAuthorEmail();
        final int commentsAmount = post.getCommentsCount();

        Map<String, Object> updateCommentsAmountMap = new HashMap<>();
        updateCommentsAmountMap.put("commentsCount", commentsAmount);

        mCloudUsers.document(authorEmail)
                .collection(POSTS)
                .document(postId)
                .update(updateCommentsAmountMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if (mRepositoryPostUpdateSucceedMLD != null) {
                            mRepositoryPostUpdateSucceedMLD.setValue(post);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (mRepositoryPostUpdateFailedMLD != null) {
                            mRepositoryPostUpdateFailedMLD.setValue(e.getMessage());
                        }
                    }
                });
    }

    public void deleteComment(final Post post, final String commentId) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final String postId = post.getPostId();
            final String postAuthorEmail = post.getAuthorEmail();

            mCloudUsers.document(postAuthorEmail)
                    .collection(POSTS)
                    .document(postId)
                    .collection(COMMENTS)
                    .document(commentId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mCommentDeletingListener != null) {
                                mCommentDeletingListener.onCommentDeletingSucceed(commentId);
                            }
                            post.setCommentsCount(post.getCommentsCount() - 1);
                            updateCommentsAmountOfPost(post);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mCommentDeletingListener != null) {
                                mCommentDeletingListener.onCommentDeletingFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    /**
     * <-------Profile methods------->
     **/
    public void downloadUser(final String userEmail) {
        mCloudUsers.document(userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            if (mDownloadUserListener != null && user != null) {
                                mDownloadUserListener.onDownloadUserSucceed(user);
                            } else if (mDownloadUserListener != null && user == null) {
                                mDownloadUserListener.onDownloadUserFailed("USER NOT FOUND");
                            }
                        } else {
                            if (mDownloadUserListener != null) {
                                mDownloadUserListener.onDownloadUserFailed(Objects.
                                        requireNonNull(task.getException()).getMessage());
                            }
                        }
                    }
                });
    }

    public void updateUserLocation(final Address address) {
        final FirebaseUser user = mAuth.getCurrentUser();

        Map<String, Object> updateUserLocationMap = new HashMap<>();
        GeoPoint geoPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
        updateUserLocationMap.put("geoPoint", geoPoint);

        if (user != null) {
            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .update(updateUserLocationMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mUpdateUserLocationListener != null) {
                                mUpdateUserLocationListener.onUpdateUserLocationSucceed(address);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mUpdateUserLocationListener != null) {
                                mUpdateUserLocationListener.onUpdateUserLocationFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    public void updateUserName(final String newUserName) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final String firstName = newUserName.split(" ")[0];
            final String lastName = newUserName.split(" ")[1];

            Map<String, Object> updateUserNameMap = new HashMap<>();
            updateUserNameMap.put("firstName", firstName);
            updateUserNameMap.put("lastName", lastName);

            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .update(updateUserNameMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mUpdateUserNameListener != null) {
                                mUpdateUserNameListener.onUpdateUserNameSucceed(user.getDisplayName());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (mUpdateUserNameListener != null) {
                        mUpdateUserNameListener.onUpdateUserNameFailed(e.getMessage());
                    }
                }
            });
        }
    }

    public void updateUserProfileImage(final String newProfilePic) {
        final FirebaseUser user = mAuth.getCurrentUser();
        final boolean[] isImageUploaded = {false};
        final boolean[] isImageUpdated = {false};

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(newProfilePic))
                .build();

        if (user != null) {
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                isImageUpdated[0] = true;
                                if (mUpdateUserImageListener != null && isImageUploaded[0]) {
                                    mUpdateUserImageListener.onUpdateUserImageSucceed(newProfilePic);
                                }
                            }
                        }
                    });

            Map<String, Object> updateUserProfilePicMap = new HashMap<>();
            updateUserProfilePicMap.put("photoUri", newProfilePic);

            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .update(updateUserProfilePicMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            isImageUploaded[0] = true;
                            if (mUpdateUserImageListener != null && isImageUpdated[0]) {
                                mUpdateUserImageListener.onUpdateUserImageSucceed(newProfilePic);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (mUpdateUserImageListener != null) {
                        mUpdateUserImageListener.onUpdateUserImageFailed(e.getMessage());
                    }
                }
            });
        }
    }

    public void deleteUser() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mUserDeletionListener != null) {
                                mUserDeletionListener.onUserDeletionSucceed(user.getUid());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mUserDeletionListener != null) {
                                mUserDeletionListener.onUserDeletionFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    /**
     * <-------Chat methods------->
     **/
    public void downloadAllUsers() {
        final ArrayList<User> users = new ArrayList<>();
        mCloudUsers.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if (!Objects.equals(document.getData().get("email"),
                                        Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                                    users.add(document.toObject(User.class));
                            }
                            if (mDownloadAllUsersListener != null) {
                                mDownloadAllUsersListener.onDownloadAllUsersSucceed(users);
                            }
                        }
                    }
                });
    }

    public void uploadMessageToDB(final String messageContent,
                                  final String senderEmail,
                                  final String recipientEmail) {
        final ChatMessage message = new ChatMessage(messageContent, recipientEmail);

        final String id1 = senderEmail.replace(".", "");
        final String id2 = recipientEmail.replace(".", "");

        Conversation conversation = new Conversation(senderEmail, recipientEmail, message);

        final String chatId = conversation.getChatId();

        mDBChats.child(chatId).child(id1).setValue(true);
        mDBChats.child(chatId).child(id2).setValue(true);
        mDBChats.child(chatId)
                .child(CONVERSATION)
                .setValue(conversation);
        mDBChats.child(chatId)
                .child(MESSAGES)
                .child(message.getTime().toString())
                .setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (mUploadMessageListener != null) {
                            mUploadMessageListener.onUploadMessageSucceed(message);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        if (mUploadMessageListener != null) {
                            mUploadMessageListener.onUploadMessageFailed(e.getMessage());
                        }
                    }
                });
    }

    public Query ConversationQuery(final String chatId) {
        return mDBChats.child(chatId).child(MESSAGES).orderByChild("time/time");
    }

    public com.google.firebase.firestore.Query PetsQuery(final String userEmail) {
        return mCloudUsers.document(userEmail).collection("pets");
    }


    public void downloadActiveChats() {
        final ArrayList<Conversation> conversations = new ArrayList<>();

        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = "";

        if (user != null) {
            userEmail = Objects.requireNonNull(user.getEmail()).replace(".", "");
        }

        mDBChats.orderByChild(userEmail).equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Conversation conversation;
                            if (!conversations.isEmpty()) {
                                conversations.clear();
                            }
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                conversation = ds.child(CONVERSATION).getValue(Conversation.class);
                                if (conversation != null) {
                                    conversations.add(conversation);
                                }
                            }

                            if (mDownloadActiveChatsListener != null) {
                                mDownloadActiveChatsListener.onDownloadActiveChatsSucceed(conversations);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (mDownloadActiveChatsListener != null) {
                            mDownloadActiveChatsListener.onDownloadActiveChatsFailed(error.getMessage());
                        }
                    }
                });

    }

    public void uploadPetToUser(Pet pet) {
        String userEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        if (userEmail != null) {
            mCloudUsers.document(userEmail).collection("pets")
                    .document(String.valueOf(System.nanoTime())).set(pet)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mPetUploadListener != null) {
                                mPetUploadListener.onPetUploadFailed(e.getMessage());
                            }
                        }
                    });

        }
    }

    public void uploadAd(final Advertisement advertisement) {
        String userEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();


        if (userEmail != null) {
            mCloudAds.document(advertisement.getAdvertisementId())
                    .set(advertisement)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mUploadAdListener != null) {
                                mUploadAdListener.onUploadAdSucceed(advertisement);

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mUploadAdListener != null) {
                                mUploadAdListener.onUploadAdFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    public void updateAdLocation(final Address address, final Advertisement advertisement) {

        Log.d(TAG, "updateAdLocation: momo");

        final FirebaseUser user = mAuth.getCurrentUser();

        Map<String, Object> updateAdMap = new HashMap<>();
        final GeoPoint geoPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
        updateAdMap.put("geoPoint", geoPoint);

        if (user != null) {
            mCloudAds.document(advertisement.getAdvertisementId())
                    .update(updateAdMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: abu enak" + geoPoint.getLongitude() + geoPoint.getLatitude());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: bad");
                        }
                    });
        }

    }


    public void deleteAdvertisement(final Advertisement advertisement) {
        mCloudAds.document(advertisement.getAdvertisementId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (mAdDeletingListener != null) {
                            mAdDeletingListener.onAdDeletingSucceed(advertisement.getAdvertisementId());
                        }
                    }
                });
    }

    public void downloadAllAds() {

        final List<Advertisement> adList = new ArrayList<>();
        final FirebaseUser user = mAuth.getCurrentUser();

        final int distance = PreferenceManager.getDefaultSharedPreferences(mContext).getInt("distance_sb", 500);


        if (user != null) {
            mCloudAds.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    Advertisement ad = document.toObject(Advertisement.class);
                                    if (ad.getGeoPoint() != null) {
                                        if (LocationUtils.getDistance(ad.getGeoPoint()) <= distance) {
                                            adList.add(ad);
                                        }
                                    } else if (distance == 500) {
                                        adList.add(ad);
                                    }
                                }

                                if (mDownloadAdListener != null) {
                                    mDownloadAdListener.onDownloadAdSucceed(adList);
                                }
                            } else {
                                Log.wtf(TAG, "onComplete: ", task.getException());

                                if (mDownloadAdListener != null) {
                                    mDownloadAdListener.onDownloadAdFailed(Objects
                                            .requireNonNull(task.getException()).getMessage());
                                }
                            }
                        }
                    });
        }
    }
}
