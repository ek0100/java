//Chapter 1 Pg. 42 1.2.3
//January 21, 2014

//String.split() version

/* To Do

   Right Align Cells...create cells JTable
   Image for printout??
   Custom file creation

*/

import static java.nio.file.StandardOpenOption.*;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
//import javax.swing.JPanel;
import javax.swing.JTable; //table only
import java.nio.file.*; //File Access
import java.util.*; //Scanner & Random
import java.io.*;
import java.awt.Desktop;


public class BranchesReg
{
   static final int NOBRANCHES = 20;
   static final int NOITEMS = 10;
   static final int MAXVAL = 100000;
   static boolean firstRep = true; //reduce dialog boxes on additional reps
   
   public static void main(String[] args)
   {
      String inputFile = "branchesInputFile.csv";
      boolean tryAgain;
      
      do{
      
         try{
               double[][] processArray = processor(reader(inputFile));      
               display(processArray);
               fileSmasher(inputFile);  
         
         }catch(IOException e){
               fileCreator();
         }
      
      }while(true);
   
   }//end main method  
    
   
   
   public static String[][] reader(String inputFile) throws IOException{
     
     int count = 0;
     int lineCount = 0;

         //opens the file & counts lines for the input array
         Scanner countFile = new Scanner(new File(inputFile));
          
         while(countFile.hasNextLine()){
            lineCount++;
            countFile.nextLine();  
         }
         countFile.close();
        
         String[][] input =  new String[lineCount][];
             
         Scanner inFile = new Scanner(new File(inputFile));
         while(inFile.hasNextLine()){                                            
            input[count] = (inFile.nextLine().split(","));
            count++;
         }  
         inFile.close();     
      return input;
   }//end reader method
   
   public static double[][] processor(String[][] input){
      
      /* this only holds totals for each store + footer row + total column currently 21x11 */
      
      double [][] processArray = new double[NOBRANCHES+1][NOITEMS+1];   
      int branch = 0, item = 0, errorCount = 0;
      double currentSale = 0.0;
      StringBuilder errors = new StringBuilder();
      errors.append("Errors found in input file:\n");
      
      for(int i=1;i<input.length;i++){//row counter
        try{
            branch = Integer.parseInt(input[i][0]);
               if(branch<1||branch>NOBRANCHES)
                  throw new NumberFormatException();
        }
        catch(NumberFormatException m){
            errorCount++;
            errors.append("\""+input[i][0]+"\" caused a Branch Input Error on Row "+(i+1)+"\n");
            continue;    
        }
        
         
         for(int j=1;j<input[i].length;j++){//column counter
            if(j%2==1){//Odd Positions Are Item Numbers
               try{
                  item = Integer.parseInt(input[i][j]);
                     if(item<1||item>NOITEMS)
                        throw new NumberFormatException();
               }
               catch(NumberFormatException m){
                  errorCount++;
                  errors.append("\""+input[i][j]+"\" caused an Item Input Error on Row "+(i+1)+", Column "+(j+1)+"\n");
                  j++; //skips the next column after a bad item number
                  continue;
               }    
            }//end item number if
            
            if(j%2==0){//Even Positions Are Sales
               try{
                  currentSale = Double.parseDouble(input[i][j]);
                     if(currentSale>MAXVAL)
                        throw new NumberFormatException();
                  
                  processArray[branch-1][item-1] += currentSale; //total in column
                  processArray[branch-1][NOITEMS] += currentSale; //total for branch
                  processArray[NOBRANCHES][item-1] += currentSale; //total for item
                  processArray[NOBRANCHES][NOITEMS] += currentSale; //grand total
               }
               catch(NumberFormatException m){
                  errorCount++;
                  errors.append("\""+input[i][j]+"\" caused a Sales Input For Item: "+item+" on row "+(i+1)+", column "+(j+1)+"\n");
                  continue;
               }   
            }//end sales if
         }//end column counter
      }//end row counter

      if(errorCount!=0) //display before end of process method
         JOptionPane.showMessageDialog(null,errors,"Format Errors found in Input File",JOptionPane.WARNING_MESSAGE);  

      return processArray;
   
   }//end processor method
   
