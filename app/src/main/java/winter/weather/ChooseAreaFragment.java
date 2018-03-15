package winter.weather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import winter.weather.db.City;
import winter.weather.db.County;
import winter.weather.db.Province;
import winter.weather.util.HttpUtil;
import winter.weather.util.Utility;

/**
 * Created by winter on 3/14/2018.
 */

public class ChooseAreaFragment extends Fragment {
    private static final String TAG = "ChooseAeraFragment";
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backBtn;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = view.findViewById(R.id.title_text);
        listView = view.findViewById(R.id.list_view);
        backBtn = view.findViewById(R.id.back_button);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel == LEVEL_CITY)
                    queryProvinces();
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {
        titleText.setText("China");
        Log.d(TAG, "queryProvinces: setText china.");
        backBtn.setVisibility(View.GONE);
        Log.d(TAG, "queryProvinces: backBtn gone.");
        provinceList = DataSupport.findAll(Province.class);
        Log.d(TAG, "queryProvinces: findAll done.");
        if (provinceList.size() > 0){
            Log.d(TAG, "queryProvinces: from local database.");
            dataList.clear();
            for (Province province : provinceList)
                dataList.add(province.getProvinceName());
            Log.d(TAG, "queryProvinces: " + dataList.toString());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            Log.d(TAG, "queryProvinces: from internet.");
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backBtn.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?",
                /*"provinceId" to "porvinceId" firstly, check the cache file of app, uninstall
                and reinstall, get this problem solved.
                 */
                String.valueOf(selectedProvince.getId())).find(City.class); //WTF;
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList)
                dataList.add(city.getCityName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backBtn.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList)
                dataList.add(county.getCountyName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    private void queryFromServer(String address, final String type) {
        showDialog();
        HttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDialog();
                        Toast.makeText(getContext(), "Load failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.d(TAG, "onResponse: " + responseText);
                boolean result = false;
                if ("province".equals(type))
                    result = Utility.handleProvinceResponse(responseText);
                else if ("city".equals(type))
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                else if ("county".equals(type))
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeDialog();
                            if ("province".equals(type))
                                queryProvinces();
                            else if ("city".equals(type))

                                queryCities();
                            else if ("county".equals(type))
                                queryCounties();
                        }
                    });
                }
            }
        });
    }

    private void showDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
