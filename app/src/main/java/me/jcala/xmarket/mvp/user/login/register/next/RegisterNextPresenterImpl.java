package me.jcala.xmarket.mvp.user.login.register.next;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.List;

import me.jcala.xmarket.data.api.ReqExecutor;
import me.jcala.xmarket.data.dto.Result;
import me.jcala.xmarket.data.pojo.User;
import me.jcala.xmarket.data.storage.SharedPreferencesStorage;

public class RegisterNextPresenterImpl
        implements RegisterNextPresenter,RegisterNextModel.onRegisterNextListener{

    private Context context;
    private RegisterNextModel model;
    private RegisterNextView view;

    public RegisterNextPresenterImpl(Context context, RegisterNextView view) {
        this.context = context;
        this.view = view;
        this.model=new RegisterNextModelImpl();
    }

    @Override
    public void getSchoolList() {
        model.executeSchoolRequest(this);
    }

    @Override
    public void hasGotSchoolList(Result<List<String>> result) {
        if (result==null){
            return;
        }
        switch (result.getCode()) {
            case 100:
                view.whenGetSchoolListSuccess(result.getData());
                break;
            case 99:
                view.whenFails(result.getMsg());
                break;
            default:
                break;
        }
    }

    @Override
    public void hasGotRegisterResult(Result<User> result) {
        view.whenStopSetProgress();
        if (result==null){
            return;
        }
        switch (result.getCode()) {
            case 100:
                SharedPreferencesStorage.instance.saveUserToken(context,result.getData());
                ReqExecutor.INSTANCE().setToken(result.getData().getToken());
                view.whenRegisterSuccess();
                break;
            case 99:
                view.whenFails(result.getMsg());
                break;
            default:
                break;
        }
    }

    @Override
    public void registerNext(String userId,String phone, String school) {
        if (checkInput(userId,phone,school)){
            model.executeRegisterRequest(userId,phone,school,this);
            view.whenStartSetProgress();
        }
    }

    private boolean checkInput(String user_id,String phone,String school){
       /* if (user_id==null || user_id.isEmpty()){
            view.whenFails("APP发生异常,userId为空");
            return false;
        }*/
        if (phone.isEmpty()){
            view.whenFails("电话号码不可以为空");
            return false;
        }
        if ("点击设置学校".equals(school)){
            view.whenFails("请设置学校名称");
            return false;
        }
        return true;
    }

    @Override
    public void checkPhone(final TextInputLayout phoneLayout,final EditText phone) {
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneData=phone.getText().toString().trim();
                if (phoneData.isEmpty()) {
                    phoneLayout.setErrorEnabled(true);
                    phoneLayout.setError("输入不可以为空");
                }else {
                    phoneLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}
