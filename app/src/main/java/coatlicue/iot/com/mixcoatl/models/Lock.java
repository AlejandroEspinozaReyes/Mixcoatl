package coatlicue.iot.com.mixcoatl.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import coatlicue.iot.com.mixcoatl.BR;

public class Lock extends BaseObservable {
    private Integer state;

    public Lock() {
        this.state = 3;
    }

    @Bindable
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        if (this.state != state) {
            this.state = state;
            notifyPropertyChanged(BR.state);
        }
    }
}