   public static void display(double[][] input){
      DecimalFormat df = new DecimalFormat("#.##");
      String outputText = "Branch"+ "\t";
       
      for(int i=1;i<NOITEMS+1;i++)
         outputText += "Item "+i+ "\t"; 
         
      outputText += "Totals\n\n";
     
      for(int i=0;i<NOBRANCHES+1;i++){
         for(int j=0;j<NOITEMS+1;j++){
            if(j==0){
               if(i==NOBRANCHES)
                  outputText += "Totals\t";
               else
                  outputText+= df.format(i+1)+"\t";
            }  
            outputText+= df.format(input[i][j])+"\t";
         }
         outputText+="\n";
      }
     
      JTextArea output = new JTextArea(outputText);
      output.setEditable(false);
      
      JOptionPane.showMessageDialog(null,output,"Company Sales Table Output",JOptionPane.PLAIN_MESSAGE);

   }//end display method
   
   public static void fileCreator(){
        
        try{
        
            String pather = System.getProperty("user.dir")+"\\branchesInputFile.csv";
            Path path = Paths.get(pather);

            if(firstRep==true){
        
               Object[] options = {"Yes, please","Quit"};
               int n = JOptionPane.showOptionDialog(null,"Would you like me to generate a new input file?",
               "No File to Read",
               JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE,null,options,options[0]); 
      
               if(n == 1)
                  System.exit(0);

               JOptionPane.showMessageDialog(null,"I will attempt to create the file in the following location: \n "+pather+
               "\nThe file can be opened/edited with Notepad or Excel",
               "Here we go...",JOptionPane.PLAIN_MESSAGE);
            }  
          
            StringBuilder outputString = new StringBuilder();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(Files.newOutputStream(path, CREATE,APPEND))));
            String del = ",";
            int itemNumber = 0;
            writer.append("Store # on left, Item Number, Sales, Item Number, Sales, etc...order of items/branches doesn't matter. This line is ignored during input");
            writer.newLine();
            Random r = new Random();
            DecimalFormat df = new DecimalFormat("#.##");

            for(int i=1;i<NOBRANCHES+1;i++){ 
               outputString.append(i);
            
               for(int j=0;j<(int)Math.round((35) * Math.random());j++){
                  do{
                     itemNumber = (int)Math.round((10) * Math.random());
                  }while(itemNumber<1||itemNumber>NOITEMS);            
                                  
                  outputString.append(del+itemNumber+del+df.format(5000* r.nextDouble()));
               }
               writer.append(outputString);
               outputString.setLength(0);
               writer.newLine();
               if (itemNumber<9)
                  i--; //randomly making this process & resulting input file longer 
            }
            writer.flush();
            writer.close(); 

            
         }
        catch(Exception e){
            JOptionPane.showMessageDialog(null,"File Creation Failure");
            e.printStackTrace();
            System.exit(1);
        }
   }//end fileCreator method
   
   
   public static void fileSmasher(String inputFile){
   
      Object[] options = {"Yes, please","Quit"};
      StringBuilder outputString = new StringBuilder();
      int n = JOptionPane.showOptionDialog(null,"Would you like to delete the input file and start over?",
      "Try Again?",
      JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]); 
      
      if(n == 1)
         System.exit(0);
         
      try{
         File file = new File(inputFile);
      
         if(file.delete() && firstRep==true)
            JOptionPane.showMessageDialog(null,"File delete successful, starting over...");
         
         firstRep = false;
      }
      catch(Exception e){
         JOptionPane.showMessageDialog(null,"File Deletion Failure");
         e.printStackTrace();
         System.exit(1);
      }   
   }//end fileSmasher
   
   public static void tableDisplay(String[][] input){
   
   
     
   
   
   
   
   
   }
   
}//end Class