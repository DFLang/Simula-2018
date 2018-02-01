package simula.test.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Scanner;

public class JavaScanner
{

	public static void main(String[] args)
	{
		ScannerExample1();
		ScannerExample2();
		ScannerExample3();
		ScannerExample4();
		ScannerExample5();

	}


	static void ScannerExample1()
	{ try
	  {	FileReader reader = new FileReader(new File("D:\temp\test.txt"));
		Scanner scan = new Scanner(reader);
		while(scan.hasNextLine()){
				System.out.println(scan.nextLine());
		}
		scan.close();
	  } catch (FileNotFoundException e)
	    { System.out.println("Error Reading File");
	      e.printStackTrace();
		}
	}

	static void ScannerExample2()
	{
			
			try {
				File input = new File("D:\temp\test.txt");
				FileInputStream fis = new FileInputStream(input);
				Scanner scan = new Scanner(fis);
				while(scan.hasNextLine()){
					System.out.println(scan.nextLine());
				}
				scan.close();
			} catch (FileNotFoundException e) {
				System.out.println("Error Reading File");
				e.printStackTrace();
			}

		}


   static void ScannerExample3()
   { try
     { File input = new File("D:\temp\test.txt");
		Scanner scannner = new Scanner(input);
		while(scannner.hasNextLine()){
				System.out.println(scannner.nextLine());
		}
	    scannner.close();
	  } catch (FileNotFoundException e)
        { System.out.println("Error Reading File");
		  e.printStackTrace();
		}
	}

   static void ScannerExample4()
   {
		try {
			Path path = FileSystems.getDefault().getPath("access.log");
			System.out.println(path.toUri());
			Scanner scannner = new Scanner(path);
			while(scannner.hasNextLine()){
				System.out.println(scannner.nextLine());
			}
			scannner.close();
		} catch (IOException e) {
			System.out.println("Error Reading the path");
			e.printStackTrace();
		}

	
   }

   static void ScannerExample5()
   {
	String source = "This is a test";
	Scanner scannner = new Scanner(source);
	while (scannner.hasNext()) {
		System.out.println(scannner.next());
	}
	scannner.close();

}

}