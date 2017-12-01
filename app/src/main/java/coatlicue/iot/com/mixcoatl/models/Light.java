package coatlicue.iot.com.mixcoatl.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import coatlicue.iot.com.mixcoatl.BR;

public class Light extends BaseObservable {
    private boolean state;

    public Light() {
        this.state = false;
    }

    @Bindable
    public Boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        if (this.state != state) {
            this.state = state;
            notifyPropertyChanged(BR.state);
        }
    }
}