package com.aprendeandroid.androidexplorer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aprendeandroid.androidexplorer.SupportListFragment.ListItemSelectedListener;

public class AndroidExplorerActivity extends FragmentActivity implements ListItemSelectedListener{
	/*
	 * 
	 * Albert Pagès Raventos
	 * 14-02-2013
	 */
	//Fragments
	private SupportListFragment listFrag;
	
	//ArrayList<DatosFichero> listaDatosFichero = new ArrayList<DatosFichero>();
	
	//Para el sistema de directorios y archivos
	private ArrayList<String> paths = null;
	private ArrayList<String> nameFiles = null;
	private String root="/"; // directorio raiz
	private String currentDir = root;
	
	//Para elementos de la vista, muestra el path actual
	private TextView myPath; 
	
	//Para filtrar los archivos por extensiones, si se queda null, los muestra todos
	public static String[] extension = null;
	
	//Por si la orientacion cambia
	private boolean orientationChange = false;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_android_explorer);
		
		
		//INSERTAMOS EL FRAGMENT CONFIGURADO CON BUNDLE PARA SU PROPIO LAYOUT
		listFrag = new SupportListFragment();
		
		Bundle parametros = new Bundle();
		parametros.putInt("listLayoutId", R.layout.list_fragment);
		listFrag.setArguments(parametros);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		
		//Donde queremos poner este fragment, el fragment a poner, el identificador
		ft.add(R.id.listPlace, listFrag, "list");
		ft.commit();
		
		/*
		//Para la primera prueba, solo ver la lista con items falsos
		ArrayList<String> listaPrueba = new ArrayList<String>();
		
		for(int i=0; i<100; i++){
			listaPrueba.add("item "+ i);
		}
		//Contexto, como queremos que se ordenen los elementos dentro de cada fila, el aspecto delt exto de cada fila y los strings de la lista
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listaPrueba);
		
		//ya sabe recibir el adapter y distribuirse
		listFrag.setListAdapter(adapter);
		*/
		
		
		//Recogemos una referencia al campo de texto superior para informar al usuario del directorio actual
		myPath = (TextView)findViewById(R.id.path);
		
		//If para controlar los giros de la pantalla
		if(savedInstanceState != null && savedInstanceState.containsKey("currentDir")) {
        	currentDir = savedInstanceState.getString("currentDir");
        }
		
		
		//llamamos a este metodo y le pasamos el path actual para que lo recorra y saque archivos o carpetas
		getDir(currentDir);
	}
	
	
	//En este metodo se accede cuando se detecta que todo se va a resetear
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("currentDir", currentDir);
	}
	
	//Cuando la configuración se modifica
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		orientationChange = true;
	}
	
	
	
	/**
	 * Recibe el path de un directorio e inserta en el Adapter del ListView
	 * los nombres de los ficheros que tengan las extensiones del array extension,
	 * los subdirectorios, el directorio raiz y el directorio padre si existe.
	 * 
	 * @param dirPath  el path del directorio
	 */
	private void getDir(String dirPath){
		
    	myPath.setText("Location: " + dirPath); // muestra el path actual en el textView
    	
    	paths = new ArrayList<String>(); // se crea la lista de paths
    	nameFiles = new ArrayList<String>(); // se crea la lista para los nombres y sus extensiones
    	
    	// se crea el objeto File, que corresponde a un directorio
    	File f = new File(dirPath);
    	
    	// se carga la lista de ficheros con las extensiones o directorios
    	File[] files = f.listFiles(new ProjectFilter()); 
    	
    	
    	
    	if(!dirPath.equals(root)){ // si no es el directorio raiz, colocamos para ir a raiz o al directorio anterior
    		paths.add(root); //añade root "/"
    		nameFiles.add("/");
    		paths.add(f.getParent()); //añade directorio padre "//"
    		nameFiles.add("../");
    	}
    	
    	// añade a las listas los ficheros con las extensiones
    	for(int i=0; i < files.length; i++){
			File file = files[i];
			paths.add(file.getPath());
			nameFiles.add(file.getName());
    	}
    	
		// creamos el Adapter del ListView con los nombres de ficheros/directorios (item)
		// le mandamos en contexto actual, el control a rellenar y la lista de tiems
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, paths); 
    	
    	//A modificar con el FileAdapter
    	//contexto, como queremos que muestre cada fila, lista de paths
    	FileAdapter adapter = new FileAdapter(this, R.layout.row_file, paths, nameFiles); 
    	
    	
    	listFrag.setListAdapter(adapter); 
    }
	
	
	/**
	 * Esta clase se usa para saber si un path (fichero o directorio)
	 * se va a mostrar en la lista del ListView, o sea, si es un
	 * directorio o un fichero con las extensiones requeridas
	 * 
	 * Se podria establecer cualquier otra condicion
	 */
	private static class ProjectFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {

			if (pathname.isDirectory() || extension == null) { // si es directorio o no se han declarado extensiones
				return true; // devuelve verdadero
			}

			// nombre del fichero en minusculas, para que reconozca extensiones
			String name = pathname.getName().toLowerCase();

			// se recorre la lista de extensiones requeridas
			for (String anExt : extension) {
				if (name.endsWith(anExt)) { // si en nombre termina con la extension...
					return true; //...devuelve verdadero
				}
			}
			return false;
		}
	}
	
	
	
	
	
//-----------------------------------------------SI PULSAMOS ALGUN ELEMENTO DE LA LISTA interfaz de SupporListFragment
	@Override
	public void onListItemSelected(int position) {
		
		//Para darle tiempo y que no pase por aqui en el attach (la primera vez)
		if (orientationChange) {
			orientationChange = false;
			return;
		}
		
		
	// crea un objeto File con el path del item del ListView pulsado
		File file = new File(paths.get(position));

		if (file.isDirectory()){ // si es un directorio
		
			if (file.canRead()) {// ES UN DIRECTORIO READ
				currentDir = paths.get(position);
				getDir(currentDir);
			} 
			else {  //ES UN DIRECTORIO NO-READ, mostramos un AlertDialog para informar

				new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("[" + file.getName() + "] folder can't be read!")
						.setPositiveButton("OK",new DialogInterface.OnClickListener() {
					
					@Override 
					public void onClick(DialogInterface dialog,int which) {}// este metodo no necesita hacer nada pero debe estar
				}).show();
			}
		} 
		else {
			// TODO dependiendo de la extension (tipo MIME)
			//si es una imagen puede abrirse
			//si es un audio o video reproducirse
			//...
		}
		
	}


}
