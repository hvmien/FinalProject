package ptit.nttrung.finalproject.ui.add_restaurant;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseActivity;
import ptit.nttrung.finalproject.ui.gallery.file.ImageGalleryBean;
import ptit.nttrung.finalproject.ui.gallery.folder.GalleryFolderActivity;
import ptit.nttrung.finalproject.util.helper.TimeUtils;

public class AddRestaurantActivity extends BaseActivity {

    public static final int REQUEST_LOC = 1995;
    public static final int REQUEST_IMG = 1000;

    @BindView(R.id.linear_layout_map_location)
    LinearLayout layoutMapLoca;
    @BindView(R.id.tab_back_place)
    LinearLayout layoutBack;
    @BindView(R.id.linear_layout_choose_type_res)
    LinearLayout layoutChooseTypeRes;
    @BindView(R.id.text_view_open_time)
    TextView tvOpenTime;
    @BindView(R.id.text_view_close_time)
    TextView tvCloseTime;
    @BindView(R.id.tv_lat_lng)
    TextView tvLatLng;
    @BindView(R.id.edit_text_name_res)
    EditText etNameRes;
    @BindView(R.id.edit_text_address_res)
    EditText etAddressRes;
    @BindView(R.id.text_view_done)
    TextView tvDone;
    @BindView(R.id.iv_add_image_res)
    ImageView ivAddImageRes;
    @BindView(R.id.tv_number_img)
    TextView tvNumberImg;
    @BindView(R.id.edit_text_min_cash)
    EditText etMinCash;
    @BindView(R.id.edit_text_max_cash)
    EditText etMaxCash;
    @BindView(R.id.edit_text_short_descr)
    EditText etDescription;

    private ArrayList<ImageGalleryBean> imageGalleryBeen;
    private double selectedLat = 0, selectedLng = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tab_back_place, R.id.linear_layout_map_location, R.id.text_view_open_time, R.id.text_view_close_time,
            R.id.linear_layout_choose_type_res, R.id.text_view_done, R.id.iv_add_image_res})
    public void onViewClicked(View view) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        switch (view.getId()) {
            case R.id.tab_back_place:
                finish();
                break;
            case R.id.linear_layout_map_location:
                startActivityForResult(new Intent(this, MapsChooseLocation.class), REQUEST_LOC);
                break;
            case R.id.text_view_open_time:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        tvOpenTime.setText(TimeUtils.parseTime2Str(calendar));
                    }
                }, hour, minutes, true).show();
                break;
            case R.id.text_view_close_time:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        tvCloseTime.setText(TimeUtils.parseTime2Str(calendar));
                    }
                }, hour, minutes, true).show();
                break;
            case R.id.linear_layout_choose_type_res:
//                showDialogChooseType();
                break;
            case R.id.iv_add_image_res:
                Intent intent = new Intent(this, GalleryFolderActivity.class);
                startActivityForResult(intent, 1000);
                break;
            case R.id.text_view_done:
                if (checkValidate()) uploadRestaurant();
                break;
        }
    }

    public void uploadRestaurant() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOC) {
            if (resultCode == 999) {
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(3);
                selectedLat = data.getExtras().getDouble("lat");
                selectedLng = data.getExtras().getDouble("long");
                tvLatLng.setText("Lat " + df.format(selectedLat) + " - Long " + df.format(selectedLng));

                if (data.hasExtra("address")) {
                    String address = data.getExtras().getString("address");
                    etAddressRes.setText(address);
                }
            }
        } else if (requestCode == REQUEST_IMG) {
            if (resultCode == RESULT_OK) {
                imageGalleryBeen = data.getParcelableArrayListExtra("images");
                if (imageGalleryBeen == null || imageGalleryBeen.size() == 0) {
                    tvNumberImg.setVisibility(View.GONE);
                } else {
                    for (int i = 0; i < imageGalleryBeen.size(); i++) {
                        Log.e("image path", imageGalleryBeen.get(i).getPath());
                    }
                    tvNumberImg.setVisibility(View.VISIBLE);
                    tvNumberImg.setText(imageGalleryBeen.size() + "");
                }
            }
        }
    }

    public boolean checkValidate() {
        boolean valid = true;
        String name = etNameRes.getText().toString();
        String address = etAddressRes.getText().toString();

        if (name.isEmpty()) {
            etNameRes.setError("Error");
            valid = false;
        } else {
            etNameRes.setError(null);
        }

        if (address.isEmpty()) {
            etAddressRes.setError("Empty");
            valid = false;
        } else {
            etAddressRes.setError(null);
        }

        if (selectedLat == 0 || selectedLng == 0) {
            makeToastError("Chưa chọn địa điểm");
            valid = false;
        }

        return valid;
    }
}
