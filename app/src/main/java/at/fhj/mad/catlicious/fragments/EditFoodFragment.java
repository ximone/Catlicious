package at.fhj.mad.catlicious.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import at.fhj.catlicious.common.Assert;
import at.fhj.mad.catlicious.R;
import at.fhj.mad.catlicious.data.entity.Food;
import at.fhj.mad.catlicious.service.CameraService;
import at.fhj.mad.catlicious.service.CameraServiceImpl;
import at.fhj.mad.catlicious.service.FoodDAOService;
import at.fhj.mad.catlicious.service.FoodDAOServiceImpl;
import at.fhj.mad.catlicious.utils.ImageUtil;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;
import static at.fhj.mad.catlicious.utils.RequestCode.CAMERA_REQUEST;

/**
 * Created by Simone on 22.04.2017.
 */

public class EditFoodFragment extends Fragment {

    private CameraService cameraService;
    private Fragment currentFragment;
    private FoodDAOService foodDaoService;
    private Context context;
    private EditText editBrand;
    private EditText editSort;
    private ImageView editImage;
    private Food food;

    public EditFoodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        cameraService = new CameraServiceImpl();
        foodDaoService = new FoodDAOServiceImpl();
        context = container.getContext();

        currentFragment = this;
        View view = inflater.inflate(R.layout.fragment_edit_food, container, false);

        Bundle bundle = currentFragment.getArguments();
        food = (Food) bundle.getSerializable("food");
        cameraService = new CameraServiceImpl();

        initFields (view);

        displayFood(food);

        Button btUpdate = (Button) view.findViewById(R.id.update_food);
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFood();
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraService.captureImageFromCamera(currentFragment);
            }
        });

        return view;
    }

    /**
     * inits the view components depending on their values
     * @param view
     */
    public void initFields(View view) {
        editImage = (ImageView) view.findViewById(R.id.edit_food_image);
        editBrand = (EditText) view.findViewById(R.id.edit_brand_name);
        editSort = (EditText) view.findViewById(R.id.edit_sort_name);
    }

    /**
     * updates a selected food entity
     */
    public void updateFood() {
        food.setBrand(editBrand.getText().toString());
        food.setSort(editSort.getText().toString());
        foodDaoService.updateFood(food, context);

        // go back and show the list of all food entities
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.popBackStack();
    }

    /**
     * shows the selected food entity in the edit menu
     * @param food
     */
    public void displayFood(Food food) {
        editBrand.setText(food.getBrand());
        editSort.setText(food.getSort());
        Assert.notNull(food);
        editImage.setImageBitmap(ImageUtil.convertByteArrayToBitmap(food.getImage()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            editImage.setImageBitmap(photo);
            food.setImage(stream.toByteArray());
        }
    }
}
