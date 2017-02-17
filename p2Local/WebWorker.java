/**
* Web worker: an object of this class executes in its own new thread
* to receive and respond to a single HTTP request. After the constructor
* the object executes on its "run" method, and leaves when it is done.
*
* One WebWorker object is only responsible for one client connection. 
* This code uses Java threads to parallelize the handling of clients:
* each WebWorker runs in its own thread. This means that you can essentially
* just think about what is happening on one client at a time, ignoring 
* the fact that the entirety of the webserver execution might be handling
* other clients, too. 
*
* This WebWorker class (i.e., an object of this class) is where all the
* client interaction is done. The "run()" method is the beginning -- think
* of it as the "main()" for a client interaction. It does three things in
* a row, invoking three methods in this class: it reads the incoming HTTP
* request; it writes out an HTTP header to begin its response, and then it
* writes out some HTML content for the response content. HTTP requests and
* responses are just lines of text (in a very particular format). 
*
**/

import java.net.*;
import java.nio.file.Files;
import java.lang.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.*;
import java.text.*;

import javax.imageio.ImageIO;


public class WebWorker implements Runnable
{

private Socket socket;
private boolean fileExists = false;
private FileInputStream fis;
private String fileName;
private String fileType;
static String type;
private String path = "";
private String html = "";
/**
* Constructor: must have a valid open socket
**/
public WebWorker(Socket s)
{
   socket = s;
   
}

/**
* Worker thread starting point. Each worker handles just one HTTP 
* request and then returns, which destroys the thread. This method
* assumes that whoever created the worker created it with a valid
* open socket object.
**/
public void run()
{
  
   System.err.println("Handling connection...");
   try {
      InputStream  is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();
      
      String file;
      file = readHTTPRequest(is);     
      String[] a = file.split("/");
      String ex = a[a.length - 1];
      System.out.println(ex);
      System.out.println(ex.substring(ex.lastIndexOf(".")));
      System.out.println(ex);
      /* you need not to hard coded the content type and make it always “text/html” 
       * for any request. This is must be dynamically changed based on the type of the http 
       * request your server receives, which you can figure out by reading the extension of file 
       * coming with request. Thus, you should implement conditional if statement that choose either 
       * text/html, image/png, image/jpeg, or image/gif for the content type based on the file extension */
      if(ex.equals(".html"))
      writeHTTPHeader(os,"text/html", file);
      
      if(ex.equals(".png"))
      writeHTTPHeader(os,"image/png", file);
      
      if(ex.equals(".jpeg"))
      writeHTTPHeader(os,"image/jpeg", file);
      
      if(ex.equals(".gif"))
      writeHTTPHeader(os,"image/gif", file);
      
      /*
      
      writeContent(os, file);
      
      os.flush();
      socket.close();
      */
   } catch (Exception e) {
      System.err.println("Output error: "+e);
   }
   System.err.println("Done handling connection.");
   return;
}

/**
* Read the HTTP request header.
 * @return 
 * @throws IOException 
**/
private String readHTTPRequest(InputStream is) throws IOException
{
   String line;
   String location = "";
   BufferedReader r = new BufferedReader(new InputStreamReader(is));
   FileInputStream image = null;
   /***********************/
  /* File im = new File("sun.png");
   System.out.println("Attempting to read from file in: "+im.getCanonicalPath());
   System.out.println(im.exists());
   */
   /***********************/
   while (true) {
      
      try {
         while (!r.ready()) Thread.sleep(1);
         line = r.readLine(); 
         System.err.println("Request line: ("+line+")");
         String[] a = line.split(" ");
         location = a[1];
         System.out.println(location);
         break;
      } catch (Exception e) {
         System.err.println("Request error: "+e);
         break;
      }
   }
   return location;
}

/**
* Write the HTTP header lines to the client network connection.
* @param os is the OutputStream object to write to
* @param contentType is the string MIME content type (e.g. "text/html")
**/
private void writeHTTPHeader(OutputStream os, String contentType, String location) throws Exception
{
   Date d = new Date();
   DateFormat df = DateFormat.getDateTimeInstance();
   df.setTimeZone(TimeZone.getTimeZone("GMT"));
  
   File a = new File(location);
   if(a.exists() && !a.isDirectory()){
     os.write("HTTP/1.1 200 OK\n".getBytes());
     os.write("Date: ".getBytes());
     os.write((df.format(d)).getBytes());
     os.write("\n".getBytes());
     os.write("Server: Jon's very own server\n".getBytes());
     //os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
     //os.write("Content-Length: 438\n".getBytes()); 
     os.write("Connection: close\n".getBytes());
     os.write("Content-Type: ".getBytes());
     os.write(contentType.getBytes());
     os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
   }
   
   else{
     os.write("HTTP/1.1 404 Not Found\n".getBytes());
     os.write("Date: ".getBytes());
     os.write((df.format(d)).getBytes());
     os.write("\n".getBytes());
     os.write("Server: Jon's very own server\n".getBytes());
     //os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
     //os.write("Content-Length: 438\n".getBytes()); 
     os.write("Connection: close\n".getBytes());
     os.write("Content-Type: ".getBytes());
     os.write(contentType.getBytes());
     os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
   }
   return;
}

/**
* Write the data content to the client network connection. This MUST
* be done after the HTTP header has been written out.
* @param os is the OutputStream object to write to
**/
private void writeContent(OutputStream os, String local, String ex) throws Exception
{

    /* Also here. you should implement conditional if statement that check the 
     * content type either text/html, image/png, image/jpeg, or image/gif. So if it is text/html 
     * then use buferedreader to read the file and send it as response in bytes, but if not then use 
     * FileInputStream to read the image file and send it as byte. In both cases you will use Outputstream 
     * method to send them as byte. Now to figure out how these methods are used, go to oracle online documentation 
     * and read about them, also look at some examples.*/
      String line = "";
    
     local = local.substring(1);
      if(ex.equals(".html")){
    	  BufferedReader r = new BufferedReader(new FileReader(local));
    	  String str = "";
    	  While((line = r.readLine()) != null);{
    		  line += str + "\n";
    	  }
    	  os.write(line.getBytes());
      }
      else{
    	  FileInputStream stream = new FileInputStream(local);
    	  int x;
    	  while((x = stream.read()) != -1){
    		  os.write(x);
    	  }
      }
      File a = new File(local);

      //Determine if file exists at given location
      if(a.exists() && !a.isDirectory()){
         FileInputStream stream = new FileInputStream(local);
         BufferedReader r = new BufferedReader(new InputStreamReader(stream));

         String filex;
         //Reading file
         while ((filex = r.readLine()) != null){
            
            if(filex.equals("<cs371date>")){
               SimpleDateFormat dateForm = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
               Date specific = new Date();
               String finalDate = dateForm.format(specific);
               os.write(finalDate.getBytes()); 
            }
           
            if(filex.equals("<cs371server>")){
               os.write("Alec's Server.".getBytes()); 
            }
            os.write(filex.getBytes());
         }
         r.close();
      }
      //display "404 Error"
      else{
         os.write("<h3>Error: 404 not Found</h3>".getBytes());
      }
    }

private void While(boolean b) {
	// TODO Auto-generated method stub
	
}

private byte[] getBytes() throws IOException{
    File fi = new File(path);
    byte[] fileContent = Files.readAllBytes(fi.toPath());
    return fileContent;
}

private boolean checkFile(){
    File varTmpDir = new File(path);
    fileExists = varTmpDir.exists();
    //System.out.println("fileExists = " + fileExists);
    return fileExists;
}

private String fileToString(){
    String content = "";
   try {
       BufferedReader in = new BufferedReader(new FileReader(path));
       String str;
       while ((str = in.readLine()) != null) {
           content +=str;
       }
       in.close();
   } catch (IOException e) {

   }
    return content;
}



} // end class