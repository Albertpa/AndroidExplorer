package com.aprendeandroid.androidexplorer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SupportListFragment extends ListFragment {
	
	private int index = 0;
	private ListItemSelectedListener selectedListener;
	int listLayoutId = 0;
	int emptyViewId = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		Bundle parametros = getArguments();
		if(parametros != null) {
			listLayoutId = parametros.getInt("listLayoutId");
			emptyViewId = parametros.getInt("emptyViewId");
		}
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View myFragmentView  = null;
		
        if(container == null){
            return null;                                
        }
		
		try { // layout personalizado
			myFragmentView = inflater.inflate(listLayoutId,container, false);
		} 
		catch (Exception e) { // layout por defecto
			myFragmentView = inflater.inflate(android.R.layout.list_content,container, false);		
		}
		
		return myFragmentView;
	}

	
	
	
	
//-------------------------------------PONE UNA VIEW ALTERNATIVA SI NO CARGA LISTA (en este caso un emptyViewId que llega por Bundle en onCreate)
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ListView listView = getListView();
		View emptyView = getActivity().findViewById(emptyViewId);
		if(emptyView != null) {
			listView.setEmptyView(emptyView);
		}
	}	


	
	
	
//---------------------------------------------------------SI EL FRAGMENT SE PARA, (giro de pantalla)
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("index", index);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			index = savedInstanceState.getInt("index", 0);
			selectedListener.onListItemSelected(index);
		}
	}
	

	
//----------------------------------------------------------PARA LA INTERFAZ ListItemSelectedListener
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			selectedListener = (ListItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ListItemSelectedListener in Activity");
		}
	}

	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		index = position;
		selectedListener.onListItemSelected(index);
	}

	public interface ListItemSelectedListener {
		public void onListItemSelected(int index);
	}

}
