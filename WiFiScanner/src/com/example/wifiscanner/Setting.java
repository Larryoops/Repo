/**
*  @auther : Larryoops, Edgejerry
*  @version 1.5
*  primary activity : information.class
*  secondary activity : MainActivity.class
*  floating view manager : MyWindowManager.class(manage FloatWindowSmallView & BigWindow.class)
*  view : draw.class, FloatWindowSmallView.class, BigWindow.class
*  service : FloatWindowService.class, WifiService.class
*  object : WifiInformation.class(information.class), Node.class(information.class),, Signal.class(information.class),, FinalInformation.class(MyWindowManager.class)
*  layout : activity_main.xml(information.class), newlayout.xml(MainActivity.class)
*/
package com.example.wifiscanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.Toast;

public class Setting extends PreferenceActivity implements OnPreferenceChangeListener {
	

	ListPreference voronoi_setting;
	ListPreference DifferenceFilter;
	ListPreference PathLoss;
	ListPreference Frequency;
	ListPreference StrideMultiply;
	SwitchPreference DisplayNode;
	SwitchPreference VDisplayNode;
	SwitchPreference DisplayPath;
	SwitchPreference VDisplayPath;
	SwitchPreference VDisplayWifiCircle;
	Preference reset_button;
	SwitchPreference NoStopPath;
	SwitchPreference VDisplayNOCALNode;
	SwitchPreference VDisplayPossibleAP;
	SwitchPreference DisplayRuler;
	
	EditTextPreference DefaultRSSI;
	private SharedPreferences sharedPreferences;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		sharedPreferences =PreferenceManager.getDefaultSharedPreferences(this);
		
		reset_button=(Preference)findPreference("reset_button");
		reset_button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) { 
            	VDisplayWifiCircle.setChecked(false);
            	DisplayNode.setChecked(true);
            	DisplayPath.setChecked(true);
            	VDisplayPath.setChecked(true);
            	NoStopPath.setChecked(false);
            	VDisplayNOCALNode.setChecked(false);
            	VDisplayPossibleAP.setChecked(false);
            	voronoi_setting.setValue("3");
            	DifferenceFilter.setValue("10");
            	PathLoss.setValue("2");
            	DefaultRSSI.setText("50");
            	StrideMultiply.setValue("30");
            	Frequency.setValue("0.1");
            	DisplayRuler.setChecked(true);
                return true;
            }
        });
		DisplayRuler=(SwitchPreference)findPreference("DisplayRuler");
		
		VDisplayPossibleAP=(SwitchPreference)findPreference("VDisplayPossibleAP");
		//VDisplayPossibleAP.setChecked(false);
		
		VDisplayNOCALNode=(SwitchPreference)findPreference("VDisplayNOCALNode");
		//VDisplayNOCALNode.setChecked(false);
		
		NoStopPath=(SwitchPreference)findPreference("NoStopPath");
		//NoStopPath.setChecked(false);
		
		VDisplayWifiCircle=(SwitchPreference)findPreference("VDisplayWifiCircle");
		//VDisplayWifiCircle.setChecked(false);
		
		DisplayNode=(SwitchPreference)findPreference("DisplayNode");
		//DisplayNode.setChecked(true);
		
		VDisplayNode=(SwitchPreference)findPreference("VDisplayNode");
		//VDisplayNode.setChecked(false);
		
		DisplayPath=(SwitchPreference)findPreference("DisplayPath");
		//DisplayPath.setChecked(true);
		
		VDisplayPath=(SwitchPreference)findPreference("VDisplayPath");
		//VDisplayPath.setChecked(true);
		
		voronoi_setting=(ListPreference)findPreference("VoronoiNode");
		CharSequence[] entries=voronoi_setting.getEntries();
		int index=voronoi_setting.findIndexOfValue(voronoi_setting.getEntry().toString());
		voronoi_setting.setSummary("每隔 "+entries[index]+" 個點取一個節點");		
		voronoi_setting.setOnPreferenceChangeListener(this);
		
		DifferenceFilter=(ListPreference)findPreference("DifferenceFilter");
		CharSequence[] Dentries=DifferenceFilter.getEntries();
		int Dindex=DifferenceFilter.findIndexOfValue(DifferenceFilter.getEntry().toString());
		DifferenceFilter.setSummary(Dentries[Dindex]);
		DifferenceFilter.setOnPreferenceChangeListener(this);
		
		PathLoss=(ListPreference)findPreference("PathLoss");
		CharSequence[] Pentries=PathLoss.getEntries();
		int Pindex=PathLoss.findIndexOfValue(PathLoss.getEntry().toString());
		PathLoss.setSummary(Pentries[Pindex]);
		PathLoss.setOnPreferenceChangeListener(this);
		
		DefaultRSSI=(EditTextPreference)findPreference("DefaultRSSI");
		DefaultRSSI.setSummary(DefaultRSSI.getText());
		DefaultRSSI.setOnPreferenceChangeListener(this);
		
		Frequency=(ListPreference)findPreference("Frequency");
		CharSequence[] Fentries=Frequency.getEntries();
		
		
		//int Findex=Frequency.findIndexOfValue(Frequency.getEntry().toString());
		//Frequency.setSummary(Fentries[Findex]);
		Frequency.setSummary(sharedPreferences.getString("Frequency", "0.1秒"));
		Frequency.setOnPreferenceChangeListener(this);
		
		StrideMultiply=(ListPreference)findPreference("StrideMultiply");
		CharSequence[] Sentries=StrideMultiply.getEntries();
		int Sindex=StrideMultiply.findIndexOfValue(StrideMultiply.getEntry().toString());
		StrideMultiply.setSummary(Sentries[Sindex]);
		StrideMultiply.setOnPreferenceChangeListener(this);
		
		
	
	}
	public boolean onPreferenceChange(Preference preference, Object newValue)
	{
		if(preference instanceof ListPreference)
		{
			ListPreference lp=(ListPreference)preference;
			if(lp.getKey().equals("VoronoiNode"))
			{
				CharSequence[] entries=lp.getEntries();
				int index=lp.findIndexOfValue((String) newValue);
				lp.setSummary("每隔 "+entries[index]+" 個點取一個節點");
			}
			else 
			{
				CharSequence[] entries=lp.getEntries();
				int index=lp.findIndexOfValue((String) newValue);
				lp.setSummary(entries[index]);
			}
			
		}
		else if(preference instanceof EditTextPreference)
		{
			
			EditTextPreference etp=(EditTextPreference)preference;
			etp.setSummary((String)newValue);
		}
		return true;
	}

}