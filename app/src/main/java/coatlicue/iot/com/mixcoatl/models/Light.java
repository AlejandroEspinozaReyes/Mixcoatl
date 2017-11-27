package coatlicue.iot.com.mixcoatl.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import coatlicue.iot.com.mixcoatl.BR;

public class Light extends BaseObservable {
    private Boolean state;

    public Light() {
        this.state = false;
    }

    @Bindable
    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        if (this.state != state) {
            this.state = state;
            notifyPropertyChanged(BR.state);
        }
    }
}