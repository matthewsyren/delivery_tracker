/**
 * Author: Matthew Syrén
 *
 * Date:   19 May 2017
 *
 * Description: Class provides a template for a Stock object
 */

package a15008377.opsc7311_assign2_15008377;

import android.content.Context;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import static android.content.Context.MODE_APPEND;


public class Stock implements Serializable{
    //Declarations
    private String stockID;
    private String stockDescription;
    private int stockQuantity;

    //Constructor
    public Stock(String stockID, String stockDescription, int stockQuantity){
        this.stockID = stockID;
        this.stockDescription = stockDescription;
        this.stockQuantity = stockQuantity;
    }

    public Stock(){}

    //Getter methods
    public String getStockID() {
        return stockID;
    }

    public String getStockDescription() {
        return stockDescription;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    //Setter methods
    public void setStockQuantity(int stockQuantity){
        this.stockQuantity = stockQuantity;
    }

    //Method returns true if the Stock object has valid values for all of its attributes, otherwise it returns false
    public boolean validateStock(Context context){
        boolean validStock = false;

        //If statements check numerous validation criteria for the Stock object.
        if(stockID.length() == 0){
            displayMessage("Please enter a Stock ID", context);
        }
        else if(stockID.contains("|")){
            displayMessage("Please remove all | symbols from the Stock ID", context);
        }
        else if(stockDescription.length() == 0){
            displayMessage("Please enter a Stock Description", context);
        }
        else if(stockDescription.contains("|")){
            displayMessage("Please remove all | symbols from the Stock Description", context);
        }
        else if(stockQuantity < 0){
            displayMessage("Please enter a Stock Quantity that is more than or equal to 0", context);
        }
        else{
            validStock = true;
        }
        return validStock;
    }

    //Method checks if the entered Stock ID has already been taken. The method returns true if it has been taken, and false if it hasn't been taken
    public boolean checkStockID(Context context) throws IOException {
        boolean stockIDTaken = false;

        File file = new File(context.getFilesDir(), "Stock.txt");
        if(!file.exists()){
            file.createNewFile();
        }

        ArrayList<Stock> lstStock = readStockItems(context);
        for(int i = 0; i < lstStock.size() && !stockIDTaken; i++){
            if(lstStock.get(i).getStockID().equals(stockID)){
                stockIDTaken = true;
                displayMessage(stockID + " has already been taken by another stock item, please choose another Stock ID", context);
            }
        }

        return stockIDTaken;
    }

    //Method returns an ArrayList of all Stock items in the Stock.txt text file
    public static ArrayList<Stock> readStockItems(Context context) throws IOException {
        BufferedReader bufferedReader = null;
        String line;
        ArrayList<Stock> lstStock = new ArrayList<Stock>();
        File file = new File(context.getFilesDir(), "Stock.txt");

        FileInputStream fileInputStream = context.openFileInput(file.getName());
        bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

        while((line = bufferedReader.readLine()) != null){
            String[] part = line.split("\\|");
            Stock stock = new Stock(part[0], part[1], Integer.parseInt(part[2]));
            lstStock.add(stock);
        }
        return lstStock;
    }

    //Method deletes a Stock item from the Stock.txt file
    public void deleteStockItem(String stockID, Context context) throws IOException{
        boolean foundStockID = false;

        ArrayList<Stock> lstStock = Stock.readStockItems(context);
        for(int i = 0; i < lstStock.size() && !foundStockID; i++) {
            if(lstStock.get(i).getStockID().equals(stockID)){
                lstStock.remove(i);
                i--;
                foundStockID = true;
            }
        }
        rewriteFile(lstStock, context);
        Toast.makeText(context, "Stock item successfully deleted", Toast.LENGTH_LONG).show();
    }

    //Method deletes the contents of the Stock.txt file and rewrites its content (used once a Stock item has been updated or deleted)
    public void rewriteFile(ArrayList<Stock> lstStock, Context context) throws IOException{
        //Clears contents of file
        File file = new File(context.getFilesDir(), "Stock.txt");
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();

        //Writes updated data to the Stock.txt text file
        FileOutputStream fileOutputStream = context.openFileOutput(file.getName(), MODE_APPEND);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        for(int i = 0; i < lstStock.size(); i++){
            outputStreamWriter.write(lstStock.get(i).getStockID() + "|" + lstStock.get(i).getStockDescription() + "|" + lstStock.get(i).getStockQuantity() + "\n");
        }
        outputStreamWriter.close();
    }

    //Method displays a Toast message with the message that is passed in as a parameter
    private void displayMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}