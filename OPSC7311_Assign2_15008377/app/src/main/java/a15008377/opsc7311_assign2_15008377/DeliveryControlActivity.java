/**
 * Author: Matthew Syrén
 *
 * Date:   19 May 2017
 *
 * Description: Class displays a report of all incomplete Deliveries
 */

package a15008377.opsc7311_assign2_15008377;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class DeliveryControlActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_delivery_control);

            //Sets the NavigationDrawer for the Activity and sets the selected item in the NavigationDrawer to Home
            super.onCreateDrawer();
            super.setSelectedNavItem(R.id.nav_delivery_control);

            //Sets the onKeyListener for the text_search_client, which will perform a search when the enter key is pressed
            final EditText txtSearchDelivery = (EditText) findViewById(R.id.text_search_delivery);
            txtSearchDelivery.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_ENTER){
                        String searchTerm = txtSearchDelivery.getText().toString();
                        searchDeliveries(searchTerm);

                        //Hides keyboard once search is completed
                        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        //Displays message to user
                        Toast.makeText(getApplicationContext(), "Search complete!", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    return false;
                }
            });
            populateViews();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Repopulates the views when the Activity is resumed
    @Override
    public void onResume(){
        try{
            super.onResume();
            populateViews();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method populates the views that are displayed on this Activity
    public void populateViews(){
        try{
            getIncompleteDeliveries();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method fetches the Deliveries that match the search result and send them to the displayDeliveries method
    public void searchDeliveries(String searchTerm){
        try{
            ArrayList<Delivery> lstSearchResults = Delivery.searchDeliveries(searchTerm, this, 0);
            displayDeliveries(lstSearchResults);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    //Method fetches all Deliveries and sends them to the displayDeliveries method
    public void getIncompleteDeliveries(){
        try{
            ArrayList<Delivery> lstDeliveries = Delivery.getDeliveries(this, 0);
            displayDeliveries(lstDeliveries);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    //Method populates the ListView on this Activity
    public void displayDeliveries(final ArrayList<Delivery> lstDeliveries){
        try{
            if(lstDeliveries.size() > 0){
                DBAdapter dbAdapter = new DBAdapter(this);
                dbAdapter.open();

                //Loops through Cursor and adds each Delivery item to the lstDeliveries ArrayList
                for(int i = 0; i < lstDeliveries.size(); i++) {
                    Cursor deliveryItems = dbAdapter.getDeliveryItems(lstDeliveries.get(i).getDeliveryID());
                    ArrayList<DeliveryItem> lstDeliveryItems = new ArrayList<>();
                    if(deliveryItems.moveToFirst()){
                        do{
                            DeliveryItem deliveryItem = new DeliveryItem(deliveryItems.getString(0), deliveryItems.getInt(1));
                            lstDeliveryItems.add(deliveryItem);
                        }while(deliveryItems.moveToNext());

                        //Sets the DeliveryItems for each Delivery
                        lstDeliveries.get(i).setLstDeliveryItems(lstDeliveryItems);
                    }
                }

                //Sets the Adapter for the list_view_deliveries ListView
                DeliveryReportListViewAdapter adapter = new DeliveryReportListViewAdapter(this, lstDeliveries);
                final ListView listView = (ListView) findViewById(R.id.list_view_deliveries);
                listView.setAdapter(adapter);

                //Sets OnItemClickListener, which will pass the information of the Delivery clicked to the DeliveryActivity
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(DeliveryControlActivity.this, DeliveryActivity.class);
                        intent.putExtra("action", "update");
                        intent.putExtra("deliveryObject", lstDeliveries.get(position));
                        startActivity(intent);
                    }
                });
                dbAdapter.close();
            }
            else{
                Toast.makeText(getApplicationContext(), "There are no currently no Deliveries added", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method takes the user to the DeliveryActivity
    public void addDeliveryOnClick(View view){
        try{
            Intent intent = new Intent(DeliveryControlActivity.this, DeliveryActivity.class);
            intent.putExtra("action", "add");
            startActivity(intent);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
