package cz.vspj.schrek.im.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by schrek on 31.03.2017.
 */

public class Meetup implements Serializable {
    public String icon;
    public String message;
    public String term;
    public String title;
    public List<String> invitedUsers = new ArrayList<>();
}
