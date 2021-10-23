package work.curioustools.dogfinder

import android.os.Bundle

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}

/*

    <com.google.android.material.imageview.ShapeableImageView
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:id="@+id/ivDog"
        android:background="@color/black"
        />



    <androidx.appcompat.widget.AppCompatImageButton
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:src=""
* */