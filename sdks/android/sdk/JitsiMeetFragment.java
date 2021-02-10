package org.jitsi.meet.sdk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class JitsiMeetFragment extends Fragment {
   private JitsiMeetView view;

   @Nullable
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      return this.view = new JitsiMeetView(this.getActivity());
   }

   public JitsiMeetView getJitsiView() {
      return this.view;
   }

   public void onDestroy() {
      super.onDestroy();
      JitsiMeetActivityDelegate.onHostDestroy(this.getActivity());
   }

   public void onResume() {
      super.onResume();
      JitsiMeetActivityDelegate.onHostResume(this.getActivity());
   }

   public void onStop() {
      super.onStop();
      JitsiMeetActivityDelegate.onHostPause(this.getActivity());
   }
}
