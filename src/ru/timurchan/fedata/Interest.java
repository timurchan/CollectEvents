package ru.timurchan.fedata;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class Interest implements Serializable, Comparable<Interest> {
    static public int STATUS_NONE = 0;
    static public int STATUS_USED = 1;
    static public int STATUS_GRAY = 2; // children used or partitially used
    static public int STATUS_NOT_EDIT = -1;

    public String title;
    public int parentId = 0;
    public int rootId = 0;
    public String rootTitle = "";
    public int id;

    public int children_used = 0;   // count of children which are used
    public int edit_children_used = 0;   // count of children which are selected while editing

    public int status_me = STATUS_NONE;
    public int status = STATUS_NONE;
    public int edit_status = STATUS_NOT_EDIT;


    /* Как мне видится */
    public boolean selected = false; // Отмечен в одном из списков
    public boolean hasSelectedChildren = false; // Кто-то из детей отмечен, не связанные понятия с передыдущим
    public int childrenCount = 0;
    public int childrenSelectedCount = 0;
    /* Как мне видится */


//    // check_parent = true --> need to check, if interest has children
//    // if it has, add '_parent' suffix to name
//    private String getIconName(boolean check_parent) {
//        String iconDrawable;
//        if(id == 0 && parentId == 0)
//            iconDrawable = "interesticon_blank";
//        else if(parentId == 0) {
//            if(CommonInfoManager.getRootInterests().contains(id))
//                iconDrawable = "interesticon_" + Integer.toString(id);
//            else
//                iconDrawable = "interesticon_blank";
//        } else {
//            iconDrawable = "interesticon_" + Integer.toString(rootId);
//        }
//        if(check_parent && CommonInfoManager.getInterestsMap().containsKey(id))
//            iconDrawable += "_parent";
//        return iconDrawable;
//    }
//
//    public int getIconOriginal(Context context) {
//        return context.getResources().getIdentifier(getIconName(false), "drawable", context.getPackageName());
//    }
//
//    public int getIcon(Context context) {
//        return context.getResources().getIdentifier(getIconName(true), "drawable", context.getPackageName());
//    }

    public String toString() {
        return "{" + id + ", " + title + ", " + parentId + ", " + status + ", " + children_used + "}";
    }

    public void reset() {
        status = STATUS_NONE;
        children_used = 0;

        edit_children_used = 0;
        edit_status = STATUS_NOT_EDIT;

        status_me = STATUS_NONE;
    }

    public void reset_edit_status() {
        edit_children_used = 0;
        edit_status = STATUS_NOT_EDIT;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Interest) {
            return equals((Interest) o);
        } else
            return super.equals(o);
    }

    public boolean equals(final Interest other) {
        if(this == other)
            return true;

        boolean res = title.equals(other.title);
        res = res && parentId == other.parentId;
        res = res && id == other.id;
        return res;
    }
//
//    public int getColor() {
//        int color;
//        Map<Integer, String> colors = new TreeMap<>();
//
//        colors.put(71159, "#99c4375c");
//        colors.put(71112, "#99c49e00");
//        colors.put(71111, "#99115bd1");
//        colors.put(71022, "#9954b948");
//        colors.put(70982, "#997848b7");
//        colors.put(70977, "#99cc5cca");
//        colors.put(70956, "#9939bced");
//        colors.put(70954, "#9991ba11");
//        colors.put(71254, "#99f58235");
//        colors.put(0, "#c13838");
//
//        if(rootId != 0)
//            color = Color.parseColor(colors.get(rootId));
//        else
//            color = Color.parseColor("#ccddee");
//
//        return color;
//
//    }

    @Override
    public int compareTo(Interest interest) {
        Integer thisId = this.id;
        Integer thatId = interest.id;
        return thisId.compareTo(thatId);
    }
}
