package com.anthony.ctf.nfc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anthony.ctf.utilities.FileHelper;

import android.app.ListActivity;
import android.os.Bundle;

public class MessagesListActivity extends ListActivity {

    public String[] values = {};
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	readMessagesFromFile();
    	cleanValues();
    	setListAdapter(new MessagesArrayAdapter(this, values));
    }
    
    
    private void cleanValues() {
        final List<String> list =  new ArrayList<String>();
        Collections.addAll(list, values); 
        list.remove(list.size() - 1);
        values = list.toArray(new String[list.size()]);
	}


	private void readMessagesFromFile(){
    	FileInputStream inputStream;
		try {
			inputStream = openFileInput(FileHelper.FILENAME);
			StringBuffer buffer = new StringBuffer();
			byte[] bytes = new byte[1024];
			while(inputStream.read(bytes) != -1) {
				buffer.append(new String(bytes));
			}
			values = buffer.toString().split(",");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
}
