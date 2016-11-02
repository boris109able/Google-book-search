package com.example.boris.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by boris on 8/25/2016.
 */
public class BookListAdapter extends ArrayAdapter<Book> {
    public BookListAdapter(Context context, List<Book> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View currentView = convertView;
        if (currentView == null) {
            currentView = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);
        }
        Book book = getItem(position);
        TextView textView = (TextView) currentView.findViewById(R.id.book_title);
        textView.setText(book.getTitle());
        textView = (TextView) currentView.findViewById(R.id.book_author);
        textView.setText(book.getAuthor());
        textView = (TextView) currentView.findViewById(R.id.book_publisheddate);
        textView.setText(book.getDate());
        return currentView;
    }
}
