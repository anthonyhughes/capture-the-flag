package com.anthony.ctf.nfc;

import com.anthony.ctf.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MessagesArrayAdapter extends ArrayAdapter<String> {

	private Context context;
	private String[] values;

	public MessagesArrayAdapter(Context context, String[] values) {
		super(context, R.layout.messages_list, values);
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.messages_list, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.message);
		textView.setText(values[pos]);
		
		return rowView;
	}

}
