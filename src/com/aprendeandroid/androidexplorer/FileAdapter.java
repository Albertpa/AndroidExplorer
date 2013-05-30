package com.aprendeandroid.androidexplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class FileAdapter extends ArrayAdapter<String>{

    Context context;
    int layoutResourceId;
    List<String> data = null;
    List<String> name = null;
    String imagePath = "";

    public FileAdapter(Context context, int layoutResourceId, List<String> data, List<String> name) {    	
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.name = name;
        
       
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        FileHolder holder = null; 
       
        if(row == null){ 
        	
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new FileHolder();            
          
            holder.imgFile = (ImageView) row.findViewById(R.id.imgFile);
            holder.txtPath = (TextView) row.findViewById(R.id.txtPath);
           
            row.setTag(holder);
        }
        else{
            holder = (FileHolder) row.getTag();
        }

        
        //holder.txtPath.setText(data.get(position));
        holder.txtPath.setText(name.get(position));
     // se obtiene la extension si la hay
        String FileName = data.get(position); 
        if(FileName.lastIndexOf(".") <0){
        	holder.imgFile.setImageResource(R.drawable.carpeta_icon);
        }else{
        	String ext = FileName.substring((FileName.lastIndexOf(".") + 1), FileName.length());
        	ext = ext.toLowerCase();
        	holder.imgFile.setImageResource(R.drawable.file_icon);
        	//Log.i("PracticaIntents", ext );
        	
        	
        	//logica de imagen en miniatura
        	
        	String[] extP = this.context.getResources().getStringArray(R.array.extensionesPermitidas);
        	//R.array.extensionesPermitidas
        	
        	//si es una de las permitidas
        	
        	if(contiene(extP, ext)){
        					
				Bitmap cameraPic =  scaleBitmap(data.get(position), 125);									
				//fin high resolution				
				if(cameraPic != null){
					holder.imgFile.setImageBitmap(cameraPic);
				}
        	}
        	
        }
		
		
		/*
		String imageName = data.get(position).getAvatar();

		// parche para compatibilidad con version antigua servidor
		// puede servir para los que no han puesto avatar
		if(Character.isDigit(avatarName.charAt(0))) { // si empieza por un numero
			avatarName = "avatar" + avatarName + ".jpg"; // creamos el nombre del fichero
		}
		
		Drawable drawable;
		try { // leemos el fichero assets/avatares/avatarX.jpg con open("avatares/avatarX.jpg")
			// context.getAssets() devuelve un AssetsManager
			drawable = Drawable.createFromStream(context.getAssets().open(avataresPath + "/" + avatarName), null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			drawable = context.getResources().getDrawable(R.drawable.no_avatar);
		}
        
        holder.imgAvatar.setImageDrawable(drawable);        
//        holder.imgAvatar.setImageResource(data.get(position).getAvatar()); 
        
        */

        return row;
    }

   
    static class FileHolder{
        ImageView imgFile;
        TextView txtPath;       
    }
    
    private boolean contiene(String[] str, String busca) {
  		boolean esta = false;
    	for(int i =0; i< str.length; i++){
  			if(str[i].contains(busca)){
  				esta=true;
  			}
  		}
		return esta;
    }
  //metodo de rescalado
  	private Bitmap scaleBitmap(String image_path, int maxDimension) {
  		Bitmap scaledBitmap;
  		
  		BitmapFactory.Options op = new BitmapFactory.Options();
  		op.inJustDecodeBounds = true; // solo devuelve las dimensiones, no carga bitmap
  		scaledBitmap = BitmapFactory.decodeFile(image_path, op); //en op están las dimensiones

  		// usamos Math.max porque es mejor que la imagen sea un poco mayor que el
  		// control donde se muestra, que un poco menor. Ya que si es menor el control
  		// la agranda para ajustarla y se podria pixelar un poco.
  		if ((maxDimension < op.outHeight) || (maxDimension < op.outWidth)) {
  			// cada dimensión de la imagen se dividir por op.inSampleSize al cargar
  			op.inSampleSize = Math.round(Math.max((float) op.outHeight / (float) maxDimension,(float) op.outWidth / (float) maxDimension)); //calculamos la proporcion de la escala para que no deforme la imagen y entre en las dimensiones fijadas en la vista
  		}

  		op.inJustDecodeBounds = false; // ponemos a false op...
  		scaledBitmap = BitmapFactory.decodeFile(image_path, op); //...para que ya el bitmap se cargue realmente
  		
  		return scaledBitmap;
  	}
    
    
}
