package com.vivswanshah.VCF;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class vcf {
    private static String inputFile;

    private static Path fileI;
    private static Path fileO;

    private static Pattern p1 = Pattern.compile("[^+0-9]", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static Pattern p2 = Pattern.compile("^[+][9][1]", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static Pattern p3 = Pattern.compile("^0", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    private static String line;
    private static Charset charset = Charset.forName("UTF-8");
    private static BufferedReader reader;
    private static BufferedWriter writer;

    private static void removeImages() throws IOException{
        fileO = Paths.get(inputFile + "_images_removed.vcf");
        System.out.println("BULIDING: " + inputFile + "_images_removed.vcf");
        reader = Files.newBufferedReader(fileI, charset);
        writer = Files.newBufferedWriter(fileO, charset);

        while ((line = reader.readLine()) != null) {
            if (line.contains("PHOTO;")){
                while ((line = reader.readLine()) != null) {
                    if (line.contains("END:VCARD")){
                        break;
                    }
                }
            }
            assert line != null;
            writer.write(line, 0, line.length());
            writer.newLine();
        }
        reader.close();
        writer.close();
        System.out.println("Successfully Done.");
    }

    private static void toExcel() throws IOException{
        fileO = Paths.get(inputFile + "_excel_format.txt");
        System.out.println("BULIDING: " + inputFile + "_images_removed.txt");
        reader = Files.newBufferedReader(fileI, charset);
        writer = Files.newBufferedWriter(fileO, charset);
        boolean c = false;

        while ((line = reader.readLine()) != null) {
            if (line.contains("BEGIN:VCARD")){
                c = false;
            }
            if (line.contains("FN:")){
                line = line.replace("FN:","").trim();
                writer.write(line, 0, line.length());
                if (line.length() > 0) {
                    c = true;
                }
            }
            if (line.contains("TEL;")){
                line = p3.matcher(p2.matcher(p1.matcher(line.replaceAll(" ","")).replaceAll("")).replaceAll("")).replaceAll("").trim();
                line = "\t" + line;
                writer.write(line, 0, line.length());
                c = true;
            }
            if(line.contains("END:VCARD") && c){
                writer.newLine();
            }
        }

        reader.close();
        writer.close();
        System.out.println("Successfully Done.");
    }

    private static void extractAllNumbers() throws IOException{
        fileO = Paths.get(inputFile + "_all_numbers.txt");
        System.out.println("BULIDING: " + inputFile + "_all_numbers.txt");
        reader = Files.newBufferedReader(fileI, charset);
        writer = Files.newBufferedWriter(fileO, charset);

        while ((line = reader.readLine()) != null) {
            if (line.contains("TEL;")){
                line = p3.matcher(p2.matcher(p1.matcher(line.replaceAll(" ","")).replaceAll("")).replaceAll("")).replaceAll("").trim();
                if(line.length() == 10){
                    writer.write(line, 0, line.length());
                    writer.newLine();
                }
            }
        }

        reader.close();
        writer.close();
        System.out.println("Successfully Done.");
    }

    private static void extractFirstNumbers() throws IOException{
        fileO = Paths.get(inputFile + "_selected_number.txt");
        System.out.println("BULIDING: " + inputFile + "_selected_number.txt");
        reader = Files.newBufferedReader(fileI, charset);
        writer = Files.newBufferedWriter(fileO, charset);
        boolean c = true;

        while ((line = reader.readLine()) != null) {
            if (line.contains("BEGIN:VCARD")){
                c = true;
            }
            if (line.contains("TEL;")){
                line = p3.matcher(p2.matcher(p1.matcher(line.replaceAll(" ","")).replaceAll("")).replaceAll("")).replaceAll("").trim();
                if(line.length() == 10 && c){
                    writer.write(line, 0, line.length());
                    c = false;
                    writer.newLine();
                }
            }
        }

        reader.close();
        writer.close();
        System.out.println("Successfully Done.");
    }

    public static void main(String args[]) {
        if(args.length > 0) {

            inputFile = args[0];

            File f = new File(inputFile);

            if (!f.exists() || f.isDirectory()) {
                System.out.println("File not exists!!!");
            } else {
                fileI = Paths.get(inputFile);

                if(inputFile.contains(".")) {
                    inputFile = inputFile.replace(inputFile.substring(inputFile.indexOf("."), inputFile.length()), "").trim();
                }

                int chooseFunction = -1;

                while(!(chooseFunction >= 0 && chooseFunction <= 5)) {
                    System.out.println("");
                    System.out.println(" 1 -> To reduce file by removing image from it.");
                    System.out.println(" 2 -> To convert inputFile --> excel type format.");
                    System.out.println(" 3 -> To extract one number from each contact.");
                    System.out.println(" 4 -> To extract all numbers.");
                    System.out.println(" 5 -> all the above");
                    System.out.println(" 0 -> Exit");
                    System.out.println("");
                    System.out.print("Enter a number between 0 and 5: ");

                    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
                    System.out.flush();
                    try {
                        chooseFunction = Integer.parseInt(stdin.readLine());
                    } catch (Exception e) {
                        chooseFunction = -1;
                    }

                    if (chooseFunction >= 0 && chooseFunction <= 5) {
                        boolean c = true;
                        try {
                            switch (chooseFunction) {
                                case 1:
                                    removeImages();
                                    break;
                                case 2:
                                    toExcel();
                                    break;
                                case 3:
                                    extractFirstNumbers();
                                    break;
                                case 4:
                                    extractAllNumbers();
                                    break;
                                case 5:
                                    removeImages();
                                    toExcel();
                                    extractFirstNumbers();
                                    extractAllNumbers();
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            c = false;
                        }

                        if (c) {
                            System.out.println("Exiting...");
                        } else {
                            System.out.println("Something occurred wrong!");
                            System.out.println("Error!!!");
                        }

                    } else {
                        System.out.println("Invalid Input");
                    }

                }
            }
        }else{
            System.out.println("Syntax error!!!");
            System.out.println("Correct syntax: java -jar vcf.jar <filename>");
        }
    }
}