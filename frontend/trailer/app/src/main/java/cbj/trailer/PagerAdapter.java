package cbj.trailer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;
    private int [] data;

    public PagerAdapter(@NonNull FragmentManager fm, int numOfTabs, int [] data) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.data = data;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "일별";
            case 1:
                return "주별";
            default:
                return null;
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabFragment1(data);
            case 1:
                return new TabFragment2();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
