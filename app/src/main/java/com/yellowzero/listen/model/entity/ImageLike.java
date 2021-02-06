package com.yellowzero.listen.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ImageLike {
    @Id
    private long id;

    @Generated(hash = 274807724)
    public ImageLike(long id) {
        this.id = id;
    }

    @Generated(hash = 897914664)
    public ImageLike() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
