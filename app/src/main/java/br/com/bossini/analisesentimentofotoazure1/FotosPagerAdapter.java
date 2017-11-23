package br.com.bossini.analisesentimentofotoazure1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by rodrigo on 11/16/17.
 */

public class FotosPagerAdapter  extends FragmentStatePagerAdapter{


    private List<Fragment> fragments;
    public FotosPagerAdapter (FragmentManager fm, List <Fragment> fragments){
        super (fm);
        this.fragments = fragments;
    }
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


}
